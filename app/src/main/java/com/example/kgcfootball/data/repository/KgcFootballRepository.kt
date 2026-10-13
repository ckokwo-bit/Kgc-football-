package com.example.kgcfootball.data.repository

import com.example.kgcfootball.data.db.AppDatabase
import com.example.kgcfootball.data.model.*
import com.example.kgcfootball.engine.MatchEngine
import com.example.kgcfootball.engine.OddsEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import java.util.UUID

class KgcFootballRepository(private val db: AppDatabase) {

    val allLeagues: Flow<List<LeagueEntity>> = db.leagueDao().getAllLeagues()
    val allTeams: Flow<List<TeamEntity>> = db.teamDao().getAllTeams()
    val userWallet: Flow<UserWalletEntity?> = db.userWalletDao().getWalletFlow()
    val allTickets: Flow<List<BetTicketEntity>> = db.betDao().getAllTickets()
    val topScorers: Flow<List<PlayerEntity>> = db.playerDao().getTopScorers()
    val allAwards: Flow<List<SeasonAwardEntity>> = db.seasonAwardDao().getAllAwards()
    val liveFixtures: Flow<List<MatchFixtureEntity>> = db.matchDao().getLiveFixtures()

    private val _newArticleFlow = MutableSharedFlow<NewsArticle>(extraBufferCapacity = 20)
    val newArticleFlow = _newArticleFlow.asSharedFlow()

