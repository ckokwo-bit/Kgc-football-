package com.example.kgcfootball.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import com.example.kgcfootball.data.model.BetItemEntity
import com.example.kgcfootball.data.model.MatchEventEntity
import com.example.kgcfootball.data.model.MatchFixtureEntity
import com.example.kgcfootball.ui.viewmodel.LiveMatchDetail
import com.example.ui.theme.*

@Composable
fun LiveTrackerScreen(
    liveDetail: LiveMatchDetail?,
    isLiveRunning: Boolean,
    simSpeed: Int,
    onTogglePlayPause: () -> Unit,
    onStepForward: () -> Unit,
    onSetSpeed: (Int) -> Unit,
    onFinishInstantly: () -> Unit,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    allLiveMatches: List<MatchFixtureEntity> = emptyList(),
    onSelectLiveMatch: (String) -> Unit = {}
) {
    if (liveDetail == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = StadiumGreen,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Aucun match sélectionné en direct",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Rendez-vous dans l'onglet 'Matchs' pour lancer une rencontre.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }
        return
    }

    val fixture = liveDetail.fixture
    val home = liveDetail.homeTeam
    val away = liveDetail.awayTeam
    val events = liveDetail.events
    val odds = liveDetail.liveOdds
    val isFinished = fixture.status == "FINISHED"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 0. Sélecteur de rencontres en direct si plusieurs matchs tournent en parallèle
        if (allLiveMatches.size > 1) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Matchs en direct simultanés (${allLiveMatches.size}) :",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(allLiveMatches) { liveMatch ->
                            val isSelected = liveMatch.id == fixture.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectLiveMatch(liveMatch.id) },
                                label = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(CardRed)
                                        )
                                        Text(
                                            text = "${liveMatch.homeScore} - ${liveMatch.awayScore} (${liveMatch.minute}')",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = StadiumGreenDark,
                                    selectedLabelColor = Color.White,
                                    containerColor = PitchSurfaceElevated,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) StadiumGreen else PitchBorder
                                )
                            )
                        }
                    }
                }
            }
        }

        // 1. Terrain de football 2D avec position de balle dynamique
        item {
            SoccerPitchView(
                ballX = fixture.ballX,
                ballY = fixture.ballY,
                playZone = fixture.playZoneDescription.ifBlank { fixture.lastEventText },
                possessionHome = fixture.homePossession,
                isLiveRunning = isLiveRunning,
                homeName = home.shortName,
                awayName = away.shortName,
                homeColor = Color(home.primaryColorHex),
                awayColor = Color(away.primaryColorHex)
            )
        }

        // 2. Tableau d'affichage & Scoreboard
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (isLiveRunning) StadiumGreen else PitchBorder)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    // Statut & Minute ticker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isFinished) PitchSurfaceElevated else CardRed,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isFinished) "TERMINÉ" else if (fixture.minute == 0) "COUP D'ENVOI" else "EN DIRECT",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = "${fixture.minute}' ${if (fixture.extraTime > 0) "+${fixture.extraTime}'" else ""}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = if (isLiveRunning) StadiumGreen else TextPrimary
                        )

                        Text(
                            text = fixture.leagueId.replace("kgc_", "").uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Ligne des scores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Équipe 1
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(home.primaryColorHex))
                                    .border(1.dp, Color(home.secondaryColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = home.shortName,
                                    fontWeight = FontWeight.Black,
                                    color = if (home.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(home.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("xG: ${fixture.homeXg}", fontSize = 11.sp, color = TextSecondary)
                        }

                        // Score géant
                        Text(
                            text = "${fixture.homeScore}  -  ${fixture.awayScore}",
                            fontWeight = FontWeight.Black,
                            fontSize = 34.sp,
                            color = StadiumGreen
                        )

                        // Équipe 2
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(away.primaryColorHex))
                                    .border(1.dp, Color(away.secondaryColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = away.shortName,
                                    fontWeight = FontWeight.Black,
                                    color = if (away.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(away.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Text("xG: ${fixture.awayXg}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // 3. Barre de contrôle de simulation
        item {
            Surface(
                color = PitchSurfaceElevated,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play / Pause
                    Button(
                        onClick = onTogglePlayPause,
                        enabled = !isFinished,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLiveRunning) CardRed else StadiumGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("play_pause_sim_button")
                    ) {
                        Icon(
                            imageVector = if (isLiveRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isLiveRunning) "Pause" else "Lecture",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLiveRunning) "Pause" else "Lancer",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Pas à pas (+1 min)
                    IconButton(
                        onClick = onStepForward,
                        enabled = !isFinished && !isLiveRunning
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "+1 Minute",
                            tint = if (!isFinished && !isLiveRunning) StadiumGreen else TextMuted
                        )
                    }

                    // Vitesse (1x, 2x, 5x)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(1, 2, 5).forEach { speed ->
                            Surface(
                                color = if (simSpeed == speed) StadiumGreenDark else PitchSurface,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onSetSpeed(speed) }
                            ) {
                                Text(
                                    text = "${speed}x",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (simSpeed == speed) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Terminer instantanément
                    FilledTonalButton(
                        onClick = onFinishInstantly,
                        enabled = !isFinished,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = OddsGoldDark,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("⚡ Fin", fontSize = 11.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 4. Statistiques du match en direct
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Statistiques de la rencontre",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    StatBarRow(
                        title = "Possession",
                        homeValue = "${fixture.homePossession}%",
                        awayValue = "${fixture.awayPossession}%",
                        homePercent = fixture.homePossession / 100f
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    StatBarRow(
                        title = "Tirs (Cadrés)",
                        homeValue = "${fixture.homeShots} (${fixture.homeShotsOnTarget})",
                        awayValue = "${fixture.awayShots} (${fixture.awayShotsOnTarget})",
                        homePercent = calculateRatio(fixture.homeShots, fixture.awayShots)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    StatBarRow(
                        title = "Corners",
                        homeValue = "${fixture.homeCorners}",
                        awayValue = "${fixture.awayCorners}",
                        homePercent = calculateRatio(fixture.homeCorners, fixture.awayCorners)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    StatBarRow(
                        title = "Fautes & Cartons",
                        homeValue = "${fixture.homeFouls} (${fixture.homeYellowCards}🟨 ${fixture.homeRedCards}🟥)",
                        awayValue = "${fixture.awayFouls} (${fixture.awayYellowCards}🟨 ${fixture.awayRedCards}🟥)",
                        homePercent = calculateRatio(fixture.homeFouls, fixture.awayFouls)
                    )
                }
            }
        }

        // 4b. Banc des Entraîneurs & Consignes Tactiques en Direct
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👔 BANC DES ENTRAÎNEURS & TACTIQUE", fontWeight = FontWeight.Black, fontSize = 11.sp, color = OddsGold)
                        Text("${home.formation} vs ${away.formation}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(home.coachName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            Text(home.coachPersonality, fontSize = 10.sp, color = StadiumGreen)
                            Text("« ${home.coachTacticalQuote} »", fontSize = 10.sp, color = TextMuted, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(away.coachName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            Text(away.coachPersonality, fontSize = 10.sp, color = OddsGoldLight)
                            Text("« ${away.coachTacticalQuote} »", fontSize = 10.sp, color = TextMuted, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }
        }

        // 5. Cotes en Direct Complètes (Live Sportsbook)
        if (!isFinished) {
            item {
                var selectedLiveMarket by remember { mutableStateOf("1X2") }
                val liveMarkets = listOf("1X2", "PROCHAIN BUT", "DOUBLE CHANCE", "TOTAL BUTS", "LES 2 MARQUENT", "SCORE EXACT")

                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OddsGoldDark)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "⚡ Cotes en Direct (Live Betting)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = OddsGold
                                )
                            }
                            Surface(color = CardRed, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "${fixture.minute}' LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Filtres de marchés en direct
                        androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(liveMarkets) { m ->
                                val isSel = m == selectedLiveMarket
                                FilterChip(
                                    selected = isSel,
                                    onClick = { selectedLiveMarket = m },
                                    label = { Text(m, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = StadiumGreenDark,
                                        selectedLabelColor = Color.White,
                                        containerColor = PitchSurfaceElevated,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        when (selectedLiveMarket) {
                            "1X2" -> {
                                Text("Résultat du Match (1X2 Live)", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val item1 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2 Live", "1", "${home.shortName} (1)", odds.homeWin)
                                    val itemX = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2 Live", "X", "Nul (X)", odds.draw)
                                    val item2 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2 Live", "2", "${away.shortName} (2)", odds.awayWin)

                                    OddsButton(label = "1", odds = odds.homeWin, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1" }, onClick = { onToggleBet(item1) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "N", odds = odds.draw, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X" }, onClick = { onToggleBet(itemX) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "2", odds = odds.awayWin, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "2" }, onClick = { onToggleBet(item2) }, modifier = Modifier.weight(1f))
                                }
                            }
                            "PROCHAIN BUT" -> {
                                Text("Prochain But (Live in-play)", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val itemNextHome = BetItemEntity("", "", fixture.id, home.name, away.name, "NEXT_GOAL", "Prochain But", "HOME", home.shortName, odds.nextGoalHome)
                                    val itemNextNone = BetItemEntity("", "", fixture.id, home.name, away.name, "NEXT_GOAL", "Prochain But", "NONE", "Aucun", odds.nextGoalNone)
                                    val itemNextAway = BetItemEntity("", "", fixture.id, home.name, away.name, "NEXT_GOAL", "Prochain But", "AWAY", away.shortName, odds.nextGoalAway)

                                    OddsButton(label = home.shortName, odds = odds.nextGoalHome, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "HOME" }, onClick = { onToggleBet(itemNextHome) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "Aucun", odds = odds.nextGoalNone, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "NONE" }, onClick = { onToggleBet(itemNextNone) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = away.shortName, odds = odds.nextGoalAway, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "AWAY" }, onClick = { onToggleBet(itemNextAway) }, modifier = Modifier.weight(1f))
                                }
                            }
                            "DOUBLE CHANCE" -> {
                                Text("Double Chance Live", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val item1X = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance Live", "1X", "1X", odds.doubleChance1X)
                                    val item12 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance Live", "12", "12", odds.doubleChance12)
                                    val itemX2 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance Live", "X2", "X2", odds.doubleChanceX2)

                                    OddsButton(label = "1X", odds = odds.doubleChance1X, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1X" }, onClick = { onToggleBet(item1X) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "12", odds = odds.doubleChance12, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "12" }, onClick = { onToggleBet(item12) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "X2", odds = odds.doubleChanceX2, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X2" }, onClick = { onToggleBet(itemX2) }, modifier = Modifier.weight(1f))
                                }
                            }
                            "TOTAL BUTS" -> {
                                Text("Total Buts +/- 2.5 Live", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val itemOver = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5 Live", "OVER", "+ de 2.5 Buts", odds.over25)
                                    val itemUnder = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5 Live", "UNDER", "- de 2.5 Buts", odds.under25)

                                    OddsButton(label = "+ 2.5 Buts", odds = odds.over25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "OVER" }, onClick = { onToggleBet(itemOver) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "- 2.5 Buts", odds = odds.under25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "UNDER" }, onClick = { onToggleBet(itemUnder) }, modifier = Modifier.weight(1f))
                                }
                            }
                            "LES 2 MARQUENT" -> {
                                Text("Les 2 Équipes Marquent (BTTS Live)", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val itemYes = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent Live", "YES", "Oui", odds.bttsYes)
                                    val itemNo = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent Live", "NO", "Non", odds.bttsNo)

                                    OddsButton(label = "Oui (BTTS)", odds = odds.bttsYes, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "YES" }, onClick = { onToggleBet(itemYes) }, modifier = Modifier.weight(1f))
                                    OddsButton(label = "Non", odds = odds.bttsNo, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "NO" }, onClick = { onToggleBet(itemNo) }, modifier = Modifier.weight(1f))
                                }
                            }
                            "SCORE EXACT" -> {
                                Text("Score Exact (Live)", fontSize = 11.sp, color = TextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                val liveScores = listOf("1-0", "2-0", "2-1", "1-1", "0-1", "1-2")
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    liveScores.take(3).forEach { sc ->
                                        val scOdds = odds.exactScores[sc] ?: 5.50
                                        val itemSc = BetItemEntity("", "", fixture.id, home.name, away.name, "EXACT_SCORE", "Score Exact Live", sc, sc, scOdds)
                                        OddsButton(label = sc, odds = scOdds, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == sc }, onClick = { onToggleBet(itemSc) }, modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. Fil d'actualité du match minute par minute (Commentary)
        item {
            Text(
                text = "Fil des événements du match",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (events.isEmpty()) {
            item {
                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Le match commence. En attente des faits de jeu marquants...",
                        fontSize = 12.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(events.reversed()) { event ->
                EventTimelineRow(event = event)
            }
        }
    }
}

@Composable
fun SoccerPitchView(
    ballX: Float,
    ballY: Float,
    playZone: String,
    possessionHome: Int,
    isLiveRunning: Boolean,
    homeName: String,
    awayName: String,
    homeColor: Color,
    awayColor: Color
) {
    // Animation fluide et physique des coordonnées réelles du ballon
    val animatedBallX by animateFloatAsState(
        targetValue = ballX.coerceIn(0.04f, 0.96f),
        animationSpec = tween(durationMillis = 650, easing = LinearOutSlowInEasing),
        label = "radarBallX"
    )
    val animatedBallY by animateFloatAsState(
        targetValue = ballY.coerceIn(0.08f, 0.92f),
        animationSpec = tween(durationMillis = 650, easing = LinearOutSlowInEasing),
        label = "radarBallY"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "ball_pulse")
    val ballPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ballPulse"
    )

    Surface(
        color = Color(0xFF0C2E18), // Vert pelouse profonde
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StadiumGreenDark)),
        modifier = Modifier
            .fillMaxWidth()
            .height(175.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Dessin du terrain 2D réaliste (lignes, bandes de gazon, joueurs tactiques)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val lineColor = Color(0x66FFFFFF)
                val stroke = Stroke(width = 1.8.dp.toPx())

                // 1. Bandes de tonte de pelouse réalistes
                val stripes = 10
                val stripeWidth = w / stripes
                for (i in 0 until stripes step 2) {
                    drawRect(
                        color = Color(0x15FFFFFF),
                        topLeft = Offset(i * stripeWidth, 0f),
                        size = Size(stripeWidth, h)
                    )
                }

                // 2. Bordures extérieures
                drawRect(
                    color = lineColor,
                    topLeft = Offset(14f, 12f),
                    size = Size(w - 28f, h - 24f),
                    style = stroke
                )

                // 3. Ligne médiane et rond central
                drawLine(
                    color = lineColor,
                    start = Offset(w / 2f, 12f),
                    end = Offset(w / 2f, h - 12f),
                    strokeWidth = 1.8.dp.toPx()
                )
                drawCircle(
                    color = lineColor,
                    radius = 26.dp.toPx(),
                    center = Offset(w / 2f, h / 2f),
                    style = stroke
                )
                // Point central
                drawCircle(
                    color = lineColor,
                    radius = 2.5.dp.toPx(),
                    center = Offset(w / 2f, h / 2f)
                )

                // 4. Arcs de cercle de corner (4 coins)
                val cornerRadius = 8.dp.toPx()
                drawCircle(color = lineColor, radius = cornerRadius, center = Offset(14f, 12f), style = stroke)
                drawCircle(color = lineColor, radius = cornerRadius, center = Offset(14f, h - 12f), style = stroke)
                drawCircle(color = lineColor, radius = cornerRadius, center = Offset(w - 14f, 12f), style = stroke)
                drawCircle(color = lineColor, radius = cornerRadius, center = Offset(w - 14f, h - 12f), style = stroke)

                // 5. Surfaces de réparation (16m50)
                val boxWidth = w * 0.17f
                val boxHeight = h * 0.58f
                val boxTopY = (h - boxHeight) / 2f
                // Domicile (Gauche)
                drawRect(
                    color = lineColor,
                    topLeft = Offset(14f, boxTopY),
                    size = Size(boxWidth, boxHeight),
                    style = stroke
                )
                // Extérieur (Droite)
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 14f - boxWidth, boxTopY),
                    size = Size(boxWidth, boxHeight),
                    style = stroke
                )

                // 6. Surfaces de but (6 mètres)
                val goalBoxWidth = w * 0.07f
                val goalBoxHeight = h * 0.32f
                val goalBoxTopY = (h - goalBoxHeight) / 2f
                drawRect(
                    color = lineColor,
                    topLeft = Offset(14f, goalBoxTopY),
                    size = Size(goalBoxWidth, goalBoxHeight),
                    style = stroke
                )
                drawRect(
                    color = lineColor,
                    topLeft = Offset(w - 14f - goalBoxWidth, goalBoxTopY),
                    size = Size(goalBoxWidth, goalBoxHeight),
                    style = stroke
                )

                // 7. Points de penalty
                val penSpotDist = w * 0.12f
                drawCircle(color = lineColor, radius = 2.2.dp.toPx(), center = Offset(14f + penSpotDist, h / 2f))
                drawCircle(color = lineColor, radius = 2.2.dp.toPx(), center = Offset(w - 14f - penSpotDist, h / 2f))

                // 8. Joueurs tactiques 2D animés dynamiquement (les 22 joueurs bougent selon le ballon et la phase tactique !)
                val homeShiftX = (animatedBallX - 0.50f)
                val homeShiftY = (animatedBallY - 0.50f)
                val awayShiftX = (0.50f - animatedBallX)
                val awayShiftY = (animatedBallY - 0.50f)

                // Équipe domicile (Gauche, attaque vers la droite)
                val homePlayerPositions = listOf(
                    // Gardien
                    Offset(w * (0.05f + (homeShiftX * 0.05f).coerceAtLeast(0f)), h * (0.50f + homeShiftY * 0.14f)),
                    // 4 Défenseurs
                    Offset(w * (0.18f + (homeShiftX * 0.24f).coerceIn(-0.06f, 0.22f)), h * (0.20f + homeShiftY * 0.10f)),
                    Offset(w * (0.16f + (homeShiftX * 0.22f).coerceIn(-0.06f, 0.20f)), h * (0.38f + homeShiftY * 0.08f)),
                    Offset(w * (0.16f + (homeShiftX * 0.22f).coerceIn(-0.06f, 0.20f)), h * (0.62f + homeShiftY * 0.08f)),
                    Offset(w * (0.18f + (homeShiftX * 0.24f).coerceIn(-0.06f, 0.22f)), h * (0.80f + homeShiftY * 0.10f)),
                    // 3 Milieux de terrain
                    Offset(w * (0.32f + (homeShiftX * 0.32f).coerceIn(-0.10f, 0.28f)), h * (0.32f + homeShiftY * 0.15f)),
                    Offset(w * (0.30f + (homeShiftX * 0.30f).coerceIn(-0.10f, 0.25f)), h * (0.50f + homeShiftY * 0.12f)),
                    Offset(w * (0.32f + (homeShiftX * 0.32f).coerceIn(-0.10f, 0.28f)), h * (0.68f + homeShiftY * 0.15f)),
                    // 3 Attaquants
                    Offset(w * (0.46f + (homeShiftX * 0.40f).coerceIn(-0.14f, 0.38f)), h * (0.22f + homeShiftY * 0.20f)),
                    Offset(w * (0.48f + (homeShiftX * 0.42f).coerceIn(-0.14f, 0.40f)), h * (0.50f + homeShiftY * 0.22f)),
                    Offset(w * (0.46f + (homeShiftX * 0.40f).coerceIn(-0.14f, 0.38f)), h * (0.78f + homeShiftY * 0.20f))
                )
                for (pos in homePlayerPositions) {
                    drawCircle(color = Color(0xCC000000), radius = 4.2.dp.toPx(), center = pos)
                    drawCircle(color = homeColor, radius = 3.2.dp.toPx(), center = pos)
                }

                // Équipe extérieur (Droite, attaque vers la gauche)
                val awayPlayerPositions = listOf(
                    // Gardien
                    Offset(w * (0.95f - (awayShiftX * 0.05f).coerceAtLeast(0f)), h * (0.50f + awayShiftY * 0.14f)),
                    // 4 Défenseurs
                    Offset(w * (0.82f - (awayShiftX * 0.24f).coerceIn(-0.06f, 0.22f)), h * (0.20f + awayShiftY * 0.10f)),
                    Offset(w * (0.84f - (awayShiftX * 0.22f).coerceIn(-0.06f, 0.20f)), h * (0.38f + awayShiftY * 0.08f)),
                    Offset(w * (0.84f - (awayShiftX * 0.22f).coerceIn(-0.06f, 0.20f)), h * (0.62f + awayShiftY * 0.08f)),
                    Offset(w * (0.82f - (awayShiftX * 0.24f).coerceIn(-0.06f, 0.22f)), h * (0.80f + awayShiftY * 0.10f)),
                    // 3 Milieux de terrain
                    Offset(w * (0.68f - (awayShiftX * 0.32f).coerceIn(-0.10f, 0.28f)), h * (0.32f + awayShiftY * 0.15f)),
                    Offset(w * (0.70f - (awayShiftX * 0.30f).coerceIn(-0.10f, 0.25f)), h * (0.50f + awayShiftY * 0.12f)),
                    Offset(w * (0.68f - (awayShiftX * 0.32f).coerceIn(-0.10f, 0.28f)), h * (0.68f + awayShiftY * 0.15f)),
                    // 3 Attaquants
                    Offset(w * (0.54f - (awayShiftX * 0.40f).coerceIn(-0.14f, 0.38f)), h * (0.22f + awayShiftY * 0.20f)),
                    Offset(w * (0.52f - (awayShiftX * 0.42f).coerceIn(-0.14f, 0.40f)), h * (0.50f + awayShiftY * 0.22f)),
                    Offset(w * (0.54f - (awayShiftX * 0.40f).coerceIn(-0.14f, 0.38f)), h * (0.78f + awayShiftY * 0.20f))
                )
                for (pos in awayPlayerPositions) {
                    drawCircle(color = Color(0xCC000000), radius = 4.2.dp.toPx(), center = pos)
                    drawCircle(color = awayColor, radius = 3.2.dp.toPx(), center = pos)
                }

                // 9. Coordonnées réelles animées du ballon
                val currentBallX = w * animatedBallX
                val currentBallY = h * animatedBallY

                // Trajectoire / Ligne de passe subtile vers le ballon
                val prevX = w * (0.50f + (animatedBallX - 0.50f) * 0.6f)
                val prevY = h * (0.50f + (animatedBallY - 0.50f) * 0.6f)
                drawLine(
                    color = Color(0x44FFFFFF),
                    start = Offset(prevX, prevY),
                    end = Offset(currentBallX, currentBallY),
                    strokeWidth = 2.dp.toPx()
                )

                // Halo lumineux du ballon
                if (isLiveRunning) {
                    drawCircle(
                        color = Color(0x6600E676),
                        radius = 13.dp.toPx() * ballPulse,
                        center = Offset(currentBallX, currentBallY)
                    )
                }

                // Ombre du ballon
                drawCircle(
                    color = Color(0x77000000),
                    radius = 5.dp.toPx(),
                    center = Offset(currentBallX + 1.5f, currentBallY + 2f)
                )

                // Ballon de football blanc
                drawCircle(
                    color = Color.White,
                    radius = 5.2.dp.toPx(),
                    center = Offset(currentBallX, currentBallY)
                )
                // Couture centrale du ballon
                drawCircle(
                    color = Color.Black,
                    radius = 2.2.dp.toPx(),
                    center = Offset(currentBallX, currentBallY)
                )
            }

            // Bandeau supérieur : Équipes & Possession
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xD00A160F),
                    shape = RoundedCornerShape(6.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (possessionHome > 50) StadiumGreen else PitchBorder))
                ) {
                    Text(
                        text = "◄ $homeName (${possessionHome}%)",
                        color = homeColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = Color(0xD00A160F),
                    shape = RoundedCornerShape(6.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(if (possessionHome <= 50) OddsGold else PitchBorder))
                ) {
                    Text(
                        text = "(${100 - possessionHome}%) $awayName ►",
                        color = awayColor,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            // Bandeau inférieur : Zone de jeu en temps réel / Commentaire Radar cohérent
            Surface(
                color = Color(0xE007130B),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isLiveRunning) StadiumGreen else TextMuted)
                        )
                        Text(
                            text = if (playZone.isNotBlank()) playZone else "Phase de jeu en cours",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = "RADAR 2D",
                        color = StadiumGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun StatBarRow(
    title: String,
    homeValue: String,
    awayValue: String,
    homePercent: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(homeValue, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = StadiumGreen)
            Text(title, fontSize = 11.sp, color = TextSecondary)
            Text(awayValue, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OddsGold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PitchDark)
        ) {
            Box(
                modifier = Modifier
                    .weight(homePercent.coerceIn(0.1f, 0.9f))
                    .fillMaxHeight()
                    .background(StadiumGreen)
            )
            Box(
                modifier = Modifier
                    .weight((1f - homePercent).coerceIn(0.1f, 0.9f))
                    .fillMaxHeight()
                    .background(OddsGold)
            )
        }
    }
}

@Composable
fun EventTimelineRow(event: MatchEventEntity) {
    val (typeBg, typeLabel) = when (event.type) {
        "GOAL", "PENALTY_GOAL" -> Pair(StadiumGreenDark, "⚽ BUT")
        "YELLOW_CARD" -> Pair(Color(0xFF8A6D00), "🟨 CARTON")
        "RED_CARD" -> Pair(CardRed, "🟥 ROUGE")
        "SUBSTITUTION" -> Pair(PitchSurfaceElevated, "🔄 REMPL.")
        "INJURY" -> Pair(Color(0xFF8B1E3F), "🏥 BLESSURE")
        "COACH_INSTRUCTION" -> Pair(Color(0xFF1E3A8A), "⚡ COACH")
        "VAR_DECISION" -> Pair(Color(0xFF4C1D95), "🖥️ VAR")
        "WOODWORK" -> Pair(Color(0xFF9A3412), "💥 POTEAU")
        "BIG_SAVE" -> Pair(Color(0xFF065F46), "🧤 ARRÊT")
        else -> Pair(PitchSurfaceElevated, "⏱️ ÉVÉNEMENT")
    }

    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (event.type in listOf("GOAL", "PENALTY_GOAL")) StadiumGreen else PitchBorder
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Minute
            Surface(
                color = PitchSurfaceElevated,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "${event.minute}'",
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    color = StadiumGreen,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            // Type Badge
            Surface(
                color = typeBg,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = typeLabel,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            // Description
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.description,
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontWeight = if (event.type == "GOAL" || event.type == "PENALTY_GOAL") FontWeight.Bold else FontWeight.Normal
                )
                if (event.scoreAtEvent.isNotBlank()) {
                    Text(
                        text = "Score : ${event.scoreAtEvent}",
                        fontSize = 10.sp,
                        color = OddsGoldLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun calculateRatio(a: Int, b: Int): Float {
    val total = a + b
    if (total == 0) return 0.5f
    return (a.toFloat() / total.toFloat()).coerceIn(0.1f, 0.9f)
}
