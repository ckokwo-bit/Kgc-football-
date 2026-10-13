package com.example.kgcfootball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.kgcfootball.data.model.PlayerEntity
import com.example.kgcfootball.data.model.TeamEntity
import com.example.kgcfootball.engine.OddsEngine
import com.example.kgcfootball.ui.screens.OddsButton
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreMatchAnalysisSheet(
    fixture: MatchFixtureEntity?,
    homeTeam: TeamEntity?,
    awayTeam: TeamEntity?,
    homePlayers: List<PlayerEntity> = emptyList(),
    awayPlayers: List<PlayerEntity> = emptyList(),
    couponSelections: List<BetItemEntity>,
    onToggleBet: (BetItemEntity) -> Unit,
    onLaunchImmediately: (String) -> Unit = {},
    onDismiss: () -> Unit
) {
    if (fixture == null || homeTeam == null || awayTeam == null) return

    val odds = OddsEngine.calculatePreMatchOdds(homeTeam, awayTeam)
    val scrollState = rememberScrollState()

    // Calcul des probabilités de l'algorithme bookmaker
    val totalPower = homeTeam.overallRating + awayTeam.overallRating + 10.0
    val homeProb = ((homeTeam.overallRating + 6.0) / totalPower * 100).toInt().coerceIn(20, 75)
    val awayProb = ((awayTeam.overallRating.toDouble()) / totalPower * 100).toInt().coerceIn(15, 65)
    val drawProb = (100 - homeProb - awayProb).coerceIn(15, 35)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PitchDark,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("pre_match_analysis_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            // 1. En-tête : Match & Coup d'envoi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = null,
                        tint = StadiumGreen,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = "ANALYSE D'AVANT-MATCH & STATS",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                }

                Surface(
                    color = PitchSurfaceElevated,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "J${fixture.round} • ${fixture.scheduledKickoffTime}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = OddsGoldLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Carte Principale : Équipes en confrontation
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Domicile
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(homeTeam.primaryColorHex))
                                    .border(2.dp, Color(homeTeam.secondaryColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = homeTeam.shortName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (homeTeam.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(homeTeam.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Note GEN : ${homeTeam.overallRating}", fontSize = 11.sp, color = TextSecondary)
                        }

                        // VS Central
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "VS",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = OddsGold
                            )
                            Text(
                                text = homeTeam.stadiumName,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        // Extérieur
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(awayTeam.primaryColorHex))
                                    .border(2.dp, Color(awayTeam.secondaryColorHex), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = awayTeam.shortName,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (awayTeam.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(awayTeam.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Note GEN : ${awayTeam.overallRating}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Conseil & Probabilités de l'algorithme
                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("🤖 Modèle Prédictif KGC :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OddsGold)
                                Text("Confiance : ⭐⭐⭐⭐☆ (84%)", fontSize = 11.sp, color = StadiumGreen)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${homeTeam.shortName} : $homeProb%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Nul : $drawProb%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Text("${awayTeam.shortName} : $awayProb%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Face-à-face des Entraîneurs (Personnalité & Tactique)
            Text(
                text = "👔 DUEL DES ENTRAÎNEURS & TACTIQUE",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Coach Domicile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = StadiumGreenDark,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👔", fontSize = 18.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(homeTeam.coachName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text(homeTeam.formation, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StadiumGreen)
                            }
                            Text("Profil : ${homeTeam.coachPersonality}", fontSize = 11.sp, color = OddsGoldLight)
                            Text("« ${homeTeam.coachTacticalQuote} »", fontSize = 11.sp, color = TextSecondary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }

                    HorizontalDivider(color = PitchBorder, thickness = 0.5.dp)

                    // Coach Extérieur
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = PitchSurfaceElevated,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👔", fontSize = 18.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(awayTeam.coachName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text(awayTeam.formation, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OddsGold)
                            }
                            Text("Profil : ${awayTeam.coachPersonality}", fontSize = 11.sp, color = OddsGoldLight)
                            Text("« ${awayTeam.coachTacticalQuote} »", fontSize = 11.sp, color = TextSecondary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Forme Récente & Confrontations Directes (H2H)
            Text(
                text = "📈 FORME RÉCENTE & HISTORIQUE DIRECT (H2H)",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Forme Domicile
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${homeTeam.shortName} (5 derniers) :", fontSize = 12.sp, color = TextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            homeTeam.formString.split(",").forEach { letter ->
                                FormBadge(letter)
                            }
                        }
                    }

                    // Forme Extérieur
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${awayTeam.shortName} (5 derniers) :", fontSize = 12.sp, color = TextPrimary)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            awayTeam.formString.split(",").forEach { letter ->
                                FormBadge(letter)
                            }
                        }
                    }

                    HorizontalDivider(color = PitchBorder, thickness = 0.5.dp)

                    // Bilan H2H
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Derniers duels directs :", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            "${homeTeam.recentH2hWins} Victoires ${homeTeam.shortName} • ${homeTeam.recentH2hDraws} Nuls • ${homeTeam.recentH2hLosses} Victoires ${awayTeam.shortName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OddsGoldLight
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Moyenne buts / match :", fontSize = 11.sp, color = TextSecondary)
                        Text(
                            "${homeTeam.avgGoalsScored} buts marqués vs ${awayTeam.avgGoalsScored} buts",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 5. Comparaison des Lignes (Attaque, Milieu, Défense)
            Text(
                text = "⚔️ COMPARAISON DES FORCES EN PRÉSENCE",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    RatingComparisonRow(label = "Attaque", val1 = homeTeam.attackRating, val2 = awayTeam.attackRating, name1 = homeTeam.shortName, name2 = awayTeam.shortName)
                    RatingComparisonRow(label = "Milieu de terrain", val1 = homeTeam.midfieldRating, val2 = awayTeam.midfieldRating, name1 = homeTeam.shortName, name2 = awayTeam.shortName)
                    RatingComparisonRow(label = "Défense", val1 = homeTeam.defenseRating, val2 = awayTeam.defenseRating, name1 = homeTeam.shortName, name2 = awayTeam.shortName)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 6. Duel des Joueurs Clés & Infirmerie
            Text(
                text = "⭐ JOUEURS À SUIVRE & INFIRMERIE",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Star ${homeTeam.shortName} :", fontSize = 11.sp, color = TextSecondary)
                            Text(homeTeam.starPlayerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OddsGoldLight)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Star ${awayTeam.shortName} :", fontSize = 11.sp, color = TextSecondary)
                            Text(awayTeam.starPlayerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OddsGoldLight)
                        }
                    }

                    HorizontalDivider(color = PitchBorder, thickness = 0.5.dp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🏥", fontSize = 14.sp)
                        Text(
                            text = "Infirmerie : Effectifs au complet. Tous les cadres sont aptes à débuter la rencontre.",
                            fontSize = 11.sp,
                            color = StadiumGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Cotes Rapides d'Avant-Match (Parier immédiatement depuis l'analyse)
            Text(
                text = "⚡ PLACER UN PARI SUR CETTE ANALYSE",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                color = OddsGold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val item1 = BetItemEntity("", "", fixture.id, homeTeam.name, awayTeam.name, "1X2", "1X2", "1", "${homeTeam.shortName} (1)", odds.homeWin)
                val itemX = BetItemEntity("", "", fixture.id, homeTeam.name, awayTeam.name, "1X2", "1X2", "X", "Nul (X)", odds.draw)
                val item2 = BetItemEntity("", "", fixture.id, homeTeam.name, awayTeam.name, "1X2", "1X2", "2", "${awayTeam.shortName} (2)", odds.awayWin)

                OddsButton(
                    label = "1 (${homeTeam.shortName})",
                    odds = odds.homeWin,
                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "1" },
                    onClick = { onToggleBet(item1) },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    label = "Nul (X)",
                    odds = odds.draw,
                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "X" },
                    onClick = { onToggleBet(itemX) },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    label = "2 (${awayTeam.shortName})",
                    odds = odds.awayWin,
                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "2" },
                    onClick = { onToggleBet(item2) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val itemOver = BetItemEntity("", "", fixture.id, homeTeam.name, awayTeam.name, "OVER_UNDER_2_5", "Buts +/- 2.5", "OVER", "+ de 2.5 Buts", odds.over25)
                val itemBtts = BetItemEntity("", "", fixture.id, homeTeam.name, awayTeam.name, "BTTS", "Les 2 Marquent", "YES", "Oui (BTTS)", odds.bttsYes)

                OddsButton(
                    label = "+ 2.5 Buts",
                    odds = odds.over25,
                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "OVER" },
                    onClick = { onToggleBet(itemOver) },
                    modifier = Modifier.weight(1f)
                )
                OddsButton(
                    label = "Les 2 Marquent",
                    odds = odds.bttsYes,
                    isSelected = couponSelections.any { it.matchId == fixture.id && it.selectionKey == "YES" },
                    onClick = { onToggleBet(itemBtts) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bouton de lancement immédiat si match programmé
            if (fixture.status == "SCHEDULED") {
                Button(
                    onClick = {
                        onLaunchImmediately(fixture.id)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StadiumGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LANCER LE MATCH IMMÉDIATEMENT", fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun FormBadge(letter: String) {
    val (color, text) = when (letter.trim().uppercase()) {
        "V", "W" -> Pair(StadiumGreenDark, "V")
        "D", "L" -> Pair(CardRed, "D")
        else -> Pair(PitchSurfaceElevated, "N")
    }

    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}

@Composable
fun RatingComparisonRow(
    label: String,
    val1: Int,
    val2: Int,
    name1: String,
    name2: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$name1 ($val1)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (val1 >= val2) StadiumGreen else TextSecondary)
            Text(label, fontSize = 11.sp, color = TextMuted)
            Text("$name2 ($val2)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (val2 >= val1) StadiumGreen else TextSecondary)
        }
        Spacer(modifier = Modifier.height(3.dp))
        val total = (val1 + val2).toFloat()
        val ratio1 = val1 / total
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(PitchBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(ratio1)
                    .background(StadiumGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f - ratio1)
                    .background(OddsGold)
            )
        }
    }
}
