package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.kgcfootball.ui.components.BetSlipSheet
import com.example.kgcfootball.ui.components.KgcBottomBar
import com.example.kgcfootball.ui.components.KgcTopBar
import com.example.kgcfootball.ui.components.PreMatchAnalysisSheet
import com.example.kgcfootball.ui.components.SimulationStyleDialog
import com.example.kgcfootball.ui.screens.*
import com.example.kgcfootball.ui.viewmodel.AppNavTab
import com.example.kgcfootball.ui.viewmodel.KgcFootballViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PitchBlack

class MainActivity : ComponentActivity() {

    private val viewModel: KgcFootballViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isAppLocked by viewModel.isAppLocked.collectAsStateWithLifecycle()

                if (isAppLocked) {
                    PinLockScreen(
                        onUnlock = { pin -> viewModel.verifyUnlockPin(pin) }
                    )
                } else {
                    KgcFootballApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun KgcFootballApp(viewModel: KgcFootballViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedLeagueId by viewModel.selectedLeagueId.collectAsStateWithLifecycle()
    val selectedRound by viewModel.selectedRound.collectAsStateWithLifecycle()
    val isAutoCycleActive by viewModel.isAutoCycleActive.collectAsStateWithLifecycle()
    val cyclePhase by viewModel.cyclePhase.collectAsStateWithLifecycle()
    val countdownSeconds by viewModel.countdownSeconds.collectAsStateWithLifecycle()
    val simSpeed by viewModel.simSpeed.collectAsStateWithLifecycle()

    val leagues by viewModel.allLeagues.collectAsStateWithLifecycle()
    val teams by viewModel.allTeams.collectAsStateWithLifecycle()
    val fixtures by viewModel.currentFixtures.collectAsStateWithLifecycle()
    val allLiveFixtures by viewModel.allLiveFixtures.collectAsStateWithLifecycle()
    val standings by viewModel.standings.collectAsStateWithLifecycle()
    val topScorers by viewModel.topScorers.collectAsStateWithLifecycle()
    val awards by viewModel.allAwards.collectAsStateWithLifecycle()
    val crownedAward by viewModel.crownedAward.collectAsStateWithLifecycle()
    val wallet by viewModel.wallet.collectAsStateWithLifecycle()
    val tickets by viewModel.allTickets.collectAsStateWithLifecycle()
    val newsList by viewModel.newsList.collectAsStateWithLifecycle()

    val couponSelections by viewModel.couponSelections.collectAsStateWithLifecycle()
    val isCouponOpen by viewModel.isCouponOpen.collectAsStateWithLifecycle()
    val stakeAmount by viewModel.stakeAmount.collectAsStateWithLifecycle()

    val activeLiveDetail by viewModel.activeLiveDetail.collectAsStateWithLifecycle()
    val selectedTeamDetails by viewModel.selectedTeamDetails.collectAsStateWithLifecycle()
    val squadPlayers by viewModel.squadPlayers.collectAsStateWithLifecycle()
    val isCustomBalanceDialogOpen by viewModel.isCustomBalanceDialogOpen.collectAsStateWithLifecycle()
    val isPinSetupDialogOpen by viewModel.isPinSetupDialogOpen.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val simulationStyle by viewModel.simulationStyle.collectAsStateWithLifecycle()
    val isSimulationStyleDialogOpen by viewModel.isSimulationStyleDialogOpen.collectAsStateWithLifecycle()
    val analyzedFixture by viewModel.analyzedFixture.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    // Gestion de la touche retour Android
    BackHandler(enabled = isCouponOpen || selectedTeamDetails != null || analyzedFixture != null || selectedTab != AppNavTab.MATCHES) {
        when {
            analyzedFixture != null -> viewModel.dismissPreMatchAnalysis()
            selectedTeamDetails != null -> viewModel.closeTeamDetails()
            isCouponOpen -> viewModel.setCouponOpen(false)
            selectedTab != AppNavTab.MATCHES -> viewModel.setTab(AppNavTab.MATCHES)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            KgcTopBar(
                leagues = leagues,
                selectedLeagueId = selectedLeagueId,
                onSelectLeague = { viewModel.selectLeague(it) },
                wallet = wallet,
                onWalletClick = { viewModel.setTab(AppNavTab.WALLET) },
                cyclePhase = cyclePhase,
                countdownSeconds = countdownSeconds,
                isAutoCycleActive = isAutoCycleActive,
                onToggleAutoCycle = { viewModel.toggleAutoCycle() },
                onSkipToKickoff = { viewModel.skipToKickoffNow() },
                onLockApp = { viewModel.lockAppNow() },
                simulationStyle = simulationStyle,
                onOpenSimulationStyle = { viewModel.openSimulationStyleDialog() }
            )
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Barre / Sheet du coupon de pari
                BetSlipSheet(
                    isOpen = isCouponOpen,
                    onClose = { viewModel.setCouponOpen(!isCouponOpen) },
                    selections = couponSelections,
                    onRemoveSelection = { viewModel.removeBetSelection(it) },
                    onClearAll = { viewModel.clearCoupon() },
                    stake = stakeAmount,
                    onStakeChange = { viewModel.setStake(it) },
                    walletBalance = wallet?.balance ?: 25000.0,
                    onPlaceBet = { viewModel.placeBet() }
                )

                // Barre de navigation principale
                KgcBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.setTab(it) },
                    couponCount = couponSelections.size,
                    isLiveRunning = cyclePhase == com.example.kgcfootball.ui.viewmodel.CyclePhase.LIVE_IN_PLAY
                )
            }
        },
        containerColor = PitchBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppNavTab.MATCHES -> {
                    MatchesScreen(
                        currentRound = selectedRound,
                        onSelectRound = { viewModel.selectRound(it) },
                        fixtures = fixtures,
                        teams = teams,
                        onSimulateAll = { viewModel.simulateAllRoundMatches() },
                        onOpenLiveMatch = { viewModel.openLiveMatch(it) },
                        couponSelections = couponSelections,
                        onToggleBet = { viewModel.toggleBetSelection(it) },
                        getOddsForFixture = { f, t -> viewModel.getOddsForFixture(f, t) },
                        onNavigateToNews = { viewModel.setTab(AppNavTab.NEWS) },
                        onLaunchMatch = { viewModel.launchMatchImmediately(it) },
                        onOpenAnalysis = { viewModel.openPreMatchAnalysis(it) }
                    )
                }

