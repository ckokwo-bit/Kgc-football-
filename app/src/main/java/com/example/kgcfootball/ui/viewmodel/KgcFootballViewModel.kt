package com.example.kgcfootball.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kgcfootball.data.db.AppDatabase
import com.example.kgcfootball.data.model.*
import com.example.kgcfootball.data.repository.KgcFootballRepository
import com.example.kgcfootball.engine.MarketOdds
import com.example.kgcfootball.engine.OddsEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppNavTab {
    MATCHES,
    LIVE,
    SPORTSBOOK,
    NEWS,
    STANDINGS,
    WALLET
}

enum class CyclePhase(val label: String) {
    PRE_MATCH("PARIS OUVERTS"),
    LIVE_IN_PLAY("EN DIRECT"),
    POST_MATCH_REVIEW("RÉSULTATS & GAINS")
}

data class LiveMatchDetail(
    val fixture: MatchFixtureEntity,
    val homeTeam: TeamEntity,
    val awayTeam: TeamEntity,
    val events: List<MatchEventEntity>,
    val liveOdds: MarketOdds
)

class KgcFootballViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = KgcFootballRepository(AppDatabase.getDatabase(application))

    // Navigation & Sélection
    private val _selectedTab = MutableStateFlow(AppNavTab.MATCHES)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _selectedLeagueId = MutableStateFlow("kgc_champions")
    val selectedLeagueId: StateFlow<String> = _selectedLeagueId.asStateFlow()

    private val _selectedRound = MutableStateFlow(1)
    val selectedRound: StateFlow<Int> = _selectedRound.asStateFlow()

    private val _activeLiveMatchId = MutableStateFlow<String?>("fix_c1_1")
    val activeLiveMatchId: StateFlow<String?> = _activeLiveMatchId.asStateFlow()

    // Moteur de Programme Automatique (Cycles virtuels style 1xBet / Betclic)
    private val _cyclePhase = MutableStateFlow(CyclePhase.PRE_MATCH)
    val cyclePhase: StateFlow<CyclePhase> = _cyclePhase.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(35)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isAutoCycleActive = MutableStateFlow(true)
    val isAutoCycleActive: StateFlow<Boolean> = _isAutoCycleActive.asStateFlow()

    // Vitesse de simulation du direct
    private val _simSpeed = MutableStateFlow(1)
    val simSpeed: StateFlow<Int> = _simSpeed.asStateFlow()

    // Coupon de Paris (Betting Slip)
    private val _couponSelections = MutableStateFlow<List<BetItemEntity>>(emptyList())
    val couponSelections: StateFlow<List<BetItemEntity>> = _couponSelections.asStateFlow()

    private val _isCouponOpen = MutableStateFlow(false)
    val isCouponOpen: StateFlow<Boolean> = _isCouponOpen.asStateFlow()

    private val _stakeAmount = MutableStateFlow(1000.0)
    val stakeAmount: StateFlow<Double> = _stakeAmount.asStateFlow()

    // Popups et détails
    private val _selectedTeamDetails = MutableStateFlow<TeamEntity?>(null)
    val selectedTeamDetails: StateFlow<TeamEntity?> = _selectedTeamDetails.asStateFlow()

    private val _squadPlayers = MutableStateFlow<List<PlayerEntity>>(emptyList())
    val squadPlayers: StateFlow<List<PlayerEntity>> = _squadPlayers.asStateFlow()

    private val _crownedAward = MutableStateFlow<SeasonAwardEntity?>(null)
    val crownedAward: StateFlow<SeasonAwardEntity?> = _crownedAward.asStateFlow()

    private val _isCustomBalanceDialogOpen = MutableStateFlow(false)
    val isCustomBalanceDialogOpen: StateFlow<Boolean> = _isCustomBalanceDialogOpen.asStateFlow()

    // Sécurité par Code PIN
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    private val _isPinSetupDialogOpen = MutableStateFlow(false)
    val isPinSetupDialogOpen: StateFlow<Boolean> = _isPinSetupDialogOpen.asStateFlow()

    // Style de simulation (VIRTUEL_EXPRESS, VIRTUEL_MOYEN, TEMPS_REEL)
    private val _simulationStyle = MutableStateFlow("VIRTUEL_EXPRESS")
    val simulationStyle: StateFlow<String> = _simulationStyle.asStateFlow()

    private val _isSimulationStyleDialogOpen = MutableStateFlow(false)
    val isSimulationStyleDialogOpen: StateFlow<Boolean> = _isSimulationStyleDialogOpen.asStateFlow()

    // Fiche d'Analyse d'Avant-Match & Statistiques
    private val _analyzedFixture = MutableStateFlow<MatchFixtureEntity?>(null)
    val analyzedFixture: StateFlow<MatchFixtureEntity?> = _analyzedFixture.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Fil d'Actualités KGC Foot
    private val _newsList = MutableStateFlow<List<NewsArticle>>(NewsProvider.getInitialNews())
    val newsList: StateFlow<List<NewsArticle>> = _newsList.asStateFlow()

    // Données réactives du Repository
    val allLeagues: StateFlow<List<LeagueEntity>> = repository.allLeagues
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeams: StateFlow<List<TeamEntity>> = repository.allTeams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wallet: StateFlow<UserWalletEntity?> = repository.userWallet
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allTickets: StateFlow<List<BetTicketEntity>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topScorers: StateFlow<List<PlayerEntity>> = repository.topScorers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAwards: StateFlow<List<SeasonAwardEntity>> = repository.allAwards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val standings: StateFlow<List<TeamEntity>> = _selectedLeagueId
        .flatMapLatest { leagueId -> repository.getStandings(leagueId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentFixtures: StateFlow<List<MatchFixtureEntity>> = combine(
        _selectedLeagueId,
        _selectedRound
    ) { leagueId, round ->
        Pair(leagueId, round)
    }.flatMapLatest { (leagueId, round) ->
        repository.getFixtures(leagueId, round)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLiveDetail: StateFlow<LiveMatchDetail?> = _activeLiveMatchId
        .flatMapLatest { matchId ->
            if (matchId == null) flowOf(null)
            else {
                combine(
                    repository.getFixtureFlow(matchId),
                    repository.allTeams,
                    repository.getMatchEvents(matchId)
                ) { fixture, teams, events ->
                    if (fixture == null) null
                    else {
                        val home = teams.firstOrNull { it.id == fixture.homeTeamId }
                            ?: TeamEntity(fixture.homeTeamId, "", "Domicile", "DOM", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
                        val away = teams.firstOrNull { it.id == fixture.awayTeamId }
                            ?: TeamEntity(fixture.awayTeamId, "", "Extérieur", "EXT", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")

                        val liveOdds = if (fixture.status == "SCHEDULED") {
                            OddsEngine.calculatePreMatchOdds(home, away)
                        } else {
                            OddsEngine.calculateLiveOdds(
                                home, away, fixture.minute, fixture.homeScore, fixture.awayScore,
                                fixture.homeRedCards, fixture.awayRedCards
                            )
                        }

                        LiveMatchDetail(
                            fixture = fixture,
                            homeTeam = home,
                            awayTeam = away,
                            events = events,
                            liveOdds = liveOdds
                        )
                    }
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allLiveFixtures: StateFlow<List<MatchFixtureEntity>> = repository.liveFixtures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _winningTicketAlert = MutableStateFlow<BetTicketEntity?>(null)
    val winningTicketAlert: StateFlow<BetTicketEntity?> = _winningTicketAlert.asStateFlow()

    fun dismissWinningAlert() {
        _winningTicketAlert.value = null
    }

    private var virtualCycleJob: Job? = null

    init {
        // Initialisation de la base de données et vérification de sécurité PIN et style de jeu
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
            repository.userWallet.first()?.let { userWallet ->
                if (userWallet.isPinProtectionActive && !userWallet.pinCode.isNullOrEmpty()) {
                    _isAppLocked.value = true
                }
                if (userWallet.simulationStyle.isNotBlank()) {
                    _simulationStyle.value = userWallet.simulationStyle
                }
            }
        }

        // Écoute des articles dynamiques d'après-match
        viewModelScope.launch {
            repository.newArticleFlow.collect { article ->
                _newsList.value = listOf(article) + _newsList.value.take(40)
            }
        }

        // Écoute des alertes de tickets gagnants pour célébration
        viewModelScope.launch {
            repository.winningTicketFlow.collect { ticket ->
                _winningTicketAlert.value = ticket
            }
        }

        startAutomatedVirtualCycle()
    }

    /**
     * Moteur central du calendrier automatique des matchs virtuels.
     * Prise en charge des 3 styles : Express (~1 min), Moyen (10 min réelles), Temps Réel Pro (90 min réelles).
     */
    private fun startAutomatedVirtualCycle() {
        virtualCycleJob?.cancel()
        virtualCycleJob = viewModelScope.launch {
            while (true) {
                if (_isAutoCycleActive.value) {
                    val currentStyle = _simulationStyle.value
                    // Évolution des comptes à rebours et des matchs en direct selon le style
                    repository.tickStaggeredSimulation(currentStyle)

                    val leagueId = _selectedLeagueId.value
                    val round = _selectedRound.value

                    // Vérifie les matchs de la journée sélectionnée
                    val fixtures = repository.getFixtures(leagueId, round).first()
                    if (fixtures.isEmpty()) {
                        repository.ensureRoundFixturesExist(leagueId, round)
                    } else {
                        val hasLive = fixtures.any { it.status == "LIVE" }
                        val scheduled = fixtures.filter { it.status == "SCHEDULED" }
                        val allFinished = fixtures.all { it.status == "FINISHED" }

                        if (hasLive) {
                            _cyclePhase.value = CyclePhase.LIVE_IN_PLAY
                            _countdownSeconds.value = scheduled.minOfOrNull { it.countdownSecondsRemaining } ?: 0
                        } else if (scheduled.isNotEmpty()) {
                            _cyclePhase.value = CyclePhase.PRE_MATCH
                            _countdownSeconds.value = scheduled.minOfOrNull { it.countdownSecondsRemaining } ?: 0
                        } else if (allFinished) {
                            _cyclePhase.value = CyclePhase.POST_MATCH_REVIEW
                            // Vérifie si la saison est terminée
                            val league = allLeagues.value.firstOrNull { it.id == leagueId }
                            if (league != null && round >= league.totalRounds) {
                                val award = repository.completeSeasonAndCrownChampion(leagueId)
                                if (award != null) {
                                    _crownedAward.value = award
                                }
                            }
                        }
                    }

                    // Sélection intelligente du match en direct si le match actuel est terminé ou null
                    val activeId = _activeLiveMatchId.value
                    val currentLiveMatches = repository.liveFixtures.first()
                    if (currentLiveMatches.isNotEmpty()) {
                        val activeMatch = currentLiveMatches.firstOrNull { it.id == activeId }
                        if (activeMatch == null) {
                            _activeLiveMatchId.value = currentLiveMatches.first().id
                        }
                    }

                    val delayMs = when (currentStyle) {
                        "VIRTUEL_MOYEN" -> 6000L // ~6s par minute -> 90 min de match en ~9 minutes réelles
                        "TEMPS_REEL" -> 15000L  // Vérification régulière et synchronisation horloge
                        else -> when (_simSpeed.value) {
                            5 -> 250L
                            2 -> 500L
                            else -> 1000L
                        }
                    }
                    delay(delayMs)
                } else {
                    delay(500L)
                }
            }
        }
    }

    fun setTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    fun selectLeague(leagueId: String) {
        _selectedLeagueId.value = leagueId
        viewModelScope.launch {
            repository.ensureRoundFixturesExist(leagueId, _selectedRound.value)
        }
    }

    fun selectRound(round: Int) {
        _selectedRound.value = round
        viewModelScope.launch {
            repository.ensureRoundFixturesExist(_selectedLeagueId.value, round)
        }
    }

    fun launchMatchImmediately(matchId: String) {
        viewModelScope.launch {
            repository.launchMatchImmediately(matchId)
            _activeLiveMatchId.value = matchId
            _snackbarMessage.value = "⚽ Coup d'envoi immédiat lancé !"
        }
    }

    fun openLiveMatch(matchId: String) {
        _activeLiveMatchId.value = matchId
        _selectedTab.value = AppNavTab.LIVE
    }

    fun toggleAutoCycle() {
        _isAutoCycleActive.value = !_isAutoCycleActive.value
        _snackbarMessage.value = if (_isAutoCycleActive.value) "Programme automatique activé" else "Programme automatique en pause"
    }

    fun skipToKickoffNow() {
        _countdownSeconds.value = 0
        _cyclePhase.value = CyclePhase.LIVE_IN_PLAY
    }

    fun setSimulationSpeed(speed: Int) {
        _simSpeed.value = speed
    }

    fun stepForwardOneMinute() {
        val matchId = _activeLiveMatchId.value ?: return
        viewModelScope.launch {
            repository.tickLiveMatch(matchId)
        }
    }

    fun finishLiveMatchInstantly() {
        val matchId = _activeLiveMatchId.value ?: return
        viewModelScope.launch {
            repository.simulateMatchInstantly(matchId)
            _snackbarMessage.value = "⚡ Match terminé !"
        }
    }

    fun simulateAllRoundMatches() {
        viewModelScope.launch {
            repository.simulateRoundFixtures(_selectedLeagueId.value, _selectedRound.value)
            _snackbarMessage.value = "🏆 Journée complète simulée avec succès !"
        }
    }

    fun openSimulationStyleDialog() {
        _isSimulationStyleDialogOpen.value = true
    }

    fun dismissSimulationStyleDialog() {
        _isSimulationStyleDialogOpen.value = false
    }

    fun setSimulationStyle(style: String) {
        _simulationStyle.value = style
        _isSimulationStyleDialogOpen.value = false
        viewModelScope.launch {
            repository.updateSimulationStyle(style)
            val label = when (style) {
                "VIRTUEL_MOYEN" -> "⏱️ Mode Moyen (10 minutes réelles) activé"
                "TEMPS_REEL" -> "🌍 Mode Temps Réel (90 min calendrier) activé"
                else -> "⚡ Mode Virtuel Express (Matchs rapides) activé"
            }
            _snackbarMessage.value = label
        }
    }

    fun openPreMatchAnalysis(fixture: MatchFixtureEntity) {
        _analyzedFixture.value = fixture
    }

    fun dismissPreMatchAnalysis() {
        _analyzedFixture.value = null
    }

    // Cash Out réaliste
    fun performCashout(ticketId: String) {
        viewModelScope.launch {
            val result = repository.performCashout(ticketId)
            result.onSuccess { amount ->
                _snackbarMessage.value = "💵 Cash Out réussi ! +${amount.toInt()} KGC crédités immédiatement."
            }.onFailure { err ->
                _snackbarMessage.value = "❌ Cash Out échoué : ${err.localizedMessage}"
            }
        }
    }

    // Gestion du Coupon de Paris
    fun toggleBetSelection(item: BetItemEntity) {
        val current = _couponSelections.value.toMutableList()
        val existingIndex = current.indexOfFirst {
            it.matchId == item.matchId && it.marketType == item.marketType
        }

        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            if (existing.selectionKey == item.selectionKey) {
                current.removeAt(existingIndex)
            } else {
                current[existingIndex] = item
            }
        } else {
            current.add(item)
            _isCouponOpen.value = true
        }

        _couponSelections.value = current
    }

    fun removeBetSelection(item: BetItemEntity) {
        _couponSelections.value = _couponSelections.value.filter { it.id != item.id && !(it.matchId == item.matchId && it.selectionKey == item.selectionKey) }
    }

    fun clearCoupon() {
        _couponSelections.value = emptyList()
    }

    fun setCouponOpen(open: Boolean) {
        _isCouponOpen.value = open
    }

    fun setStake(amount: Double) {
        _stakeAmount.value = amount.coerceAtLeast(100.0)
    }

    fun placeBet() {
        val stake = _stakeAmount.value
        val items = _couponSelections.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val result = repository.placeBetTicket(stake, items)
            result.onSuccess { ticketId ->
                _couponSelections.value = emptyList()
                _isCouponOpen.value = false
                _snackbarMessage.value = "✅ Pari $ticketId validé (${stake.toInt()} KGC) !"
            }.onFailure { err ->
                _snackbarMessage.value = "❌ Erreur : ${err.localizedMessage}"
            }
        }
    }

    // Modification du solde & Bankroll sandbox
    fun openCustomBalanceDialog() {
        _isCustomBalanceDialogOpen.value = true
    }

    fun closeCustomBalanceDialog() {
        _isCustomBalanceDialogOpen.value = false
    }

    fun updateWalletBalance(newAmount: Double) {
        viewModelScope.launch {
            repository.updateWalletBalance(newAmount)
            _isCustomBalanceDialogOpen.value = false
            _snackbarMessage.value = "💰 Solde ajusté à ${newAmount.toInt()} KGC !"
        }
    }

    // Sécurité par code PIN
    fun verifyUnlockPin(enteredPin: String): Boolean {
        val actualPin = wallet.value?.pinCode
        return if (actualPin == enteredPin) {
            _isAppLocked.value = false
            true
        } else {
            false
        }
    }

    fun openPinSetupDialog() {
        _isPinSetupDialogOpen.value = true
    }

    fun closePinSetupDialog() {
        _isPinSetupDialogOpen.value = false
    }

    fun savePinCode(newPin: String?, enable: Boolean) {
        viewModelScope.launch {
            repository.setPinCode(newPin, enable)
            _isPinSetupDialogOpen.value = false
            _snackbarMessage.value = if (enable) "🔒 Protection par Code PIN activée" else "🔓 Protection par PIN désactivée"
        }
    }

    fun lockAppNow() {
        if (!wallet.value?.pinCode.isNullOrEmpty()) {
            _isAppLocked.value = true
        }
    }

    fun openTeamDetails(team: TeamEntity) {
        _selectedTeamDetails.value = team
        viewModelScope.launch {
            _squadPlayers.value = repository.getPlayersForTeam(team.id)
        }
    }

    fun closeTeamDetails() {
        _selectedTeamDetails.value = null
        _squadPlayers.value = emptyList()
    }

    fun dismissCrownedAward() {
        _crownedAward.value = null
    }

    fun claimDailyBonus() {
        viewModelScope.launch {
            val res = repository.claimFreeDailyBonus()
            res.onSuccess { bonus ->
                _snackbarMessage.value = "🎁 Bonus quotidien reçu : +${bonus.toInt()} KGC !"
            }
        }
    }

    fun resetBalance() {
        viewModelScope.launch {
            repository.resetWalletBalance(25000.0)
            _snackbarMessage.value = "💰 Portefeuille réinitialisé à 25,000 KGC !"
        }
    }

    fun dismissSnackbar() {
        _snackbarMessage.value = null
    }

    fun getOddsForFixture(fixture: MatchFixtureEntity, teams: List<TeamEntity>): MarketOdds {
        val home = teams.firstOrNull { it.id == fixture.homeTeamId }
            ?: TeamEntity(fixture.homeTeamId, "", "Domicile", "DOM", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
        val away = teams.firstOrNull { it.id == fixture.awayTeamId }
            ?: TeamEntity(fixture.awayTeamId, "", "Extérieur", "EXT", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")

        return if (fixture.status == "SCHEDULED") {
            OddsEngine.calculatePreMatchOdds(home, away)
        } else {
            OddsEngine.calculateLiveOdds(
                home, away, fixture.minute, fixture.homeScore, fixture.awayScore,
                fixture.homeRedCards, fixture.awayRedCards
            )
        }
    }
}
