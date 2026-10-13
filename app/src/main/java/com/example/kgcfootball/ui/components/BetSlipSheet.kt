package com.example.kgcfootball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
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
import com.example.kgcfootball.engine.OddsEngine
import com.example.ui.theme.*

@Composable
fun BetSlipSheet(
    isOpen: Boolean,
    onClose: () -> Unit,
    selections: List<BetItemEntity>,
    onRemoveSelection: (BetItemEntity) -> Unit,
    onClearAll: () -> Unit,
    stake: Double,
    onStakeChange: (Double) -> Unit,
    walletBalance: Double,
    onPlaceBet: () -> Unit
) {
    val boostPercent = OddsEngine.getComboBoostPercent(selections.size)
    val boostMultiplier = OddsEngine.getComboBoostMultiplier(selections.size)

    if (!isOpen && selections.isNotEmpty()) {
        // Barre flottante rétractable indiquant le coupon actif
        Surface(
            color = StadiumGreenDark,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClose() }
                .testTag("floating_coupon_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Badge(containerColor = OddsGold, contentColor = Color.Black) {
                        Text("${selections.size}", fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(
                            text = "Coupon de Pari • Cote : ${calculateTotalOdds(selections)}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        if (boostPercent > 0) {
                            Text(
                                text = "🔥 Super Booster Combiné : +${boostPercent}%",
                                fontWeight = FontWeight.Black,
                                color = OddsGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
                Text(
                    text = "OUVRIR ▲",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = OddsGoldLight
                )
            }
        }
        return
    }

    if (!isOpen) return

    val totalOdds = calculateTotalOdds(selections)
    val rawPotentialWin = stake * totalOdds
    val boostedPotentialWin = rawPotentialWin * boostMultiplier
    val bonusKgc = (boostedPotentialWin - rawPotentialWin).toInt()
    val canPlace = selections.isNotEmpty() && stake > 0 && stake <= walletBalance

    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        tonalElevation = 16.dp,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bet_slip_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Entête du coupon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Coupon de Pari",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextPrimary
                    )
                    Surface(
                        color = if (selections.size > 1) StadiumGreenDark else PitchSurfaceElevated,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (selections.size > 1) "COMBINÉ (${selections.size})" else "SIMPLE",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (selections.isNotEmpty()) {
                        IconButton(onClick = onClearAll) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Vider le coupon",
                                tint = CardRed
                            )
                        }
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Réduire",
                            tint = TextSecondary
                        )
                    }
                }
            }

            Divider(color = PitchBorder, modifier = Modifier.padding(vertical = 8.dp))

            // Liste des sélections
            if (selections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Votre coupon est vide.\nSélectionnez une cote pour commencer.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 220.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selections) { item ->
                        Surface(
                            color = PitchSurfaceElevated,
                            shape = RoundedCornerShape(12.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.homeTeamName} vs ${item.awayTeamName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = item.selectionLabel,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = StadiumGreen
                                        )
                                        Text(
                                            text = "(${item.marketNameFr})",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = OddsGoldDark,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${item.odds}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = Color.Black,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onRemoveSelection(item) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Supprimer",
                                            tint = TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sélecteur de mise
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mise virtuelle :",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${stake.toInt()} KGC",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = OddsGoldLight
                    )
                }

                // Boutons d'ajustement rapide de mise
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val quickStakes = listOf(500.0, 1000.0, 5000.0, 10000.0)
                    quickStakes.forEach { qStake ->
                        Surface(
                            color = if (stake == qStake) StadiumGreenDark else PitchSurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onStakeChange(qStake) }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = "${qStake.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stake == qStake) Color.White else TextSecondary
                                )
                            }
                        }
                    }
                    // Bouton MAX
                    Surface(
                        color = if (stake == walletBalance) OddsGoldDark else PitchSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStakeChange(walletBalance.coerceAtLeast(100.0)) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "MAX",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = if (stake == walletBalance) Color.Black else OddsGoldLight
                            )
                        }
                    }
                }

                // Bannière Super Booster Combiné
                if (boostPercent > 0) {
                    Surface(
                        color = StadiumGreenDark,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("🔥", fontSize = 14.sp)
                                Text(
                                    text = "BOOST COMBINÉ +$boostPercent%",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                text = "+$bonusKgc KGC OFFERTS",
                                fontWeight = FontWeight.Black,
                                color = OddsGoldLight,
                                fontSize = 11.sp
                            )
                        }
                    }
                } else if (selections.size == 1) {
                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "💡 Ajoutez 1 match de plus pour débloquer le Booster Combiné (+5%) !",
                            fontSize = 11.sp,
                            color = OddsGoldLight,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Synthèse cote et gain potentiel
                Surface(
                    color = PitchDark,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cote totale :", color = TextSecondary, fontSize = 12.sp)
                            Text("$totalOdds", fontWeight = FontWeight.Bold, color = OddsGold, fontSize = 13.sp)
                        }
                        if (boostPercent > 0) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cote boostée (+${boostPercent}%) :", color = StadiumGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    "${Math.round(totalOdds * boostMultiplier * 100) / 100.0}",
                                    fontWeight = FontWeight.Black,
                                    color = StadiumGreen,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Gain potentiel total :", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${boostedPotentialWin.toInt()} KGC",
                                fontWeight = FontWeight.Black,
                                color = StadiumGreen,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Bouton de validation
                Button(
                    onClick = onPlaceBet,
                    enabled = canPlace,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StadiumGreen,
                        contentColor = Color.Black,
                        disabledContainerColor = PitchSurfaceElevated,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_bet_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(20.dp))
                        Text(
                            text = if (stake > walletBalance) "SOLDE INSUFFISANT" else "VALIDER LE PARI (${stake.toInt()} KGC)",
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun calculateTotalOdds(selections: List<BetItemEntity>): Double {
    if (selections.isEmpty()) return 1.00
    var total = 1.0
    selections.forEach { total *= it.odds }
    return Math.round(total * 100) / 100.0
}
