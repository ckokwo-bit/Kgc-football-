package com.example.kgcfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kgcfootball.data.model.BetTicketEntity
import com.example.kgcfootball.data.model.UserWalletEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    wallet: UserWalletEntity?,
    tickets: List<BetTicketEntity>,
    onClaimDailyBonus: () -> Unit,
    onResetBalance: () -> Unit,
    onCashout: (String) -> Unit,
    onOpenCustomBalance: () -> Unit,
    isCustomBalanceOpen: Boolean,
    onCloseCustomBalance: () -> Unit,
    onApplyCustomBalance: (Double) -> Unit,
    onOpenPinSetup: () -> Unit,
    isPinSetupOpen: Boolean,
    onClosePinSetup: () -> Unit,
    onSavePin: (String?, Boolean) -> Unit
) {
    var filterStatus by remember { mutableStateOf("ALL") }

    val filteredTickets = remember(tickets, filterStatus) {
        when (filterStatus) {
            "PENDING" -> tickets.filter { it.status == "PENDING" }
            "WON" -> tickets.filter { it.status == "WON" || it.status == "CASHED_OUT" }
            "LOST" -> tickets.filter { it.status == "LOST" }
            else -> tickets
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Carte de Solde Virtuel
            item {
                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OddsGoldDark)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wallet_balance_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PORTFEUILLE VIRTUEL KGC",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = OddsGold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Surface(
                                    color = PitchSurfaceElevated,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "100% FICTIF",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = StadiumGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onOpenPinSetup,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (wallet?.isPinProtectionActive == true) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = "Sécurité PIN",
                                        tint = if (wallet?.isPinProtectionActive == true) StadiumGreen else TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${(wallet?.balance ?: 25000.0).toInt()} KGC",
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            color = TextPrimary
                        )

                        Text(
                            text = "Équivalent : ~${((wallet?.balance ?: 25000.0) * 10).toInt()} FCFA Virtuels",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Boutons d'action (Modifier argent, Bonus & Reset)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenCustomBalance,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = OddsGoldDark,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("edit_balance_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Text("Modifier Solde", fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }

                            val totalBets = wallet?.totalBetsPlaced ?: 0
                            val bonusButtonLabel = when {
                                totalBets >= 50 -> "+50,000 Gratuit"
                                totalBets >= 30 -> "+35,000 Gratuit"
                                totalBets >= 15 -> "+20,000 Gratuit"
                                totalBets >= 5 -> "+10,000 Gratuit"
                                else -> "+5,000 Gratuit"
                            }

                            Button(
                                onClick = onClaimDailyBonus,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StadiumGreen,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("claim_daily_bonus_button")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Text(bonusButtonLabel, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            // 1.5. Programme Fidélité VIP Parieur
            item {
                val totalBets = wallet?.totalBetsPlaced ?: 0
                val (vipTitle, vipBonus, nextGoal) = when {
                    totalBets >= 50 -> Triple("👑 DIAMANT LÉGENDAIRE", "+50,000 KGC/jour", 50)
                    totalBets >= 30 -> Triple("💎 PLATINE PRO", "+35,000 KGC/jour", 50)
                    totalBets >= 15 -> Triple("🥇 OR SUPRÊME", "+20,000 KGC/jour", 30)
                    totalBets >= 5 -> Triple("🥈 ARGENT ÉLITE", "+10,000 KGC/jour", 15)
                    else -> Triple("🥉 BRONZE ROOKIE", "+5,000 KGC/jour", 5)
                }
                val progressFraction = if (totalBets >= 50) 1.0f else (totalBets.toFloat() / nextGoal.toFloat()).coerceIn(0f, 1f)

                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = vipTitle,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = OddsGold
                            )
                            Surface(
                                color = StadiumGreenDark,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = vipBonus,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            color = StadiumGreen,
                            trackColor = PitchSurfaceElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progression VIP : $totalBets / $nextGoal paris placés",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = if (totalBets >= 50) "Rang Maximum !" else "Niveau supérieur à $nextGoal paris",
                                fontSize = 10.sp,
                                color = OddsGoldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Statistiques de paris (Bankroll Metrics)
            item {
                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Statistiques de Jeu & Rentabilité",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricBox(title = "Paris Placés", value = "${wallet?.totalBetsPlaced ?: 0}")
                            MetricBox(title = "Gagnés", value = "${wallet?.totalWonBets ?: 0}", color = StadiumGreen)
                            MetricBox(title = "Perdus", value = "${wallet?.totalLostBets ?: 0}", color = CardRed)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val winRate = if ((wallet?.totalBetsPlaced ?: 0) > 0) {
                                (wallet?.totalWonBets ?: 0) * 100 / (wallet?.totalBetsPlaced ?: 1)
                            } else 0

                            val profit = (wallet?.totalProfit ?: 0.0).toInt()

                            MetricBox(title = "Taux Victoires", value = "$winRate%")
                            MetricBox(
                                title = "Profit Net",
                                value = "${if (profit >= 0) "+$profit" else "$profit"} KGC",
                                color = if (profit >= 0) StadiumGreen else CardRed
                            )
                            MetricBox(
                                title = "Plus Gros Gain",
                                value = "${(wallet?.biggestWin ?: 0.0).toInt()} KGC",
                                color = OddsGold
                            )
                        }
                    }
                }
            }

            // 3. Filtre de l'historique des tickets
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Historique des Paris (${filteredTickets.size})",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("ALL" to "Tous", "PENDING" to "En cours", "WON" to "Gagnés", "LOST" to "Perdus").forEach { (key, label) ->
                            val isSelected = filterStatus == key
                            Surface(
                                color = if (isSelected) StadiumGreenDark else PitchSurfaceElevated,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { filterStatus = key }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 4. Liste des tickets
            if (filteredTickets.isEmpty()) {
                item {
                    Surface(
                        color = PitchSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Aucun pari trouvé dans cette catégorie.",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            } else {
                items(filteredTickets) { ticket ->
                    TicketHistoryCard(
                        ticket = ticket,
                        onCashout = { onCashout(ticket.id) }
                    )
                }
            }
        }

        // Dialogue de modification du solde (Sandbox Bankroll)
        if (isCustomBalanceOpen) {
            CustomBalanceDialog(
                currentBalance = wallet?.balance ?: 25000.0,
                onDismiss = onCloseCustomBalance,
                onApply = onApplyCustomBalance
            )
        }

        // Dialogue de configuration du code PIN
        if (isPinSetupOpen) {
            PinSetupDialog(
                isCurrentlyActive = wallet?.isPinProtectionActive == true,
                onDismiss = onClosePinSetup,
                onSave = onSavePin
            )
        }
    }
}

@Composable
fun MetricBox(title: String, value: String, color: Color = TextPrimary) {
    Column(
        modifier = Modifier.width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = 10.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, fontWeight = FontWeight.Black, fontSize = 13.sp, color = color)
    }
}

@Composable
fun TicketHistoryCard(
    ticket: BetTicketEntity,
    onCashout: () -> Unit
) {
    val dateStr = remember(ticket.placedAt) {
        SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(ticket.placedAt))
    }

    val statusColor = when (ticket.status) {
        "WON" -> StadiumGreen
        "CASHED_OUT" -> OddsGold
        "LOST" -> CardRed
        else -> TextSecondary
    }

    val statusLabel = when (ticket.status) {
        "WON" -> "GAGNÉ (+${ticket.potentialWin.toInt()} KGC)"
        "CASHED_OUT" -> "CASH OUT (+${(ticket.cashoutAmount ?: ticket.stake).toInt()} KGC)"
        "LOST" -> "PERDU"
        else -> "EN COURS"
    }

    // Calcul de la valeur approximative de cashout si en cours
    val estimatedCashout = if (ticket.status == "PENDING") {
        ticket.currentCashoutValue.coerceAtLeast(ticket.stake * 0.85).toInt()
    } else 0

    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Entête du ticket
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = ticket.id,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    Surface(
                        color = PitchSurfaceElevated,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = ticket.type,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

                Surface(
                    color = statusColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Ligne détails financiers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Mise", fontSize = 10.sp, color = TextMuted)
                    Text("${ticket.stake.toInt()} KGC", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }
                Column {
                    Text("Cote totale", fontSize = 10.sp, color = TextMuted)
                    Text("${ticket.totalOdds}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OddsGold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Gain potentiel", fontSize = 10.sp, color = TextMuted)
                    Text("${ticket.potentialWin.toInt()} KGC", fontWeight = FontWeight.Black, fontSize = 13.sp, color = StadiumGreen)
                }
            }

            // Bouton de Cash Out si le ticket est en cours
            if (ticket.status == "PENDING") {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onCashout,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OddsGoldDark,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("cashout_button_${ticket.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(
                            text = "CASH OUT IMMÉDIAT ($estimatedCashout KGC)",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Placé le $dateStr",
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun CustomBalanceDialog(
    currentBalance: Double,
    onDismiss: () -> Unit,
    onApply: (Double) -> Unit
) {
    var textInput by remember { mutableStateOf(currentBalance.toInt().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val amount = textInput.toDoubleOrNull() ?: currentBalance
                    onApply(amount)
                },
                colors = ButtonDefaults.buttonColors(containerColor = StadiumGreen, contentColor = Color.Black)
            ) {
                Text("Valider ce solde", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler", color = TextSecondary)
            }
        },
        containerColor = PitchSurface,
        title = {
            Text("Modifier le Solde Virtuel", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "Définissez le montant virtuel souhaité pour tester vos stratégies de paris :",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it.filter { char -> char.isDigit() } },
                    label = { Text("Montant KGC") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = StadiumGreen,
                        unfocusedBorderColor = PitchBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Paliers rapides
                Text("Paliers rapides :", fontSize = 11.sp, color = TextMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50000.0, 100000.0, 500000.0, 1000000.0).forEach { preset ->
                        Surface(
                            color = PitchSurfaceElevated,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { textInput = preset.toInt().toString() }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    text = if (preset >= 1000000) "1M" else "${(preset / 1000).toInt()}k",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OddsGold
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PinSetupDialog(
    isCurrentlyActive: Boolean,
    onDismiss: () -> Unit,
    onSave: (String?, Boolean) -> Unit
) {
    var pinText by remember { mutableStateOf("") }
    var enableProtection by remember { mutableStateOf(isCurrentlyActive) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (enableProtection) {
                        if (pinText.length == 4) {
                            onSave(pinText, true)
                        }
                    } else {
                        onSave(null, false)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StadiumGreen, contentColor = Color.Black)
            ) {
                Text("Enregistrer", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer", color = TextSecondary)
            }
        },
        containerColor = PitchSurface,
        title = {
            Text("Sécurité de l'application", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activer le verrouillage par Code PIN", fontSize = 13.sp, color = TextPrimary)
                    Switch(
                        checked = enableProtection,
                        onCheckedChange = { enableProtection = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = StadiumGreen)
                    )
                }

                if (enableProtection) {
                    Text(
                        "Choisissez un code secret à 4 chiffres :",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = pinText,
                        onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) pinText = it },
                        label = { Text("Code PIN (4 chiffres)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = StadiumGreen,
                            unfocusedBorderColor = PitchBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}