    private val _winningTicketFlow = MutableSharedFlow<BetTicketEntity>(extraBufferCapacity = 5)
    val winningTicketFlow = _winningTicketFlow.asSharedFlow()

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        val count = db.matchDao().getFixtureCount()
        if (count < 20) {
            db.leagueDao().insertLeagues(com.example.kgcfootball.data.db.DatabaseSeeder.getInitialLeagues())
            db.teamDao().insertTeams(com.example.kgcfootball.data.db.DatabaseSeeder.getInitialTeams())
            db.playerDao().insertPlayers(com.example.kgcfootball.data.db.DatabaseSeeder.getInitialPlayers())
            db.matchDao().insertFixtures(com.example.kgcfootball.data.db.DatabaseSeeder.getInitialFixtures())
            if (db.userWalletDao().getWalletSync() == null) {
                db.userWalletDao().insertWallet(
                    UserWalletEntity(
                        id = 1,
                        balance = 25000.0,
                        currencySymbol = "KGC"
                    )
                )
            }
        }
    }

    fun getStandings(leagueId: String): Flow<List<TeamEntity>> {
        return db.teamDao().getStandingsByLeague(leagueId)
    }

    fun getFixtures(leagueId: String, round: Int): Flow<List<MatchFixtureEntity>> {
        return db.matchDao().getFixturesByRound(leagueId, round)
    }

    fun getFixtureFlow(id: String): Flow<MatchFixtureEntity?> {
        return db.matchDao().getFixtureFlow(id)
    }

    fun getMatchEvents(matchId: String): Flow<List<MatchEventEntity>> {
        return db.matchDao().getEventsByMatch(matchId)
    }

    fun getBetItemsForTicket(ticketId: String): Flow<List<BetItemEntity>> {
        return db.betDao().getItemsForTicket(ticketId)
    }

    suspend fun getTeamById(id: String): TeamEntity? {
        return db.teamDao().getTeamById(id)
    }

    suspend fun getPlayersForTeam(teamId: String): List<PlayerEntity> {
        return db.playerDao().getPlayersByTeamSync(teamId)
    }

    /**
     * Valide et enregistre un coupon de paris (Simple ou Combiné).
     */
    suspend fun placeBetTicket(
        stake: Double,
        selections: List<BetItemEntity>
    ): Result<String> = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync()
            ?: return@withContext Result.failure(Exception("Portefeuille introuvable"))

        if (wallet.balance < stake) {
            return@withContext Result.failure(Exception("Solde virtuel insuffisant ($stake > ${wallet.balance})"))
        }

        if (selections.isEmpty()) {
            return@withContext Result.failure(Exception("Aucune sélection dans le coupon"))
        }

        val boostMultiplier = com.example.kgcfootball.engine.OddsEngine.getComboBoostMultiplier(selections.size)
        var combinedOdds = 1.0
        selections.forEach { combinedOdds *= it.odds }
        val potentialWin = (stake * combinedOdds * boostMultiplier)

        val ticketId = "TKT-" + UUID.randomUUID().toString().take(8).uppercase()
        val isCombine = selections.size > 1

        val ticket = BetTicketEntity(
            id = ticketId,
            placedAt = System.currentTimeMillis(),
            stake = stake,
            totalOdds = Math.round(combinedOdds * 100) / 100.0,
            potentialWin = Math.round(potentialWin * 100) / 100.0,
            status = "PENDING",
            type = if (isCombine) "COMBINE" else "SIMPLE",
            currentCashoutValue = Math.round((stake * 0.94) * 100) / 100.0
        )

        val itemsToSave = selections.map { it.copy(id = UUID.randomUUID().toString(), ticketId = ticketId) }

        val updatedWallet = wallet.copy(
            balance = wallet.balance - stake,
            totalBetsPlaced = wallet.totalBetsPlaced + 1
        )

        db.userWalletDao().updateWallet(updatedWallet)
        db.betDao().insertTicket(ticket)
        db.betDao().insertBetItems(itemsToSave)

        Result.success(ticketId)
    }

    /**
     * Exécute un Cash Out réaliste en direct.
     */
    suspend fun performCashout(ticketId: String): Result<Double> = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync()
            ?: return@withContext Result.failure(Exception("Portefeuille introuvable"))

        val pendingTickets = db.betDao().getPendingTicketsSync()
        val ticket = pendingTickets.firstOrNull { it.id == ticketId }
            ?: return@withContext Result.failure(Exception("Ticket non éligible au Cash Out ou déjà réglé"))

        val items = db.betDao().getItemsForTicketSync(ticket.id)
        if (items.isEmpty()) {
            return@withContext Result.failure(Exception("Aucune sélection trouvée"))
        }

        val allTeams = db.teamDao().getAllTeamsSync()
        val selectionPairs = items.map { item ->
            val fixture = db.matchDao().getFixtureById(item.matchId)
            val liveOdds = if (fixture != null) {
                val home = allTeams.firstOrNull { it.id == fixture.homeTeamId }
                val away = allTeams.firstOrNull { it.id == fixture.awayTeamId }
                if (home != null && away != null) {
                    val marketOdds = if (fixture.status == "SCHEDULED") {
                        OddsEngine.calculatePreMatchOdds(home, away)
                    } else {
                        OddsEngine.calculateLiveOdds(
                            home, away, fixture.minute, fixture.homeScore, fixture.awayScore,
                            fixture.homeRedCards, fixture.awayRedCards
                        )
                    }
                    when (item.selectionKey) {
                        "1" -> marketOdds.homeWin
                        "X" -> marketOdds.draw
                        "2" -> marketOdds.awayWin
                        "OVER" -> marketOdds.over25
                        "UNDER" -> marketOdds.under25
                        "1X" -> marketOdds.doubleChance1X
                        "12" -> marketOdds.doubleChance12
                        "X2" -> marketOdds.doubleChanceX2
                        "YES" -> marketOdds.bttsYes
                        "NO" -> marketOdds.bttsNo
                        else -> marketOdds.exactScores[item.selectionKey] ?: item.odds
                    }
                } else item.odds
            } else item.odds

            Pair(item, liveOdds)
        }

        val cashoutAmount = OddsEngine.calculateLiveCashout(
            stake = ticket.stake,
            potentialWin = ticket.potentialWin,
            selections = selectionPairs
        )

        if (cashoutAmount <= 0) {
            return@withContext Result.failure(Exception("Le Cash Out n'est pas disponible actuellement pour ce pari"))
        }

        // Marque le ticket comme encaissé (CASHED_OUT)
        db.betDao().updateTicket(
            ticket.copy(
                status = "CASHED_OUT",
                settledAt = System.currentTimeMillis(),
                cashoutAmount = cashoutAmount,
                currentCashoutValue = cashoutAmount
            )
        )

        // Crédite le montant du Cash Out au portefeuille
        val netGain = cashoutAmount - ticket.stake
        db.userWalletDao().updateWallet(
            wallet.copy(
                balance = wallet.balance + cashoutAmount,
                totalProfit = wallet.totalProfit + netGain
            )
        )

        Result.success(cashoutAmount)
    }

    /**
     * Lance le coup d'envoi immédiat d'un match sans attendre la fin du compte à rebours.
     */
    suspend fun launchMatchImmediately(matchId: String) = withContext(Dispatchers.IO) {
        val fixture = db.matchDao().getFixtureById(matchId) ?: return@withContext
        if (fixture.status != "FINISHED") {
            val updated = fixture.copy(
                status = "LIVE",
                minute = 1.coerceAtLeast(fixture.minute),
                countdownSecondsRemaining = 0,
                lastEventText = "Coup d'envoi du match ! Le ballon roule."
            )
            db.matchDao().updateFixture(updated)
        }
    }

    suspend fun updateSimulationStyle(style: String) = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync() ?: return@withContext
        db.userWalletDao().updateWallet(wallet.copy(simulationStyle = style))
    }

    /**
     * Rattrapage hors-ligne et moteur de simulation :
     * - En mode TEMPS_REEL : calcule la progression selon les minutes réelles de l'horloge système
     *   même si l'application a été fermée pendant 30 minutes ou 3 heures !
     * - En mode VIRTUEL_EXPRESS et VIRTUEL_MOYEN : progression cadencée.
     */
    suspend fun tickStaggeredSimulation(simulationStyle: String = "VIRTUEL_EXPRESS") = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()

        if (simulationStyle == "TEMPS_REEL") {
            val scheduledMatches = db.matchDao().getAllScheduledFixturesSync()
            for (f in scheduledMatches) {
                val kickoff = if (f.kickoffTimestamp > 0L) f.kickoffTimestamp else (now + f.countdownSecondsRemaining * 1000L)
                if (now >= kickoff) {
                    val elapsedMinutes = ((now - kickoff) / 60_000L).toInt().coerceAtLeast(1)
                    if (elapsedMinutes >= 90) {
                        simulateMatchInstantly(f.id)
                    } else {
                        val started = f.copy(
                            status = "LIVE",
                            minute = 1,
                            countdownSecondsRemaining = 0,
                            kickoffTimestamp = kickoff,
                            lastSimulatedTimestamp = now,
                            lastEventText = "Coup d'envoi en temps réel ! Le match bat son plein."
                        )
                        db.matchDao().updateFixture(started)
                        for (m in 1 until elapsedMinutes) {
                            val updated = tickLiveMatch(f.id)
                            if (updated == null || updated.status == "FINISHED") break
                        }
                    }
                } else {
                    val remainingSeconds = ((kickoff - now) / 1000L).toInt().coerceAtLeast(0)
                    db.matchDao().updateFixture(f.copy(countdownSecondsRemaining = remainingSeconds))
                }
            }

            val liveMatches = db.matchDao().getLiveFixturesSync()
            for (f in liveMatches) {
                val lastSim = if (f.lastSimulatedTimestamp > 0L) f.lastSimulatedTimestamp else (now - 60_000L)
                val elapsedMinutes = ((now - lastSim) / 60_000L).toInt()
                if (elapsedMinutes >= 1) {
                    for (m in 0 until elapsedMinutes.coerceAtMost(10)) {
                        val updated = tickLiveMatch(f.id)
                        if (updated == null || updated.status == "FINISHED") break
                    }
                    val currentF = db.matchDao().getFixtureById(f.id)
                    if (currentF != null) {
                        db.matchDao().updateFixture(currentF.copy(lastSimulatedTimestamp = now))
                    }
                }
            }
        } else {
            // 1. Décrémentation des comptes à rebours des matchs SCHEDULED
            val scheduledMatches = db.matchDao().getAllScheduledFixturesSync()
            for (f in scheduledMatches) {
                val newCountdown = f.countdownSecondsRemaining - 1
                if (newCountdown <= 0) {
                    val started = f.copy(
                        status = "LIVE",
                        minute = 1,
                        countdownSecondsRemaining = 0,
                        lastSimulatedTimestamp = now,
                        lastEventText = "Coup d'envoi ! Les 22 acteurs sont sur la pelouse."
                    )
                    db.matchDao().updateFixture(started)
                } else {
                    db.matchDao().updateFixture(f.copy(countdownSecondsRemaining = newCountdown))
                }
            }

            // 2. Évolution des matchs en direct (LIVE)
            val liveMatches = db.matchDao().getLiveFixturesSync()
            for (f in liveMatches) {
                tickLiveMatch(f.id)
            }
        }
    }

    /**
     * Simule la minute suivante de TOUS les matchs en direct d'une journée.
     */
    suspend fun tickAllMatchesInRound(leagueId: String, round: Int) = withContext(Dispatchers.IO) {
        val fixtures = db.matchDao().getFixturesByRoundSync(leagueId, round)
        for (f in fixtures) {
            if (f.status != "FINISHED") {
                if (f.status == "SCHEDULED") {
                    launchMatchImmediately(f.id)
                }
                tickLiveMatch(f.id)
            }
        }
    }

    /**
     * Simule la minute suivante d'un match en direct et persiste les données.
     */
    suspend fun tickLiveMatch(matchId: String): MatchFixtureEntity? = withContext(Dispatchers.IO) {
        val fixture = db.matchDao().getFixtureById(matchId) ?: return@withContext null
        if (fixture.status == "FINISHED") return@withContext fixture

        val homeTeam = db.teamDao().getTeamById(fixture.homeTeamId) ?: return@withContext fixture
        val awayTeam = db.teamDao().getTeamById(fixture.awayTeamId) ?: return@withContext fixture
        val homePlayers = db.playerDao().getPlayersByTeamSync(homeTeam.id)
        val awayPlayers = db.playerDao().getPlayersByTeamSync(awayTeam.id)

        val tickResult = MatchEngine.simulateMinute(
            fixture,
            homeTeam,
            awayTeam,
            homePlayers,
            awayPlayers
        )

        db.matchDao().updateFixture(tickResult.updatedFixture)
        if (tickResult.newEvents.isNotEmpty()) {
            db.matchDao().insertEvents(tickResult.newEvents)
        }

        if (tickResult.updatedFixture.status == "FINISHED") {
            onMatchFinished(tickResult.updatedFixture, homeTeam, awayTeam)
        }

        tickResult.updatedFixture
    }

    /**
     * Simule l'ensemble d'un match immédiatement.
     */
    suspend fun simulateMatchInstantly(matchId: String) = withContext(Dispatchers.IO) {
        val fixture = db.matchDao().getFixtureById(matchId) ?: return@withContext
        if (fixture.status == "FINISHED") return@withContext

        val homeTeam = db.teamDao().getTeamById(fixture.homeTeamId) ?: return@withContext
        val awayTeam = db.teamDao().getTeamById(fixture.awayTeamId) ?: return@withContext
        val homePlayers = db.playerDao().getPlayersByTeamSync(homeTeam.id)
        val awayPlayers = db.playerDao().getPlayersByTeamSync(awayTeam.id)

        val result = MatchEngine.simulateFullMatch(
            fixture,
            homeTeam,
            awayTeam,
            homePlayers,
            awayPlayers
        )

        db.matchDao().updateFixture(result.updatedFixture)
        if (result.newEvents.isNotEmpty()) {
            db.matchDao().insertEvents(result.newEvents)
        }

        onMatchFinished(result.updatedFixture, homeTeam, awayTeam)
    }

    /**
     * Simule tous les matchs programmés d'une journée.
     */
    suspend fun simulateRoundFixtures(leagueId: String, round: Int) = withContext(Dispatchers.IO) {
        val fixtures = db.matchDao().getFixturesByRoundSync(leagueId, round)
        for (f in fixtures) {
            if (f.status != "FINISHED") {
                simulateMatchInstantly(f.id)
            }
        }
    }

    /**
     * S'assure que les 4 matchs d'une journée existent dans la base de données.
     * Si la liste est vide, génère immédiatement les rencontres du tournoi.
     */
    suspend fun ensureRoundFixturesExist(leagueId: String, round: Int) = withContext(Dispatchers.IO) {
        val list = db.matchDao().getFixturesByRoundSync(leagueId, round)
        if (list.isEmpty()) {
            val teams = db.teamDao().getTeamsByLeagueSync(leagueId)
            if (teams.size == 8) {
                val newFixtures = com.example.kgcfootball.data.db.DatabaseSeeder.generateLeagueSchedule(
                    leagueId = leagueId,
                    teams = teams.map { it.id }
                ).filter { it.round == round }
                db.matchDao().insertFixtures(newFixtures)
            }
        }
    }

    /**
     * Traite les impacts du match terminé : classement + paris.
     */
    private suspend fun onMatchFinished(
        fixture: MatchFixtureEntity,
        homeTeam: TeamEntity,
        awayTeam: TeamEntity
    ) {
        val homeWin = fixture.homeScore > fixture.awayScore
        val draw = fixture.homeScore == fixture.awayScore
        val awayWin = fixture.homeScore < fixture.awayScore

        val updatedHome = homeTeam.copy(
            played = homeTeam.played + 1,
            won = homeTeam.won + if (homeWin) 1 else 0,
            drawn = homeTeam.drawn + if (draw) 1 else 0,
            lost = homeTeam.lost + if (awayWin) 1 else 0,
            goalsFor = homeTeam.goalsFor + fixture.homeScore,
            goalsAgainst = homeTeam.goalsAgainst + fixture.awayScore,
            points = homeTeam.points + if (homeWin) 3 else if (draw) 1 else 0,
            formString = (if (homeWin) "V," else if (draw) "N," else "D,") + homeTeam.formString.take(8)
        )

        val updatedAway = awayTeam.copy(
            played = awayTeam.played + 1,
            won = awayTeam.won + if (awayWin) 1 else 0,
            drawn = awayTeam.drawn + if (draw) 1 else 0,
            lost = awayTeam.lost + if (homeWin) 1 else 0,
            goalsFor = awayTeam.goalsFor + fixture.awayScore,
            goalsAgainst = awayTeam.goalsAgainst + fixture.homeScore,
            points = awayTeam.points + if (awayWin) 3 else if (draw) 1 else 0,
            formString = (if (awayWin) "V," else if (draw) "N," else "D,") + awayTeam.formString.take(8)
        )

        db.teamDao().updateTeam(updatedHome)
        db.teamDao().updateTeam(updatedAway)

        val article = com.example.kgcfootball.data.model.NewsProvider.generatePostMatchArticle(
            fixture = fixture,
            homeTeam = updatedHome,
            awayTeam = updatedAway,
            topScorerName = null
        )
        _newArticleFlow.tryEmit(article)

        settleBetsForMatch(fixture)
    }

    private suspend fun settleBetsForMatch(fixture: MatchFixtureEntity) {
        val affectedItems = db.betDao().getItemsForMatchSync(fixture.id)
        if (affectedItems.isEmpty()) return

        for (item in affectedItems) {
            val isWon = evaluateSelection(item, fixture)
            val updatedStatus = if (isWon) "WON" else "LOST"
            db.betDao().updateBetItem(item.copy(status = updatedStatus))
        }

        val pendingTickets = db.betDao().getPendingTicketsSync()
        var wallet = db.userWalletDao().getWalletSync() ?: return

        for (ticket in pendingTickets) {
            val items = db.betDao().getItemsForTicketSync(ticket.id)
            val hasLost = items.any { it.status == "LOST" }
            val allWon = items.all { it.status == "WON" }

            if (hasLost) {
                db.betDao().updateTicket(
                    ticket.copy(
                        status = "LOST",
                        settledAt = System.currentTimeMillis()
                    )
                )
                wallet = wallet.copy(
                    totalLostBets = wallet.totalLostBets + 1,
                    totalProfit = wallet.totalProfit - ticket.stake
                )
            } else if (allWon) {
                val wonTicket = ticket.copy(
                    status = "WON",
                    settledAt = System.currentTimeMillis()
                )
                db.betDao().updateTicket(wonTicket)
                _winningTicketFlow.tryEmit(wonTicket)
                val netProfit = ticket.potentialWin - ticket.stake
                wallet = wallet.copy(
                    balance = wallet.balance + ticket.potentialWin,
                    totalWonBets = wallet.totalWonBets + 1,
                    totalProfit = wallet.totalProfit + netProfit,
                    biggestWin = maxOf(wallet.biggestWin, ticket.potentialWin)
                )
            }
        }

        db.userWalletDao().updateWallet(wallet)
    }

    private fun evaluateSelection(item: BetItemEntity, fixture: MatchFixtureEntity): Boolean {
        val homeScore = fixture.homeScore
        val awayScore = fixture.awayScore

        return when (item.marketType) {
            "1X2" -> when (item.selectionKey) {
                "1" -> homeScore > awayScore
                "X" -> homeScore == awayScore
                "2" -> homeScore < awayScore
                else -> false
            }
            "DOUBLE_CHANCE" -> when (item.selectionKey) {
                "1X" -> homeScore >= awayScore
                "12" -> homeScore != awayScore
                "X2" -> awayScore >= homeScore
                else -> false
            }
            "OVER_UNDER_1_5" -> when (item.selectionKey) {
                "OVER" -> (homeScore + awayScore) > 1
                "UNDER" -> (homeScore + awayScore) <= 1
                else -> false
            }
            "OVER_UNDER_2_5" -> when (item.selectionKey) {
                "OVER" -> (homeScore + awayScore) > 2
                "UNDER" -> (homeScore + awayScore) <= 2
                else -> false
            }
            "OVER_UNDER_3_5" -> when (item.selectionKey) {
                "OVER" -> (homeScore + awayScore) > 3
                "UNDER" -> (homeScore + awayScore) <= 3
                else -> false
            }
            "BTTS" -> when (item.selectionKey) {
                "YES" -> homeScore > 0 && awayScore > 0
                "NO" -> homeScore == 0 || awayScore == 0
                else -> false
            }
            "HANDICAP" -> when (item.selectionKey) {
                "HOME_MINUS_1" -> (homeScore - awayScore) >= 2
                "AWAY_PLUS_1" -> (awayScore - homeScore) >= 0
                else -> false
            }
            "EXACT_SCORE" -> item.selectionKey == "$homeScore-$awayScore"
            "NEXT_GOAL" -> when (item.selectionKey) {
                "HOME" -> homeScore > 0
                "AWAY" -> awayScore > 0
                "NONE" -> homeScore == 0 && awayScore == 0
                else -> false
            }
            else -> false
        }
    }

    /**
     * Termine la saison, décerne les trophées (Ballon d'Or, Soulier d'Or, etc.) et lance la saison suivante.
     */
    suspend fun completeSeasonAndCrownChampion(leagueId: String): SeasonAwardEntity? = withContext(Dispatchers.IO) {
        val league = db.leagueDao().getLeagueById(leagueId) ?: return@withContext null
        val standings = db.teamDao().getTeamsByLeagueSync(leagueId).sortedWith(
            compareByDescending<TeamEntity> { it.points }
                .thenByDescending { it.goalsFor - it.goalsAgainst }
                .thenByDescending { it.goalsFor }
        )

        if (standings.isEmpty()) return@withContext null

        val champion = standings[0]
        val runnerUp = if (standings.size > 1) standings[1] else champion

        val champPlayers = db.playerDao().getPlayersByTeamSync(champion.id)
        val ballonDor = champPlayers.maxByOrNull { it.rating } ?: champPlayers.firstOrNull()
        val allPlayers = champPlayers.toMutableList()
        for (team in standings) {
            allPlayers.addAll(db.playerDao().getPlayersByTeamSync(team.id))
        }

        val soulierDor = allPlayers.maxByOrNull { it.goals } ?: allPlayers.firstOrNull()
        val bestKeeper = allPlayers.firstOrNull { it.position == "GK" } ?: allPlayers.firstOrNull()

        val seasonNumber = league.season.filter { it.isDigit() }.toIntOrNull() ?: 1
        val award = SeasonAwardEntity(
            id = UUID.randomUUID().toString(),
            leagueId = leagueId,
            leagueName = league.name,
            seasonNumber = seasonNumber,
            seasonLabel = "Saison $seasonNumber",
            championTeamId = champion.id,
            championTeamName = champion.name,
            runnerUpTeamName = runnerUp.name,
            ballonDorPlayerName = ballonDor?.name ?: "Étoile KGC",
            ballonDorTeamName = champion.name,
            soulierDorPlayerName = soulierDor?.name ?: "Buteur KGC",
            soulierDorGoals = soulierDor?.goals?.coerceAtLeast(8) ?: 8,
            soulierDorTeamName = champion.name,
            gantDorPlayerName = bestKeeper?.name ?: "Mur d'Or",
            gantDorTeamName = champion.name
        )

        db.seasonAwardDao().insertAward(award)

        // Réinitialisation pour la nouvelle saison
        val nextSeason = "Saison ${seasonNumber + 1} (2026/2027)"
        db.leagueDao().updateLeague(
            league.copy(
                currentRound = 1,
                season = nextSeason
            )
        )

        // Remise à zéro des statistiques de championnat des équipes
        val resetTeams = standings.map {
            it.copy(
                played = 0,
                won = 0,
                drawn = 0,
                lost = 0,
                goalsFor = 0,
                goalsAgainst = 0,
                points = 0,
                formString = "V,V,N,D,V"
            )
        }
        db.teamDao().updateTeams(resetTeams)

        // Réinitialisation des matchs
        val initialFixtures = com.example.kgcfootball.data.db.DatabaseSeeder.getInitialFixtures()
            .filter { it.leagueId == leagueId }
            .map {
                it.copy(
                    id = "fix_${it.leagueId}_s${seasonNumber + 1}_${it.id}",
                    status = "SCHEDULED",
                    minute = 0,
                    homeScore = 0,
                    awayScore = 0,
                    lastEventText = "Coup d'envoi de la nouvelle saison !"
                )
            }
        db.matchDao().insertFixtures(initialFixtures)

        award
    }

    /**
     * Modification directe du solde virtuel (Sandbox Bankroll).
     */
    suspend fun updateWalletBalance(newBalance: Double) = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync() ?: return@withContext
        db.userWalletDao().updateWallet(
            wallet.copy(balance = newBalance.coerceAtLeast(0.0))
        )
    }

    /**
     * Configuration de la sécurité par code PIN.
     */
    suspend fun setPinCode(pin: String?, enabled: Boolean) = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync() ?: return@withContext
        db.userWalletDao().updateWallet(
            wallet.copy(
                pinCode = pin,
                isPinProtectionActive = enabled
            )
        )
    }

    suspend fun claimFreeDailyBonus(): Result<Double> = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync() ?: return@withContext Result.failure(Exception("Portefeuille non initialisé"))
        val bonus = when {
            wallet.totalBetsPlaced >= 50 -> 50000.0 // VIP Diamant
            wallet.totalBetsPlaced >= 30 -> 35000.0 // VIP Platine
            wallet.totalBetsPlaced >= 15 -> 20000.0 // VIP Or
            wallet.totalBetsPlaced >= 5 -> 10000.0  // VIP Argent
            else -> 5000.0                          // VIP Bronze
        }
        val updated = wallet.copy(
            balance = wallet.balance + bonus,
            lastDailyBonusTime = System.currentTimeMillis()
        )
        db.userWalletDao().updateWallet(updated)
        Result.success(bonus)
    }

    suspend fun resetWalletBalance(amount: Double = 25000.0) = withContext(Dispatchers.IO) {
        val wallet = db.userWalletDao().getWalletSync() ?: return@withContext
        db.userWalletDao().updateWallet(
            wallet.copy(balance = amount)
        )
    }
}