                AppNavTab.LIVE -> {
                    LiveTrackerScreen(
                        liveDetail = activeLiveDetail,
                        isLiveRunning = cyclePhase == com.example.kgcfootball.ui.viewmodel.CyclePhase.LIVE_IN_PLAY,
                        simSpeed = simSpeed,
                        onTogglePlayPause = { viewModel.toggleAutoCycle() },
                        onStepForward = { viewModel.stepForwardOneMinute() },
                        onSetSpeed = { viewModel.setSimulationSpeed(it) },
                        onFinishInstantly = { viewModel.finishLiveMatchInstantly() },
                        couponSelections = couponSelections,
                        onToggleBet = { viewModel.toggleBetSelection(it) },
                        allLiveMatches = allLiveFixtures,
                        onSelectLiveMatch = { viewModel.openLiveMatch(it) }
                    )
                }

                AppNavTab.SPORTSBOOK -> {
                    SportsbookScreen(
                        fixtures = fixtures,
                        teams = teams,
                        couponSelections = couponSelections,
                        onToggleBet = { viewModel.toggleBetSelection(it) },
                        getOddsForFixture = { f, t -> viewModel.getOddsForFixture(f, t) },
                        onOpenAnalysis = { viewModel.openPreMatchAnalysis(it) }
                    )
                }

                AppNavTab.NEWS -> {
                    NewsScreen(
                        newsList = newsList
                    )
                }

                AppNavTab.STANDINGS -> {
                    StandingsScreen(
                        standings = standings,
                        topScorers = topScorers,
                        awards = awards,
                        selectedTeamDetails = selectedTeamDetails,
                        squadPlayers = squadPlayers,
                        crownedAward = crownedAward,
                        onDismissCrownedAward = { viewModel.dismissCrownedAward() },
                        onSelectTeam = { viewModel.openTeamDetails(it) },
                        onCloseTeamDetails = { viewModel.closeTeamDetails() }
                    )
                }

