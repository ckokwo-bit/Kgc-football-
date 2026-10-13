package com.example.kgcfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
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
import com.example.kgcfootball.data.model.PlayerEntity
import com.example.kgcfootball.data.model.SeasonAwardEntity
import com.example.kgcfootball.data.model.TeamEntity
import com.example.ui.theme.*

@Composable
fun StandingsScreen(
    standings: List<TeamEntity>,
    topScorers: List<PlayerEntity>,
    awards: List<SeasonAwardEntity>,
    selectedTeamDetails: TeamEntity?,
    squadPlayers: List<PlayerEntity>,
    crownedAward: SeasonAwardEntity?,
    onDismissCrownedAward: () -> Unit,
    onSelectTeam: (TeamEntity) -> Unit,
    onCloseTeamDetails: () -> Unit
) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Classement, 1 = Trophées & Palmarès

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sélecteur d'onglets (Classement vs Trophées)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSubTab == 0,
                        onClick = { selectedSubTab = 0 },
                        label = { Text("Tableau & Buteurs", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StadiumGreenDark,
                            selectedLabelColor = Color.White,
                            containerColor = PitchSurface,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = selectedSubTab == 1,
                        onClick = { selectedSubTab = 1 },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = OddsGold, modifier = Modifier.size(16.dp))
                                Text("Trophées & Palmarès", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OddsGoldDark,
                            selectedLabelColor = Color.Black,
                            containerColor = PitchSurface,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (selectedSubTab == 0) {
                // En-tête Classement
                item {
                    Surface(
                        color = PitchSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tableau du Championnat",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Touchez un club pour voir sa tactique",
                                    fontSize = 11.sp,
                                    color = StadiumGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Ligne d'en-tête du tableau
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("#", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(22.dp))
                                Text("Club", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.weight(1f))
                                Text("MJ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(26.dp))
                                Text("G", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(22.dp))
                                Text("N", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(22.dp))
                                Text("P", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(22.dp))
                                Text("Diff", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextMuted, modifier = Modifier.width(32.dp))
                                Text("Pts", fontWeight = FontWeight.Black, fontSize = 12.sp, color = OddsGold, modifier = Modifier.width(32.dp))
                            }
                        }
                    }
                }

                // Lignes des équipes du classement
                itemsIndexed(standings) { index, team ->
                    val rank = index + 1
                    val isTop = rank <= 2
                    Surface(
                        color = PitchSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(if (isTop) OddsGoldDark else PitchBorder)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectTeam(team) }
                            .testTag("standing_row_${team.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$rank",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp,
                                color = if (isTop) OddsGold else TextSecondary,
                                modifier = Modifier.width(22.dp)
                            )

                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(team.primaryColorHex))
                                        .border(1.dp, Color(team.secondaryColorHex), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = team.shortName.take(2),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (team.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White
                                    )
                                }
                                Column {
                                    Text(
                                        text = team.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    FormBadgeRow(formString = team.formString)
                                }
                            }

                            Text("${team.played}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(26.dp))
                            Text("${team.won}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp))
                            Text("${team.drawn}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp))
                            Text("${team.lost}", fontSize = 11.sp, color = TextSecondary, modifier = Modifier.width(22.dp))
                            Text(
                                text = "${if (team.goalDifference > 0) "+${team.goalDifference}" else team.goalDifference}",
                                fontSize = 11.sp,
                                color = if (team.goalDifference > 0) StadiumGreen else if (team.goalDifference < 0) CardRed else TextSecondary,
                                modifier = Modifier.width(32.dp)
                            )
                            Text(
                                text = "${team.points}",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = if (isTop) OddsGold else TextPrimary,
                                modifier = Modifier.width(32.dp)
                            )
                        }
                    }
                }

                // Section Meilleurs Buteurs
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🏆 Meilleurs Buteurs",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                }

                itemsIndexed(topScorers.take(5)) { index, scorer ->
                    Surface(
                        color = PitchSurface,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = OddsGold
                                )
                                Icon(
                                    imageVector = Icons.Default.SportsSoccer,
                                    contentDescription = null,
                                    tint = StadiumGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = scorer.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = PitchSurfaceElevated,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = scorer.position,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${scorer.goals} buts",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = StadiumGreen
                            )
                        }
                    }
                }
            } else {
                // Section Trophées & Palmarès
                if (awards.isEmpty()) {
                    item {
                        Surface(
                            color = PitchSurface,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(28.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = OddsGold, modifier = Modifier.size(44.dp))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Le championnat est en cours !", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    Text("Les trophées (Champion, Ballon d'Or, Soulier d'Or) seront décernés à la fin de la 7ème journée.", fontSize = 12.sp, color = TextMuted, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                                }
                            }
                        }
                    }
                } else {
                    items(awards) { award ->
                        AwardCard(award = award)
                    }
                }
            }
        }

        // Popup Détail de l'équipe (Tactique, Formation & Joueurs)
        if (selectedTeamDetails != null) {
            TeamDetailsDialog(
                team = selectedTeamDetails,
                players = squadPlayers,
                onDismiss = onCloseTeamDetails
            )
        }

        // Dialogue Célébration Trophée de Fin de Saison
        if (crownedAward != null) {
            CrownedAwardDialog(
                award = crownedAward,
                onDismiss = onDismissCrownedAward
            )
        }
    }
}

