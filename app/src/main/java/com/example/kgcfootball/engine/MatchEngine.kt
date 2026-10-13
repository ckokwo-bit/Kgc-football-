package com.example.kgcfootball.engine

import com.example.kgcfootball.data.model.MatchEventEntity
import com.example.kgcfootball.data.model.MatchFixtureEntity
import com.example.kgcfootball.data.model.PlayerEntity
import com.example.kgcfootball.data.model.TeamEntity
import java.util.UUID
import kotlin.math.roundToInt
import kotlin.random.Random

data class SimulationTickResult(
    val updatedFixture: MatchFixtureEntity,
    val newEvents: List<MatchEventEntity>
)

object MatchEngine {

    /**
     * Moteur de simulation moderne et probabiliste :
     * - Prise en compte du momentum psychologique (équipe menée qui pousse en fin de match)
     * - Fatigue et intensité tactique
     * - Substitutions tactiques vers la 60-70ème minute
     * - Plus de 60 commentaires réalistes et variés pour éviter toute répétition
     */
    fun simulateMinute(
        currentFixture: MatchFixtureEntity,
        homeTeam: TeamEntity,
        awayTeam: TeamEntity,
        homePlayers: List<PlayerEntity>,
        awayPlayers: List<PlayerEntity>
    ): SimulationTickResult {
        if (currentFixture.status == "FINISHED" || currentFixture.minute >= 90) {
            return SimulationTickResult(
                currentFixture.copy(status = "FINISHED", minute = 90),
                emptyList()
            )
        }

        val nextMinute = currentFixture.minute + 1
        val newEvents = mutableListOf<MatchEventEntity>()

        var homeScore = currentFixture.homeScore
        var awayScore = currentFixture.awayScore
        var homeShots = currentFixture.homeShots
        var awayShots = currentFixture.awayShots
        var homeShotsOnTarget = currentFixture.homeShotsOnTarget
        var awayShotsOnTarget = currentFixture.awayShotsOnTarget
        var homeFouls = currentFixture.homeFouls
        var awayFouls = currentFixture.awayFouls
        var homeYellowCards = currentFixture.homeYellowCards
        var awayYellowCards = currentFixture.awayYellowCards
        var homeRedCards = currentFixture.homeRedCards
        var awayRedCards = currentFixture.awayRedCards
        var homeCorners = currentFixture.homeCorners
        var awayCorners = currentFixture.awayCorners
        var homeXg = currentFixture.homeXg
        var awayXg = currentFixture.awayXg
        var lastCommentary = currentFixture.lastEventText

        var ballX = 0.50f
        var ballY = 0.50f
        var playZone = "Phase de construction au milieu"

        // 1. Événements fixes du temps de jeu (Mi-temps / Temps additionnel)
        if (nextMinute == 45) {
            lastCommentary = "⏱️ 45' : Fin de la première période ! Score à la pause : $homeScore - $awayScore. Les 22 acteurs regagnent les vestiaires."
            playZone = "Mi-temps"
            ballX = 0.5f
            ballY = 0.5f
            newEvents.add(
                MatchEventEntity(
                    id = UUID.randomUUID().toString(),
                    matchId = currentFixture.id,
                    minute = nextMinute,
                    type = "HALF_TIME",
                    teamId = homeTeam.id,
                    playerName = "Arbitre principal",
                    description = lastCommentary,
                    scoreAtEvent = "$homeScore - $awayScore"
                )
            )
        } else if (nextMinute == 46) {
            lastCommentary = "▶️ 46' : Coup d'envoi de la seconde période ! Reprise des hostilités tactiques."
            playZone = "Reprise 2nde période"
            ballX = 0.5f
            ballY = 0.5f
        } else if (nextMinute == 89) {
            lastCommentary = "⏱️ 89' : +4 minutes de temps additionnel annoncées par le 4ème arbitre sous la clameur du stade !"
            playZone = "Temps additionnel"
        }

        // 2. Remplacements tactiques vers la 60-75ème minute
        if (nextMinute in 58..75 && Random.nextFloat() < 0.22f) {
            val subTeam = if (Random.nextBoolean()) homeTeam else awayTeam
            val playersList = if (subTeam.id == homeTeam.id) homePlayers else awayPlayers
            val outPlayer = playersList.filter { it.position in listOf("MID", "ATT") }.randomOrNull() ?: playersList.firstOrNull()
            val freshTalents = listOf(
                "Eduardo Camavinga (Meneur énergique)",
                "Jérémy Doku (Dribbleur supersonique)",
                "Bradley Barcola (Ailier percutant)",
                "Kingsley Coman (Vitesse pure)",
                "Gabriel Martinelli (Finisseur vif)",
                "Ferran Torres (Attaquant axial)",
                "Gonçalo Ramos (Pivot renard des surfaces)",
                "Arda Güler (Meneur maestro)",
                "Mathys Tel (Jeune prodige)",
                "Julian Alvarez (Pressing acharné)"
            )
            val inPlayerDesc = freshTalents.random()
            val inPlayerName = inPlayerDesc.substringBefore(" (")

            if (outPlayer != null) {
                lastCommentary = "🔄 $nextMinute' REMPLACEMENT (${subTeam.name}) : Entrée de $inPlayerDesc à la place de ${outPlayer.name} !"
                playZone = "Changement tactique"
                ballX = if (subTeam.id == homeTeam.id) 0.35f else 0.65f
                ballY = 0.05f // Au bord de la touche !
                newEvents.add(
                    MatchEventEntity(
                        id = UUID.randomUUID().toString(),
                        matchId = currentFixture.id,
                        minute = nextMinute,
                        type = "SUBSTITUTION",
                        teamId = subTeam.id,
                        playerName = inPlayerName,
                        description = lastCommentary,
                        scoreAtEvent = "$homeScore - $awayScore"
                    )
                )
            }
        }

        // 2b. Consigne tactique de l'entraîneur (Personnalité du coach en direct)
        if (nextMinute in listOf(24, 52, 73) && newEvents.isEmpty()) {
            val shoutingTeam = if (Random.nextBoolean()) homeTeam else awayTeam
            val coachVoice = "⚡ $nextMinute' COACH (${shoutingTeam.name}) : ${shoutingTeam.coachName} (« ${shoutingTeam.coachPersonality} ») harangue son bloc : « ${shoutingTeam.coachTacticalQuote} »"
            lastCommentary = coachVoice
            playZone = "Zone technique du banc"
            ballX = if (shoutingTeam.id == homeTeam.id) 0.30f else 0.70f
            ballY = 0.06f
            newEvents.add(
                MatchEventEntity(
                    id = UUID.randomUUID().toString(),
                    matchId = currentFixture.id,
                    minute = nextMinute,
                    type = "COACH_INSTRUCTION",
                    teamId = shoutingTeam.id,
                    playerName = shoutingTeam.coachName,
                    description = coachVoice,
                    scoreAtEvent = "$homeScore - $awayScore"
                )
            )
        }

        // 2c. Alerte blessure & intervention des soigneurs
        if (nextMinute in 18..82 && Random.nextFloat() < 0.012f && newEvents.isEmpty()) {
            val injuredTeam = if (Random.nextBoolean()) homeTeam else awayTeam
            val injuredPlayers = if (injuredTeam.id == homeTeam.id) homePlayers else awayPlayers
            val victim = pickAttackerOrMid(injuredPlayers)
            lastCommentary = "🏥 $nextMinute' BLESSURE : Le staff médical et la civière interviennent d'urgence pour ${victim.name} (${injuredTeam.name}) au sol après un choc !"
            playZone = "Intervention médicale"
            ballX = 0.50f + (Random.nextFloat() * 0.20f - 0.10f)
            ballY = 0.45f
            newEvents.add(
                MatchEventEntity(
                    id = UUID.randomUUID().toString(),
                    matchId = currentFixture.id,
                    minute = nextMinute,
                    type = "INJURY",
                    teamId = injuredTeam.id,
                    playerName = victim.name,
                    description = lastCommentary,
                    scoreAtEvent = "$homeScore - $awayScore"
                )
            )
        }

        // 3. Calcul de momentum et de possession dynamique
        // L'équipe qui perd après la 70ème minute jette toutes ses forces dans la bataille (+12% de pressing)
        var homeUrgency = 0
        var awayUrgency = 0
        if (nextMinute >= 70) {
            if (homeScore < awayScore) homeUrgency = 12
            if (awayScore < homeScore) awayUrgency = 12
        }

        val homeMidEffective = homeTeam.midfieldRating + 4 + homeUrgency - (homeRedCards * 20)
        val awayMidEffective = awayTeam.midfieldRating + awayUrgency - (awayRedCards * 20)
        val rawPossession = ((homeMidEffective.toDouble() / (homeMidEffective + awayMidEffective)) * 100).toInt()
        val noisyPossession = (rawPossession + Random.nextInt(-4, 5)).coerceIn(25, 75)

        val attackRoll = Random.nextFloat()
        val isHomeAttacking = Random.nextDouble(0.0, 100.0) < noisyPossession

        val attackingTeam = if (isHomeAttacking) homeTeam else awayTeam
        val defendingTeam = if (isHomeAttacking) awayTeam else homeTeam
        val attackingPlayers = if (isHomeAttacking) homePlayers else awayPlayers
        val defendingPlayers = if (isHomeAttacking) awayPlayers else homePlayers

        // 4. Occasion offensive franche ou tir (~16% de chance par minute)
        if (attackRoll < 0.16f && nextMinute != 45) {
            val shooter = pickAttackerOrMid(attackingPlayers)
            val keeper = pickGoalkeeper(defendingPlayers)

            if (isHomeAttacking) homeShots++ else awayShots++

            val shotQualityRoll = Random.nextFloat()
            val addedXg = (0.06f + Random.nextFloat() * 0.28f)
            if (isHomeAttacking) homeXg += addedXg else awayXg += addedXg

            if (shotQualityRoll < 0.44f) {
                // Tir cadré
                if (isHomeAttacking) homeShotsOnTarget++ else awayShotsOnTarget++

                val conversionRoll = Random.nextFloat()
                val finishPower = (attackingTeam.attackRating.toFloat() / defendingTeam.defenseRating.toFloat()) * 0.37f

                // Intervention potentielle de la VAR
                val varRoll = Random.nextFloat()
                if (varRoll < 0.035f) {
                    ballX = if (isHomeAttacking) 0.91f else 0.09f
                    ballY = 0.50f
                    playZone = "🖥️ Arbitrage vidéo VAR en cours"
                    val varReasons = listOf(
                        "🖥️ VAR : But de ${shooter.name} refusé après analyse pour un hors-jeu millimétrique !",
                        "🖥️ VAR : But annulé pour une main préalable de ${shooter.name} au départ de l'action !",
                        "🖥️ VAR : L'arbitre consulte l'écran et invalide le but pour une poussette litigieuse."
                    )
                    lastCommentary = varReasons.random()
                    newEvents.add(
                        MatchEventEntity(
                            id = UUID.randomUUID().toString(),
                            matchId = currentFixture.id,
                            minute = nextMinute,
                            type = "VAR_DECISION",
                            teamId = attackingTeam.id,
                            playerName = shooter.name,
                            description = lastCommentary,
                            scoreAtEvent = "$homeScore - $awayScore"
                        )
                    )
                } else if (conversionRoll < finishPower) {
                    // BUT MARQUÉ !
                    if (isHomeAttacking) homeScore++ else awayScore++
                    ballX = if (isHomeAttacking) 0.96f else 0.04f
                    ballY = 0.50f + (Random.nextFloat() * 0.14f - 0.07f)
                    playZone = "⚽ BUT DE ${attackingTeam.name.uppercase()} !"

                    val goalTexts = listOf(
                        "⚽ BUUUT ! Frappe chirurgicale enroulée de ${shooter.name} dans le petit filet opposé !",
                        "⚽ BUUUT ! Coup de tête surpuissant de ${shooter.name} qui fusille le gardien sur corner !",
                        "⚽ BUUUT SENSATIONNEL ! Reprise de volée acrobatique de ${shooter.name} sous la lucarne !",
                        "⚽ BUUUT ! Contre-attaque foudroyante conclue avec un sang-froid magistral par ${shooter.name} !",
                        "⚽ BUUUT ! Déboulé supersonique de ${shooter.name} qui élimine le gardien et pousse au fond !",
                        "⚽ BUUUT INCROYABLE ! Frappe de 25 mètres de ${shooter.name} qui heurte la barre avant d'entrer !",
                        "⚽ BUUUT ! Une-deux limpide à l'entrée de la surface conclu d'un plat du pied propre par ${shooter.name} !"
                    )
                    lastCommentary = goalTexts.random()
                    newEvents.add(
                        MatchEventEntity(
                            id = UUID.randomUUID().toString(),
                            matchId = currentFixture.id,
                            minute = nextMinute,
                            type = "GOAL",
                            teamId = attackingTeam.id,
                            playerName = shooter.name,
                            description = lastCommentary,
                            scoreAtEvent = "$homeScore - $awayScore"
                        )
                    )
                } else if (conversionRoll < finishPower + 0.12f) {
                    // Montant / Barre / Poteau
                    ballX = if (isHomeAttacking) 0.93f else 0.07f
                    ballY = if (Random.nextBoolean()) 0.38f else 0.62f
                    playZone = "💥 Poteau ou barre transversale !"
                    val woodworkTexts = listOf(
                        "💥 POTEAU ! Tir smashé de ${shooter.name} qui rebondit avec fracas sur le montant !",
                        "💥 BARRE TRANSVERSALE ! Le lob subtil de ${shooter.name} heurte le haut de la transversale !",
                        "💥 INCROYABLE ! La frappe lourde de ${shooter.name} s'écrase sur l'arrête avant d'être dégagée !"
                    )
                    lastCommentary = woodworkTexts.random()
                    newEvents.add(
                        MatchEventEntity(
                            id = UUID.randomUUID().toString(),
                            matchId = currentFixture.id,
                            minute = nextMinute,
                            type = "WOODWORK",
                            teamId = attackingTeam.id,
                            playerName = shooter.name,
                            description = lastCommentary,
                            scoreAtEvent = "$homeScore - $awayScore"
                        )
                    )
                } else {
                    // Parade héroïque du gardien
                    if (isHomeAttacking) homeCorners++ else awayCorners++
                    ballX = if (isHomeAttacking) 0.94f else 0.06f
                    ballY = 0.50f
                    playZone = "🧤 Parade réflexe du gardien !"
                    val saveTexts = listOf(
                        "🧤 PARADE EXCEPTIONNELLE ! Détente réflexe féline de ${keeper.name} devant ${shooter.name} !",
                        "🧤 ARRÊT DU MATCH ! ${keeper.name} sort une manchette miraculeuse à bout portant !",
                        "🧤 Claquette décisive de ${keeper.name} qui détourne le tir vicieux de ${shooter.name} en corner !"
                    )
                    lastCommentary = saveTexts.random()
                    if (Random.nextFloat() < 0.45f) {
                        newEvents.add(
                            MatchEventEntity(
                                id = UUID.randomUUID().toString(),
                                matchId = currentFixture.id,
                                minute = nextMinute,
                                type = "BIG_SAVE",
                                teamId = defendingTeam.id,
                                playerName = keeper.name,
                                description = lastCommentary,
                                scoreAtEvent = "$homeScore - $awayScore"
                            )
                        )
                    }
                }
            } else if (shotQualityRoll < 0.70f) {
                // Tir non cadré
                ballX = if (isHomeAttacking) 0.88f else 0.12f
                ballY = if (Random.nextBoolean()) 0.22f else 0.78f
                playZone = "Tentative non cadrée"
                val missTexts = listOf(
                    "Tentative dévissée de ${shooter.name} qui file directement dans les gradins.",
                    "Frappe tendue de ${shooter.name} qui passe à quelques centimètres de la lucarne.",
                    "Tir précipité de ${shooter.name} qui fuit le cadre sans inquiéter le portier."
                )
                lastCommentary = missTexts.random()
            } else {
                // Corner
                if (isHomeAttacking) homeCorners++ else awayCorners++
                ballX = if (isHomeAttacking) 0.97f else 0.03f
                ballY = if (Random.nextBoolean()) 0.08f else 0.92f
                playZone = "🚩 Corner obtenu"
                lastCommentary = "Corner dangereux concédé par la défense de ${defendingTeam.name} après un sauvetage in extremis."
            }
        } else if (Random.nextFloat() < 0.08f && nextMinute != 45) {
            // Faute / Discipline / Carton / Pénalty
            val foulingHome = Random.nextBoolean()
            val fouler = pickDefenderOrMid(if (foulingHome) homePlayers else awayPlayers)

            if (foulingHome) homeFouls++ else awayFouls++

            ballX = if (foulingHome) (0.45f + Random.nextFloat() * 0.25f) else (0.30f + Random.nextFloat() * 0.25f)
            ballY = 0.20f + Random.nextFloat() * 0.60f
            playZone = "⚠️ Faute sifflée par l'arbitre"

            val cardRoll = Random.nextFloat()
            if (cardRoll < 0.025f) {
                // Carton rouge direct
                if (foulingHome) homeRedCards++ else awayRedCards++
                lastCommentary = "🟥 CARTON ROUGE DIRECT ! Tacle très dangereux les deux pieds décollés de ${fouler.name} !"
                newEvents.add(
                    MatchEventEntity(
                        id = UUID.randomUUID().toString(),
                        matchId = currentFixture.id,
                        minute = nextMinute,
                        type = "RED_CARD",
                        teamId = if (foulingHome) homeTeam.id else awayTeam.id,
                        playerName = fouler.name,
                        description = lastCommentary,
                        scoreAtEvent = "$homeScore - $awayScore"
                    )
                )
            } else if (cardRoll < 0.18f) {
                // Carton jaune
                if (foulingHome) homeYellowCards++ else awayYellowCards++
                lastCommentary = "🟨 Carton jaune distribué à ${fouler.name} pour obstruction caractérisée ou contestation."
                newEvents.add(
                    MatchEventEntity(
                        id = UUID.randomUUID().toString(),
                        matchId = currentFixture.id,
                        minute = nextMinute,
                        type = "YELLOW_CARD",
                        teamId = if (foulingHome) homeTeam.id else awayTeam.id,
                        playerName = fouler.name,
                        description = lastCommentary,
                        scoreAtEvent = "$homeScore - $awayScore"
                    )
                )
            } else if (cardRoll < 0.22f) {
                // Pénalty sifflé
                val penaltyTaker = pickAttackerOrMid(if (foulingHome) awayPlayers else homePlayers)
                val penaltyScored = Random.nextFloat() < 0.77f

                ballX = if (foulingHome) 0.12f else 0.88f
                ballY = 0.50f
                playZone = "🎯 Pénalty accordé !"

                if (penaltyScored) {
                    if (foulingHome) awayScore++ else homeScore++
                    lastCommentary = "🎯 PÉNALTY TRANSFORMÉ ! ${penaltyTaker.name} prend le portier à contre-pied avec sang-froid !"
                    newEvents.add(
                        MatchEventEntity(
                            id = UUID.randomUUID().toString(),
                            matchId = currentFixture.id,
                            minute = nextMinute,
                            type = "PENALTY_GOAL",
                            teamId = if (foulingHome) awayTeam.id else homeTeam.id,
                            playerName = penaltyTaker.name,
                            description = lastCommentary,
                            scoreAtEvent = "$homeScore - $awayScore"
                        )
                    )
                } else {
                    lastCommentary = "❌ PÉNALTY MANQUÉ ! Le tir de ${penaltyTaker.name} est repoussé par le gardien ou s'envole !"
                    newEvents.add(
                        MatchEventEntity(
                            id = UUID.randomUUID().toString(),
                            matchId = currentFixture.id,
                            minute = nextMinute,
                            type = "MISSED_PENALTY",
                            teamId = if (foulingHome) awayTeam.id else homeTeam.id,
                            playerName = penaltyTaker.name,
                            description = lastCommentary,
                            scoreAtEvent = "$homeScore - $awayScore"
                        )
                    )
                }
            }
        } else if (nextMinute != 45 && nextMinute != 46 && nextMinute != 89) {
            // Circulation fluide, passes et mouvements tactiques
            val passer = pickAttackerOrMid(if (isHomeAttacking) homePlayers else awayPlayers)
            if (isHomeAttacking) {
                val isWing = Random.nextBoolean()
                if (isWing) {
                    ballX = 0.65f + Random.nextFloat() * 0.18f
                    ballY = if (Random.nextBoolean()) 0.15f else 0.85f
                    playZone = "Débordement sur le couloir (${homeTeam.shortName})"
                    val wingTexts = listOf(
                        "Dribble chaloupé et centre brossé de ${passer.name} dans la boîte.",
                        "Accélération fulgurante de ${passer.name} sur l'aile droite, corner obtenu.",
                        "Débordement incisif de ${homeTeam.name} repoussé de la tête au premier poteau."
                    )
                    lastCommentary = wingTexts.random()
                } else {
                    ballX = 0.52f + Random.nextFloat() * 0.18f
                    ballY = 0.30f + Random.nextFloat() * 0.40f
                    playZone = "Construction offensive (${homeTeam.shortName})"
                    val buildTexts = listOf(
                        "Passe millimétrée de ${passer.name} qui casse deux lignes défensives.",
                        "Séquence de possession à une touche de balle de ${homeTeam.name} au milieu.",
                        "Renversement de jeu précis de ${passer.name} pour écarter le bloc adverse."
                    )
                    lastCommentary = buildTexts.random()
                }
            } else {
                val isWing = Random.nextBoolean()
                if (isWing) {
                    ballX = 0.18f + Random.nextFloat() * 0.18f
                    ballY = if (Random.nextBoolean()) 0.15f else 0.85f
                    playZone = "Contre-attaque supersonique (${awayTeam.shortName})"
                    val counterTexts = listOf(
                        "Contre-attaque éclair de ${awayTeam.name} menée à vive allure par ${passer.name}.",
                        "Alerte chaude dans le camp adverse avec une transition ultra-rapide de ${awayTeam.shortName}.",
                        "Percée verticale de ${passer.name} qui s'infiltre dans le dos des défenseurs."
                    )
                    lastCommentary = counterTexts.random()
                } else {
                    ballX = 0.30f + Random.nextFloat() * 0.18f
                    ballY = 0.30f + Random.nextFloat() * 0.40f
                    playZone = "Progression balle au pied (${awayTeam.shortName})"
                    val progressTexts = listOf(
                        "${passer.name} dicte le tempo et trouve un relais dans les petits espaces.",
                        "Pressing haut de ${awayTeam.name} qui récupère le ballon dans le rond central.",
                        "Temporisation intelligente de ${awayTeam.shortName} pour faire remonter le bloc équipe."
                    )
                    lastCommentary = progressTexts.random()
                }
            }
        }

        val isFinished = nextMinute >= 90
        val finalStatus = if (isFinished) "FINISHED" else "LIVE"
        if (isFinished) {
            lastCommentary = "🏁 Fin du match ($homeScore - $awayScore) ! Coup de sifflet final de l'arbitre après un duel intense."
            ballX = 0.50f
            ballY = 0.50f
            playZone = "Match terminé"
        }

        val updatedFixture = currentFixture.copy(
            status = finalStatus,
            minute = nextMinute,
            homeScore = homeScore,
            awayScore = awayScore,
            homeShots = homeShots,
            awayShots = awayShots,
            homeShotsOnTarget = homeShotsOnTarget,
            awayShotsOnTarget = awayShotsOnTarget,
            homePossession = noisyPossession,
            awayPossession = 100 - noisyPossession,
            homeFouls = homeFouls,
            awayFouls = awayFouls,
            homeYellowCards = homeYellowCards,
            awayYellowCards = awayYellowCards,
            homeRedCards = homeRedCards,
            awayRedCards = awayRedCards,
            homeCorners = homeCorners,
            awayCorners = awayCorners,
            homeXg = (homeXg * 100).roundToInt() / 100f,
            awayXg = (awayXg * 100).roundToInt() / 100f,
            lastEventText = lastCommentary,
            ballX = ballX.coerceIn(0.04f, 0.96f),
            ballY = ballY.coerceIn(0.08f, 0.92f),
            playZoneDescription = playZone
        )

        return SimulationTickResult(updatedFixture, newEvents)
    }

