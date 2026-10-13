package com.example.kgcfootball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.*

@Composable
fun SimulationStyleDialog(
    currentStyle: String,
    onSelectStyle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PitchDark,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = StadiumGreen,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "STYLE & VITESSE DE SIMULATION",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Choisissez comment les matchs et vos paris progressent dans l'application :",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Option 1 : Virtuel Express
                SimulationStyleCard(
                    styleKey = "VIRTUEL_EXPRESS",
                    title = "⚡ Virtuel Express (Matchs Rapides)",
                    durationBadge = "~60 sec",
                    description = "Les matchs s'enchaînent rapidement (~1 minute par match). Compte à rebours express pour parier immédiatement sans attente !",
                    isSelected = currentStyle == "VIRTUEL_EXPRESS",
                    onSelect = { onSelectStyle("VIRTUEL_EXPRESS") }
                )

                // Option 2 : Mode Moyen 10 Min
                SimulationStyleCard(
                    styleKey = "VIRTUEL_MOYEN",
                    title = "⏱️ Mode Moyen (10 Minutes Réelles)",
                    durationBadge = "10 min",
                    description = "Chaque match dure 10 vraies minutes. Vous avez 90 secondes avant chaque match pour analyser les statistiques et peaufiner vos combinés !",
                    isSelected = currentStyle == "VIRTUEL_MOYEN",
                    onSelect = { onSelectStyle("VIRTUEL_MOYEN") }
                )

                // Option 3 : Temps Réel Pro (90 min calendrier)
                SimulationStyleCard(
                    styleKey = "TEMPS_REEL",
                    title = "🌍 Temps Réel Pro (90 Vraies Minutes)",
                    durationBadge = "90 min",
                    description = "Vrais jours et vraies minutes calendaires. Fonctionne même si l'application est fermée ! Les paris et les scores continuent d'avancer selon l'horloge réelle.",
                    isSelected = currentStyle == "TEMPS_REEL",
                    onSelect = { onSelectStyle("TEMPS_REEL") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_sim_style_dialog")
            ) {
                Text("FERMER", fontWeight = FontWeight.Bold, color = StadiumGreen)
            }
        }
    )
}

@Composable
fun SimulationStyleCard(
    styleKey: String,
    title: String,
    durationBadge: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        color = if (isSelected) StadiumGreenDark.copy(alpha = 0.35f) else PitchSurface,
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(if (isSelected) StadiumGreen else PitchBorder)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("sim_style_card_$styleKey")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (isSelected) OddsGoldLight else TextPrimary
                )
                Surface(
                    color = if (isSelected) StadiumGreen else PitchSurfaceElevated,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = durationBadge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.Black else TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp
            )
        }
    }
}