@Composable
fun AwardCard(award: SeasonAwardEntity) {
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
                Text(
                    text = "${award.seasonLabel} • ${award.leagueName}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = OddsGold
                )
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = OddsGold, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Champion
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🏆 Champion :", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                Text(award.championTeamName, fontWeight = FontWeight.Black, fontSize = 14.sp, color = StadiumGreen)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ballon d'Or KGC
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🥇 Ballon d'Or KGC :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                Text(award.ballonDorPlayerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OddsGoldLight)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Soulier d'Or
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("👟 Soulier d'Or :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                Text("${award.soulierDorPlayerName} (${award.soulierDorGoals} buts)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Gant d'Or
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🧤 Gant d'Or :", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                Text(award.gantDorPlayerName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
fun CrownedAwardDialog(
    award: SeasonAwardEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = OddsGold, contentColor = Color.Black)
            ) {
                Text("Continuer vers la Saison Suivante", fontWeight = FontWeight.Black)
            }
        },
        containerColor = PitchSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = OddsGold, modifier = Modifier.size(28.dp))
                Text("SAISON TERMINÉE !", fontWeight = FontWeight.Black, fontSize = 18.sp, color = OddsGold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Félicitations au nouveau champion couronné :", fontSize = 13.sp, color = TextSecondary)
                Text(award.championTeamName, fontWeight = FontWeight.Black, fontSize = 20.sp, color = StadiumGreen)

                Divider(color = PitchBorder)

                Text("DISTINCTIONS INDIVIDUELLES :", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OddsGold)
                Text("🥇 Ballon d'Or KGC : ${award.ballonDorPlayerName}", fontSize = 13.sp, color = TextPrimary)
                Text("👟 Soulier d'Or : ${award.soulierDorPlayerName} (${award.soulierDorGoals} buts)", fontSize = 13.sp, color = TextPrimary)
                Text("🧤 Gant d'Or : ${award.gantDorPlayerName}", fontSize = 13.sp, color = TextPrimary)
            }
        }
    )
}

@Composable
fun FormBadgeRow(formString: String) {
    val items = formString.split(",").filter { it.isNotBlank() }.take(5)
    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        items.forEach { char ->
            val color = when (char.trim().uppercase()) {
                "V", "W" -> StadiumGreen
                "N", "D" -> OddsGold
                else -> CardRed
            }
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}

@Composable
fun TeamDetailsDialog(
    team: TeamEntity,
    players: List<PlayerEntity>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = StadiumGreen, contentColor = Color.Black)
            ) {
                Text("Fermer", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = PitchSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(team.primaryColorHex))
                            .border(1.dp, Color(team.secondaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(team.shortName, fontSize = 9.sp, fontWeight = FontWeight.Black, color = if (team.primaryColorHex == 0xFFFFFFFF) Color.Black else Color.White)
                    }
                    Text(team.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text("Stade : ${team.stadiumName}", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = PitchSurfaceElevated,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("DISPOSITION TACTIQUE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = StadiumGreen)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Formation : ${team.formation}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                            Text("Style : ${team.tacticStyle}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OddsGold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Attaque: ${team.attackRating}", fontSize = 11.sp, color = TextSecondary)
                            Text("Milieu: ${team.midfieldRating}", fontSize = 11.sp, color = TextSecondary)
                            Text("Défense: ${team.defenseRating}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("EFFECTIF DES JOUEURS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(players) { player ->
                        Surface(
                            color = PitchDark,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("#${player.number}", fontSize = 11.sp, color = TextMuted)
                                    Text(player.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Surface(
                                        color = PitchSurfaceElevated,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            player.position,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = StadiumGreen,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text("${player.rating} GEN", fontWeight = FontWeight.Black, fontSize = 11.sp, color = OddsGold)
                            }
                        }
                    }
                }
            }
        }
    )
}