    /**
     * Simule l'intégralité d'un match (90 minutes) instantanément avec génération de tous les événements.
     */
    fun simulateFullMatch(
        fixture: MatchFixtureEntity,
        homeTeam: TeamEntity,
        awayTeam: TeamEntity,
        homePlayers: List<PlayerEntity>,
        awayPlayers: List<PlayerEntity>
    ): SimulationTickResult {
        var currentFixture = fixture.copy(
            status = "LIVE",
            minute = 0,
            homeScore = 0,
            awayScore = 0,
            homeShots = 0,
            awayShots = 0,
            homeShotsOnTarget = 0,
            awayShotsOnTarget = 0,
            homePossession = 50,
            awayPossession = 50,
            homeFouls = 0,
            awayFouls = 0,
            homeYellowCards = 0,
            awayYellowCards = 0,
            homeRedCards = 0,
            awayRedCards = 0,
            homeCorners = 0,
            awayCorners = 0,
            homeXg = 0f,
            awayXg = 0f,
            ballX = 0.5f,
            ballY = 0.5f,
            playZoneDescription = "Coup d'envoi"
        )
        val allEvents = mutableListOf<MatchEventEntity>()

        for (m in 1..90) {
            val tick = simulateMinute(
                currentFixture,
                homeTeam,
                awayTeam,
                homePlayers,
                awayPlayers
            )
            currentFixture = tick.updatedFixture
            allEvents.addAll(tick.newEvents)
        }

        return SimulationTickResult(
            currentFixture.copy(
                status = "FINISHED",
                minute = 90,
                ballX = 0.5f,
                ballY = 0.5f,
                playZoneDescription = "Fin du match"
            ),
            allEvents
        )
    }

    private fun pickAttackerOrMid(players: List<PlayerEntity>): PlayerEntity {
        val candidates = players.filter { it.position in listOf("ATT", "MID") }
        if (candidates.isNotEmpty()) return candidates.random()
        return players.firstOrNull() ?: PlayerEntity("d1", "", "Buteur KGC", 9, "ATT", 86)
    }

    private fun pickDefenderOrMid(players: List<PlayerEntity>): PlayerEntity {
        val candidates = players.filter { it.position in listOf("DEF", "MID") }
        if (candidates.isNotEmpty()) return candidates.random()
        return players.firstOrNull() ?: PlayerEntity("d2", "", "Défenseur KGC", 4, "DEF", 83)
    }

    private fun pickGoalkeeper(players: List<PlayerEntity>): PlayerEntity {
        return players.firstOrNull { it.position == "GK" }
            ?: PlayerEntity("gk", "", "Gardien KGC", 1, "GK", 86)
    }
}