                AppNavTab.WALLET -> {
                    WalletScreen(
                        wallet = wallet,
                        tickets = tickets,
                        onClaimDailyBonus = { viewModel.claimDailyBonus() },
                        onResetBalance = { viewModel.resetBalance() },
                        onCashout = { viewModel.performCashout(it) },
                        onOpenCustomBalance = { viewModel.openCustomBalanceDialog() },
                        isCustomBalanceOpen = isCustomBalanceDialogOpen,
                        onCloseCustomBalance = { viewModel.closeCustomBalanceDialog() },
                        onApplyCustomBalance = { viewModel.updateWalletBalance(it) },
                        onOpenPinSetup = { viewModel.openPinSetupDialog() },
                        isPinSetupOpen = isPinSetupDialogOpen,
                        onClosePinSetup = { viewModel.closePinSetupDialog() },
                        onSavePin = { pin, enable -> viewModel.savePinCode(pin, enable) }
                    )
                }
            }
        }
    }

    // Sélecteur de style et vitesse de simulation (Express, Moyen 10 min, Temps Réel 90 min)
    if (isSimulationStyleDialogOpen) {
        SimulationStyleDialog(
            currentStyle = simulationStyle,
            onSelectStyle = { viewModel.setSimulationStyle(it) },
            onDismiss = { viewModel.dismissSimulationStyleDialog() }
        )
    }

    // Feuille d'analyse d'avant-match & statistiques complètes
    analyzedFixture?.let { fix ->
        val home = teams.firstOrNull { it.id == fix.homeTeamId }
        val away = teams.firstOrNull { it.id == fix.awayTeamId }
        PreMatchAnalysisSheet(
            fixture = fix,
            homeTeam = home,
            awayTeam = away,
            couponSelections = couponSelections,
            onToggleBet = { viewModel.toggleBetSelection(it) },
            onLaunchImmediately = { viewModel.launchMatchImmediately(it) },
            onDismiss = { viewModel.dismissPreMatchAnalysis() }
        )
    }

    // Dialogue festif de célébration de ticket gagnant (Style Bookmaker)
    val winningTicketAlert by viewModel.winningTicketAlert.collectAsStateWithLifecycle()
    winningTicketAlert?.let { wonTicket ->
        WinningTicketCelebrationDialog(
            ticket = wonTicket,
            onDismiss = { viewModel.dismissWinningAlert() },
            onViewWallet = {
                viewModel.dismissWinningAlert()
                viewModel.setTab(AppNavTab.WALLET)
            }
        )
    }
}

@Composable
fun WinningTicketCelebrationDialog(
    ticket: com.example.kgcfootball.data.model.BetTicketEntity,
    onDismiss: () -> Unit,
    onViewWallet: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = com.example.ui.theme.PitchSurface,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(com.example.ui.theme.OddsGold)
            ),
            modifier = androidx.compose.ui.Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = androidx.compose.ui.Modifier.padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🎉",
                    fontSize = 44.sp
                )
                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
                Text(
                    text = "TICKET GAGNANT !",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                    fontSize = 20.sp,
                    color = com.example.ui.theme.OddsGold
                )
                Text(
                    text = "Félicitations ! Votre pronostic a été validé avec brio.",
                    fontSize = 12.sp,
                    color = com.example.ui.theme.TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = androidx.compose.ui.Modifier.height(16.dp))

                Surface(
                    color = com.example.ui.theme.PitchSurfaceElevated,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = androidx.compose.ui.Modifier.padding(14.dp),
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GAIN CRÉDITÉ",
                            fontSize = 11.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = com.example.ui.theme.StadiumGreen
                        )
                        Text(
                            text = "+${ticket.potentialWin.toInt()} KGC",
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                            fontSize = 28.sp,
                            color = com.example.ui.theme.StadiumGreen
                        )
                        Spacer(modifier = androidx.compose.ui.Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Cote : @ ${ticket.totalOdds}",
                                fontSize = 12.sp,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = com.example.ui.theme.OddsGoldLight
                            )
                            Text(
                                text = "Mise : ${ticket.stake.toInt()} KGC",
                                fontSize = 12.sp,
                                color = com.example.ui.theme.TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = androidx.compose.ui.Modifier.height(18.dp))

                Button(
                    onClick = onViewWallet,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.StadiumGreen,
                        contentColor = Color.Black
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "VOIR MON SOLDE & HISTORIQUE 💰",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Continuer à jouer ⚽",
                        color = com.example.ui.theme.TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
