package com.example.kgcfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kgcfootball.data.model.BetItemEntity
import com.example.kgcfootball.data.model.MatchFixtureEntity
import com.example.kgcfootball.data.model.TeamEntity
import com.example.kgcfootball.engine.MarketOdds
import com.example.ui.theme.*

@Composable
fun MatchesScreen(
    currentRound: Int,
    onSelectRound: (Int) -> Unit,
    fixtures: List<MatchFixtureEntity>,
    teams: List<TeamEntity>,
    onSimulateAll: () -> Unit,
    onOpenLiveMatch: (String) -> Unit,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    getOddsForFixture: (MatchFixtureEntity, List<TeamEntity>) -> MarketOdds,
    onNavigateToNews: (() -> Unit)? = null,
    onLaunchMatch: (String) -> Unit = {},
    onOpenAnalysis: (MatchFixtureEntity) -> Unit = {}
) {
    var selectedMarketCategory by remember { mutableStateOf(MarketCategory.RESULT_1X2) }
    var matchForFullOdds by remember { mutableStateOf<Triple<MatchFixtureEntity, TeamEntity, TeamEntity>?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Bandeau Actualités & Mercato KGC en Direct
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToNews?.invoke() }
                    .testTag("news_banner_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            color = CardRed,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ACTUS",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "🔥 Mercato & Déclarations d'après-match",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = TextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Consultez les dernières rumeurs, transferts et analyses",
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 1
                            )
                        }
                    }

                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Lire", fontSize = 11.sp, color = StadiumGreen, fontWeight = FontWeight.Bold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = StadiumGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 2. Contrôle de la journée et Bouton de simulation globale
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sélecteur de journée
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (currentRound > 1) onSelectRound(currentRound - 1) },
                            enabled = currentRound > 1
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Journée précédente",
                                tint = if (currentRound > 1) TextPrimary else TextMuted
                            )
                        }
                        Text(
                            text = "Journée $currentRound",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        IconButton(
                            onClick = { if (currentRound < 7) onSelectRound(currentRound + 1) },
                            enabled = currentRound < 7
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Journée suivante",
                                tint = if (currentRound < 7) TextPrimary else TextMuted
                            )
                        }
                    }

                    // Bouton Simuler toute la journée
                    Button(
                        onClick = onSimulateAll,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StadiumGreenDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("simulate_round_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text("Simuler tout", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 3. Sélecteur rapide de marché pour les cartes de match
        item {
            Column {
                Text(
                    text = "Marché affiché sur les cartes :",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(MarketCategory.values()) { category ->
                        val isSelected = category == selectedMarketCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMarketCategory = category },
                            label = {
                                Text(
                                    text = category.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
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

        // 4. Cartes des matchs
        if (fixtures.isEmpty()) {
            item {
                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = StadiumGreen,
                            modifier = Modifier.size(32.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Chargement des rencontres de la Journée $currentRound...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Préparation des cotes, compositions et calendrier réaliste.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(fixtures) { fixture ->
                val home = teams.firstOrNull { it.id == fixture.homeTeamId }
                    ?: TeamEntity(fixture.homeTeamId, "", "Domicile", "DOM", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
                val away = teams.firstOrNull { it.id == fixture.awayTeamId }
                    ?: TeamEntity(fixture.awayTeamId, "", "Extérieur", "EXT", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")

                val odds = getOddsForFixture(fixture, teams)

                MatchCard(
                    fixture = fixture,
                    home = home,
                    away = away,
                    odds = odds,
                    selectedCategory = selectedMarketCategory,
                    couponSelections = couponSelections,
                    onToggleBet = onToggleBet,
                    onOpenLive = { onOpenLiveMatch(fixture.id) },
                    onOpenAllOdds = { matchForFullOdds = Triple(fixture, home, away) },
                    onLaunchMatch = onLaunchMatch,
                    onOpenAnalysis = onOpenAnalysis
                )
            }
        }
    }

    // Dialogue complet de tous les marchés de cotes pour un match
    matchForFullOdds?.let { (fixture, home, away) ->
        val odds = getOddsForFixture(fixture, teams)
        AllOddsDialog(
            fixture = fixture,
            home = home,
            away = away,
            odds = odds,
            couponSelections = couponSelections,
            onToggleBet = onToggleBet,
            onDismiss = { matchForFullOdds = null }
        )
    }
}

@Composable
fun MatchCard(
    fixture: MatchFixtureEntity,
    home: TeamEntity,
    away: TeamEntity,
    odds: MarketOdds,
    selectedCategory: MarketCategory,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    onOpenLive: () -> Unit,
    onOpenAllOdds: () -> Unit,
    onLaunchMatch: (String) -> Unit = {},
    onOpenAnalysis: (MatchFixtureEntity) -> Unit = {}
) {
    val isLive = fixture.status == "LIVE"
    val isFinished = fixture.status == "FINISHED"

    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isLive) StadiumGreen else PitchBorder)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${fixture.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Statut du match & Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    when {
                        isLive -> {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CardRed)
                            )
                            Text(
                                text = "EN DIRECT ${fixture.minute}'",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = CardRed
                            )
                        }
                        isFinished -> {
                            Text(
                                text = "TERMINÉ",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                        else -> {
                            val min = fixture.countdownSecondsRemaining / 60
                            val sec = fixture.countdownSecondsRemaining % 60
                            val countdownStr = String.format("%02d:%02d", min, sec)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = OddsGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "${fixture.scheduledKickoffTime} (dans $countdownStr)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = OddsGoldLight
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Bouton Lancer immédiatement (si programmé)
                    if (!isFinished && !isLive) {
                        FilledTonalButton(
                            onClick = { onLaunchMatch(fixture.id) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = StadiumGreenDark,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.testTag("launch_match_${fixture.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Text("Lancer ▶", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Bouton Stats & Analyse d'avant-match
                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenAnalysis(fixture) }
                            .testTag("match_stats_btn_${fixture.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = StadiumGreen, modifier = Modifier.size(13.dp))
                            Text("Stats 📊", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }

                    // Bouton + de cotes (Tous les marchés)
                    if (!isFinished) {
                        Surface(
                            color = OddsButtonBg,
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenAllOdds() }
                        ) {
                            Text(
                                text = "+18 Cotes >",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OddsGoldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Bouton Suivre en direct
                    FilledTonalButton(
                        onClick = onOpenLive,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = PitchSurfaceElevated,
                            contentColor = StadiumGreen
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(14.dp))
                            Text(
                                text = if (isLive) "Suivre (${fixture.minute}')" else "Direct",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Score et Équipes
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Équipe Domicile
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(home.primaryColorHex))
                            .border(1.dp, Color(home.secondaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = home.shortName,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = if (home.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = home.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${home.overallRating} GEN",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }

                // Panneau central de score
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    if (isFinished || isLive) {
                        Text(
                            text = "${fixture.homeScore} - ${fixture.awayScore}",
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = if (isLive) StadiumGreen else TextPrimary
                        )
                    } else {
                        Text(
                            text = "VS",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = TextMuted
                        )
                    }
                    Text(
                        text = home.stadiumName,
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1
                    )
                }

                // Équipe Extérieur
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(away.primaryColorHex))
                            .border(1.dp, Color(away.secondaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = away.shortName,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = if (away.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = away.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = "${away.overallRating} GEN",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Commentaire du match
            if (fixture.lastEventText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = PitchDark,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = fixture.lastEventText,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        maxLines = 1
                    )
                }
            }

            // Cotes dynamiques selon le marché sélectionné
            if (!isFinished) {
                Spacer(modifier = Modifier.height(10.dp))
                when (selectedCategory) {
                    MarketCategory.RESULT_1X2 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val item1 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "1", "${home.shortName} (1)", odds.homeWin)
                            val itemX = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "X", "Nul (X)", odds.draw)
                            val item2 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "2", "${away.shortName} (2)", odds.awayWin)

                            OddsButton(
                                label = "1",
                                odds = odds.homeWin,
                                isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1" },
                                onClick = { onToggleBet(item1) },
                                modifier = Modifier.weight(1f)
                            )
                            OddsButton(
                                label = "N",
                                odds = odds.draw,
                                isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X" },
                                onClick = { onToggleBet(itemX) },
                                modifier = Modifier.weight(1f)
                            )
                            OddsButton(
                                label = "2",
                                odds = odds.awayWin,
                                isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "2" },
                                onClick = { onToggleBet(item2) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    MarketCategory.DOUBLE_CHANCE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val item1X = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "1X", "1X", odds.doubleChance1X)
                            val item12 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "12", "12", odds.doubleChance12)
                            val itemX2 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "X2", "X2", odds.doubleChanceX2)

                            OddsButton(label = "1X", odds = odds.doubleChance1X, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1X" }, onClick = { onToggleBet(item1X) }, modifier = Modifier.weight(1f))
                            OddsButton(label = "12", odds = odds.doubleChance12, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "12" }, onClick = { onToggleBet(item12) }, modifier = Modifier.weight(1f))
                            OddsButton(label = "X2", odds = odds.doubleChanceX2, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X2" }, onClick = { onToggleBet(itemX2) }, modifier = Modifier.weight(1f))
                        }
                    }

                    MarketCategory.OVER_UNDER_2_5 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val itemOver = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "OVER", "+ de 2.5 Buts", odds.over25)
                            val itemUnder = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "UNDER", "- de 2.5 Buts", odds.under25)

                            OddsButton(label = "+ 2.5 Buts", odds = odds.over25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "OVER" }, onClick = { onToggleBet(itemOver) }, modifier = Modifier.weight(1f))
                            OddsButton(label = "- 2.5 Buts", odds = odds.under25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "UNDER" }, onClick = { onToggleBet(itemUnder) }, modifier = Modifier.weight(1f))
                        }
                    }

                    MarketCategory.BTTS -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val itemYes = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "YES", "Oui (BTTS)", odds.bttsYes)
                            val itemNo = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "NO", "Non", odds.bttsNo)

                            OddsButton(label = "Oui (BTTS)", odds = odds.bttsYes, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "YES" }, onClick = { onToggleBet(itemYes) }, modifier = Modifier.weight(1f))
                            OddsButton(label = "Non", odds = odds.bttsNo, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "NO" }, onClick = { onToggleBet(itemNo) }, modifier = Modifier.weight(1f))
                        }
                    }

                    MarketCategory.HANDICAP -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val itemH1 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "HOME_MINUS_1", "${home.shortName} (-1.5)", odds.handicapHomeMinus1)
                            val itemH2 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "AWAY_PLUS_1", "${away.shortName} (+1.5)", odds.handicapAwayPlus1)

                            OddsButton(label = "${home.shortName} (-1.5)", odds = odds.handicapHomeMinus1, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "HOME_MINUS_1" }, onClick = { onToggleBet(itemH1) }, modifier = Modifier.weight(1f))
                            OddsButton(label = "${away.shortName} (+1.5)", odds = odds.handicapAwayPlus1, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "AWAY_PLUS_1" }, onClick = { onToggleBet(itemH2) }, modifier = Modifier.weight(1f))
                        }
                    }

                    MarketCategory.EXACT_SCORE -> {
                        val popularScores = listOf("1-0", "2-0", "2-1", "1-1", "0-1", "1-2")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            popularScores.take(3).forEach { score ->
                                val scoreOdds = odds.exactScores[score] ?: 6.50
                                val itemScore = BetItemEntity("", "", fixture.id, home.name, away.name, "EXACT_SCORE", "Score Exact", score, score, scoreOdds)
                                OddsButton(label = score, odds = scoreOdds, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == score }, onClick = { onToggleBet(itemScore) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AllOddsDialog(
    fixture: MatchFixtureEntity,
    home: TeamEntity,
    away: TeamEntity,
    odds: MarketOdds,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = PitchDark,
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // En-tête
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${home.name} vs ${away.name}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tous les marchés de cotes • Modèle Poisson",
                            fontSize = 11.sp,
                            color = StadiumGreen
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // 1. Résultat (1X2)
                    item {
                        MarketSectionBlock(title = "Résultat du Match (1X2)") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val item1 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "1", "${home.shortName} (1)", odds.homeWin)
                                val itemX = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "X", "Nul (X)", odds.draw)
                                val item2 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "Résultat", "2", "${away.shortName} (2)", odds.awayWin)

                                OddsButton(label = "1", odds = odds.homeWin, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1" }, onClick = { onToggleBet(item1) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "N", odds = odds.draw, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X" }, onClick = { onToggleBet(itemX) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "2", odds = odds.awayWin, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "2" }, onClick = { onToggleBet(item2) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 2. Double Chance
                    item {
                        MarketSectionBlock(title = "Double Chance") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val item1X = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "1X", "1X", odds.doubleChance1X)
                                val item12 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "12", "12", odds.doubleChance12)
                                val itemX2 = BetItemEntity("", "", fixture.id, home.name, away.name, "DOUBLE_CHANCE", "Double Chance", "X2", "X2", odds.doubleChanceX2)

                                OddsButton(label = "1X", odds = odds.doubleChance1X, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1X" }, onClick = { onToggleBet(item1X) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "12", odds = odds.doubleChance12, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "12" }, onClick = { onToggleBet(item12) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "X2", odds = odds.doubleChanceX2, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X2" }, onClick = { onToggleBet(itemX2) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 3. Total Buts (+/- 2.5 & +/- 1.5)
                    item {
                        MarketSectionBlock(title = "Total Buts (+/- 2.5)") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val itemOver = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "OVER", "+ de 2.5", odds.over25)
                                val itemUnder = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "UNDER", "- de 2.5", odds.under25)

                                OddsButton(label = "+ 2.5 Buts", odds = odds.over25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "OVER" }, onClick = { onToggleBet(itemOver) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "- 2.5 Buts", odds = odds.under25, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "UNDER" }, onClick = { onToggleBet(itemUnder) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 4. Les Deux Équipes Marquent (BTTS)
                    item {
                        MarketSectionBlock(title = "Les Deux Équipes Marquent (BTTS)") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val itemYes = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "YES", "Oui", odds.bttsYes)
                                val itemNo = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "NO", "Non", odds.bttsNo)

                                OddsButton(label = "Oui (BTTS)", odds = odds.bttsYes, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "YES" }, onClick = { onToggleBet(itemYes) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "Non", odds = odds.bttsNo, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "NO" }, onClick = { onToggleBet(itemNo) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 5. Handicap (+/- 1.5)
                    item {
                        MarketSectionBlock(title = "Handicap") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val itemH1 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "HOME_MINUS_1", "${home.shortName} (-1.5)", odds.handicapHomeMinus1)
                                val itemH2 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "AWAY_PLUS_1", "${away.shortName} (+1.5)", odds.handicapAwayPlus1)

                                OddsButton(label = "${home.shortName} (-1.5)", odds = odds.handicapHomeMinus1, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "HOME_MINUS_1" }, onClick = { onToggleBet(itemH1) }, modifier = Modifier.weight(1f))
                                OddsButton(label = "${away.shortName} (+1.5)", odds = odds.handicapAwayPlus1, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "AWAY_PLUS_1" }, onClick = { onToggleBet(itemH2) }, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    // 6. Score Exact
                    item {
                        MarketSectionBlock(title = "Score Exact") {
                            val popularScores = listOf("1-0", "2-0", "2-1", "1-1", "0-1", "1-2", "0-0", "2-2", "3-1")
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                popularScores.chunked(3).forEach { rowScores ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        rowScores.forEach { score ->
                                            val scoreOdds = odds.exactScores[score] ?: 7.00
                                            val itemScore = BetItemEntity("", "", fixture.id, home.name, away.name, "EXACT_SCORE", "Score Exact", score, score, scoreOdds)
                                            OddsButton(label = score, odds = scoreOdds, isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == score }, onClick = { onToggleBet(itemScore) }, modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = StadiumGreen, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Valider les sélections", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MarketSectionBlock(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
fun OddsButton(
    label: String,
    odds: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) StadiumGreen else OddsButtonBg,
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) StadiumGreen else PitchBorder)
        ),
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .testTag("odds_button_${label}_$odds")
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (isSelected) Color.Black else TextSecondary,
                maxLines = 1
            )
            Text(
                text = "$odds",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = if (isSelected) Color.Black else OddsGoldLight
            )
        }
    }
}
