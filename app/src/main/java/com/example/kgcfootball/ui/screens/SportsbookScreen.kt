package com.example.kgcfootball.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.kgcfootball.data.model.BetItemEntity
import com.example.kgcfootball.data.model.MatchFixtureEntity
import com.example.kgcfootball.data.model.TeamEntity
import com.example.kgcfootball.engine.MarketOdds
import com.example.ui.theme.*

enum class MarketCategory(val label: String) {
    RESULT_1X2("1X2 (Résultat)"),
    DOUBLE_CHANCE("Double Chance"),
    OVER_UNDER_2_5("Total Buts +/- 2.5"),
    BTTS("Les 2 Marquent"),
    HANDICAP("Handicap +/- 1.5"),
    EXACT_SCORE("Score Exact")
}

@Composable
fun SportsbookScreen(
    fixtures: List<MatchFixtureEntity>,
    teams: List<TeamEntity>,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    getOddsForFixture: (MatchFixtureEntity, List<TeamEntity>) -> MarketOdds,
    onOpenAnalysis: (MatchFixtureEntity) -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf(MarketCategory.RESULT_1X2) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // En-tête explicatif
        item {
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Sportsbook KGC • Paris Fictifs",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Cotes mathématiques calculées en temps réel d'après le modèle de Poisson. 100% monnaie virtuelle.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Filtre de marché
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MarketCategory.values()) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category.label,
                                fontSize = 12.sp,
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

        // Liste des matchs avec le marché sélectionné
        val activeFixtures = fixtures.filter { it.status != "FINISHED" }

        // Carte Combiné Recommandé (Super Cote Boostée)
        if (activeFixtures.size >= 2) {
            item {
                val candidateMatches = activeFixtures.take(3)
                val comboPicks = candidateMatches.map { fix ->
                    val h = teams.firstOrNull { it.id == fix.homeTeamId }
                        ?: TeamEntity(fix.homeTeamId, "", "Domicile", "DOM", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
                    val a = teams.firstOrNull { it.id == fix.awayTeamId }
                        ?: TeamEntity(fix.awayTeamId, "", "Extérieur", "EXT", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
                    val o = getOddsForFixture(fix, teams)
                    if (o.homeWin <= o.awayWin) {
                        BetItemEntity("", "", fix.id, h.name, a.name, "1X2", "1X2", "1", "${h.shortName} (1)", o.homeWin)
                    } else {
                        BetItemEntity("", "", fix.id, h.name, a.name, "1X2", "1X2", "2", "${a.shortName} (2)", o.awayWin)
                    }
                }
                var rawOdds = 1.0
                comboPicks.forEach { rawOdds *= it.odds }
                val boostedOdds = Math.round(rawOdds * 1.15 * 100) / 100.0

                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OddsGold)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recommended_combo_card")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🔥", fontSize = 16.sp)
                                Text(
                                    text = "COMBINÉ EXPRESS DES FAVORIS",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = OddsGold
                                )
                            }
                            Surface(
                                color = StadiumGreenDark,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "+15% BOOST",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        comboPicks.forEach { pick ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "• ${pick.homeTeamName} vs ${pick.awayTeamName} : ${pick.selectionLabel}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                                Text(
                                    text = "@ ${pick.odds}",
                                    fontSize = 12.sp,
                                    color = OddsGoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Cote Boostée :", fontSize = 11.sp, color = TextSecondary)
                                Text("@ $boostedOdds", fontSize = 18.sp, fontWeight = FontWeight.Black, color = StadiumGreen)
                            }

                            Button(
                                onClick = {
                                    comboPicks.forEach { pick ->
                                        if (!couponSelections.any { it.matchId == pick.matchId && it.selectionKey == pick.selectionKey }) {
                                            onToggleBet(pick)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StadiumGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Ajouter au Coupon ⚡", fontSize = 12.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        if (activeFixtures.isEmpty()) {
            item {
                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Tous les matchs de cette journée sont terminés.\nSélectionnez une autre journée pour parier !",
                            fontSize = 13.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(activeFixtures) { fixture ->
                val home = teams.firstOrNull { it.id == fixture.homeTeamId }
                    ?: TeamEntity(fixture.homeTeamId, "", "Domicile", "DOM", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")
                val away = teams.firstOrNull { it.id == fixture.awayTeamId }
                    ?: TeamEntity(fixture.awayTeamId, "", "Extérieur", "EXT", "", 0, 0, 85, 85, 85, 85, "EQUILIBRE", "4-3-3")

                val odds = getOddsForFixture(fixture, teams)

                SportsbookMatchCard(
                    fixture = fixture,
                    home = home,
                    away = away,
                    odds = odds,
                    category = selectedCategory,
                    couponSelections = couponSelections,
                    onToggleBet = onToggleBet,
                    onOpenAnalysis = onOpenAnalysis
                )
            }
        }
    }
}

@Composable
fun SportsbookMatchCard(
    fixture: MatchFixtureEntity,
    home: TeamEntity,
    away: TeamEntity,
    odds: MarketOdds,
    category: MarketCategory,
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    onOpenAnalysis: (MatchFixtureEntity) -> Unit = {}
) {
    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Affiche du match
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${home.name} vs ${away.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Text(
                        text = if (fixture.status == "LIVE") "EN DIRECT ${fixture.minute}' • ${fixture.homeScore}-${fixture.awayScore}" else "Avant-match",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (fixture.status == "LIVE") CardRed else StadiumGreen
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(6.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onOpenAnalysis(fixture) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = StadiumGreen, modifier = Modifier.size(12.dp))
                            Text(
                                text = "Stats 📊",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }

                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${home.overallRating} vs ${away.overallRating}",
                            fontSize = 10.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Affichage des options selon la catégorie sélectionnée
            when (category) {
                MarketCategory.RESULT_1X2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val item1 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2", "1", "${home.shortName} (1)", odds.homeWin)
                        val itemX = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2", "X", "Nul (X)", odds.draw)
                        val item2 = BetItemEntity("", "", fixture.id, home.name, away.name, "1X2", "1X2", "2", "${away.shortName} (2)", odds.awayWin)

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

                        OddsButton(
                            label = "1X",
                            odds = odds.doubleChance1X,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1X" },
                            onClick = { onToggleBet(item1X) },
                            modifier = Modifier.weight(1f)
                        )
                        OddsButton(
                            label = "12",
                            odds = odds.doubleChance12,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "12" },
                            onClick = { onToggleBet(item12) },
                            modifier = Modifier.weight(1f)
                        )
                        OddsButton(
                            label = "X2",
                            odds = odds.doubleChanceX2,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X2" },
                            onClick = { onToggleBet(itemX2) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                MarketCategory.OVER_UNDER_2_5 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val itemOver = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "OVER", "+ de 2.5 Buts", odds.over25)
                        val itemUnder = BetItemEntity("", "", fixture.id, home.name, away.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "UNDER", "- de 2.5 Buts", odds.under25)

                        OddsButton(
                            label = "+ 2.5 Buts",
                            odds = odds.over25,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "OVER" },
                            onClick = { onToggleBet(itemOver) },
                            modifier = Modifier.weight(1f)
                        )
                        OddsButton(
                            label = "- 2.5 Buts",
                            odds = odds.under25,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "UNDER" },
                            onClick = { onToggleBet(itemUnder) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                MarketCategory.BTTS -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val itemYes = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "YES", "Oui (BTTS)", odds.bttsYes)
                        val itemNo = BetItemEntity("", "", fixture.id, home.name, away.name, "BTTS", "Les 2 Marquent", "NO", "Non", odds.bttsNo)

                        OddsButton(
                            label = "Oui (Les 2)",
                            odds = odds.bttsYes,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "YES" },
                            onClick = { onToggleBet(itemYes) },
                            modifier = Modifier.weight(1f)
                        )
                        OddsButton(
                            label = "Non",
                            odds = odds.bttsNo,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "NO" },
                            onClick = { onToggleBet(itemNo) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                MarketCategory.HANDICAP -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val itemH1 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "HOME_MINUS_1", "${home.shortName} (-1.5)", odds.handicapHomeMinus1)
                        val itemH2 = BetItemEntity("", "", fixture.id, home.name, away.name, "HANDICAP", "Handicap", "AWAY_PLUS_1", "${away.shortName} (+1.5)", odds.handicapAwayPlus1)

                        OddsButton(
                            label = "${home.shortName} (-1.5)",
                            odds = odds.handicapHomeMinus1,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "HOME_MINUS_1" },
                            onClick = { onToggleBet(itemH1) },
                            modifier = Modifier.weight(1f)
                        )
                        OddsButton(
                            label = "${away.shortName} (+1.5)",
                            odds = odds.handicapAwayPlus1,
                            isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "AWAY_PLUS_1" },
                            onClick = { onToggleBet(itemH2) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                MarketCategory.EXACT_SCORE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val popularScores = listOf("1-0", "2-0", "2-1", "1-1", "0-1", "1-2")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            popularScores.take(3).forEach { score ->
                                val scoreOdds = odds.exactScores[score] ?: 6.50
                                val itemScore = BetItemEntity("", "", fixture.id, home.name, away.name, "EXACT_SCORE", "Score Exact", score, score, scoreOdds)
                                OddsButton(
                                    label = score,
                                    odds = scoreOdds,
                                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == score },
                                    onClick = { onToggleBet(itemScore) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            popularScores.drop(3).forEach { score ->
                                val scoreOdds = odds.exactScores[score] ?: 7.00
                                val itemScore = BetItemEntity("", "", fixture.id, home.name, away.name, "EXACT_SCORE", "Score Exact", score, score, scoreOdds)
                                OddsButton(
                                    label = score,
                                    odds = scoreOdds,
                                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == score },
                                    onClick = { onToggleBet(itemScore) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
