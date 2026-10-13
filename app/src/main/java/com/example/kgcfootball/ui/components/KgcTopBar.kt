package com.example.kgcfootball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.kgcfootball.data.model.LeagueEntity
import com.example.kgcfootball.data.model.UserWalletEntity
import com.example.kgcfootball.ui.viewmodel.CyclePhase
import com.example.ui.theme.*

@Composable
fun KgcTopBar(
    leagues: List<LeagueEntity>,
    selectedLeagueId: String,
    onSelectLeague: (String) -> Unit,
    wallet: UserWalletEntity?,
    onWalletClick: () -> Unit,
    cyclePhase: CyclePhase,
    countdownSeconds: Int,
    isAutoCycleActive: Boolean,
    onToggleAutoCycle: () -> Unit,
    onSkipToKickoff: () -> Unit,
    onLockApp: () -> Unit,
    simulationStyle: String = "VIRTUEL_EXPRESS",
    onOpenSimulationStyle: () -> Unit = {}
) {
    Surface(
        color = PitchDark,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 6.dp)
        ) {
            // Ligne supérieure : Titre et Solde du joueur
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo & Titre
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(PitchSurfaceElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Logo KGC Football",
                            tint = StadiumGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "KGC FOOTBALL",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Virtual Sports • Offline Pro",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = StadiumGreen
                        )
                    }
                }

                // Actions à droite : Verrouillage PIN (si configuré) + Solde virtuel
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (wallet?.isPinProtectionActive == true) {
                        IconButton(
                            onClick = onLockApp,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Verrouiller",
                                tint = OddsGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Bouton Sélecteur de Style de Simulation
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = PitchSurfaceElevated,
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(StadiumGreen)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onOpenSimulationStyle() }
                            .testTag("topbar_sim_style_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = when (simulationStyle) {
                                    "VIRTUEL_MOYEN" -> "⏱️ 10 min"
                                    "TEMPS_REEL" -> "🌍 Réel 90'"
                                    else -> "⚡ Express"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = OddsGoldLight
                            )
                        }
                    }

                    // Bouton Solde virtuel
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PitchSurfaceElevated,
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onWalletClick() }
                            .testTag("topbar_wallet_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = "Portefeuille",
                                tint = OddsGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "${(wallet?.balance ?: 25000.0).toInt()} KGC",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = OddsGoldLight
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bandeau du Cycle Virtuel Automatique (Style Bookmaker 1xBet / Betclic)
            Surface(
                color = PitchSurface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when (cyclePhase) {
                                        CyclePhase.PRE_MATCH -> OddsGold
                                        CyclePhase.LIVE_IN_PLAY -> CardRed
                                        CyclePhase.POST_MATCH_REVIEW -> StadiumGreen
                                    }
                                )
                        )
                        val min = countdownSeconds / 60
                        val sec = countdownSeconds % 60
                        val timeStr = String.format("%02d:%02d", min, sec)

                        Text(
                            text = when (cyclePhase) {
                                CyclePhase.PRE_MATCH -> "Prochain coup d'envoi dans $timeStr"
                                CyclePhase.LIVE_IN_PLAY -> "⚽ Matchs en direct • Cotes Live actives"
                                CyclePhase.POST_MATCH_REVIEW -> "Règlement des paris • Prochaine journée"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = when (cyclePhase) {
                                CyclePhase.PRE_MATCH -> OddsGoldLight
                                CyclePhase.LIVE_IN_PLAY -> CardRed
                                CyclePhase.POST_MATCH_REVIEW -> StadiumGreen
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (cyclePhase == CyclePhase.PRE_MATCH) {
                            Text(
                                text = "LANCER DIRECT ▶",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = StadiumGreen,
                                modifier = Modifier
                                    .clickable { onSkipToKickoff() }
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleAutoCycle,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isAutoCycleActive) Icons.Default.PauseCircle else Icons.Default.PlayCircle,
                                contentDescription = "Auto cycle",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Carousel des Ligues
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(leagues) { league ->
                    val isSelected = league.id == selectedLeagueId
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectLeague(league.id) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(league.flagEmoji)
                                Text(
                                    text = league.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StadiumGreenDark,
                            selectedLabelColor = Color.White,
                            containerColor = PitchSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) StadiumGreen else PitchBorder
                        ),
                        modifier = Modifier.testTag("league_chip_${league.id}")
                    )
                }
            }
        }
    }
}
