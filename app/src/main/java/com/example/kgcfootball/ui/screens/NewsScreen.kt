package com.example.kgcfootball.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.kgcfootball.data.model.NewsArticle
import com.example.ui.theme.*

@Composable
fun NewsScreen(
    newsList: List<NewsArticle>,
    onSelectLeagueNews: ((String) -> Unit)? = null
) {
    var selectedCategory by remember { mutableStateOf("TOUS") }
    var readingArticle by remember { mutableStateOf<NewsArticle?>(null) }

    val categories = listOf("TOUS", "MERCATO", "RÉACTION", "VAR", "TACTIQUE", "INFIRMERIE", "EXCLUSIF", "CLASSEMENT")

    val filteredNews = remember(selectedCategory, newsList) {
        if (selectedCategory == "TOUS") newsList
        else newsList.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // En-tête Médias KGC
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(CardRed)
                            )
                            Text(
                                text = "KGC Foot Média • En Direct",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                        }
                        Surface(
                            color = PitchSurfaceElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "24h/24 Hors-Ligne",
                                fontSize = 10.sp,
                                color = StadiumGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mercato brûlant, coulisses des vestiaires, analyses tactiques poussées et réactions d'après-match.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Bandeau Flash Info en continu
        item {
            Surface(
                color = PitchSurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = CardRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "FLASH",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = remember(newsList) {
                            newsList.take(3).joinToString("  •  ") { it.title.replace(Regex("^[^\u0020]* "), "") }
                        },
                        fontSize = 11.sp,
                        color = OddsGoldLight,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Filtres par catégories
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
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

        // Article à la Une (Premier élément)
        if (filteredNews.isNotEmpty()) {
            val headline = filteredNews.first()
            item {
                HeadlineArticleCard(
                    article = headline,
                    onClick = { readingArticle = headline }
                )
            }
        }

        // Autres articles du fil d'actualités
        val remainingNews = if (filteredNews.isNotEmpty()) filteredNews.drop(1) else emptyList()
        items(remainingNews) { article ->
            NewsArticleItemCard(
                article = article,
                onClick = { readingArticle = article }
            )
        }
    }

    // Modal de lecture complète de l'article
    readingArticle?.let { article ->
        ArticleReadingDialog(
            article = article,
            onDismiss = { readingArticle = null }
        )
    }
}

@Composable
fun HeadlineArticleCard(
    article: NewsArticle,
    onClick: () -> Unit
) {
    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StadiumGreen)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("headline_article_${article.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = CardRed,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "À LA UNE • ${article.category}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Text(
                    text = "${article.source} • ${article.timeAgo}",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.title,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = TextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = article.subtitle,
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(
                        text = "${article.readTimeMinutes} min de lecture",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                FilledTonalButton(
                    onClick = onClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PitchSurfaceElevated,
                        contentColor = StadiumGreen
                    )
                ) {
                    Text("Lire l'article complet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NewsArticleItemCard(
    article: NewsArticle,
    onClick: () -> Unit
) {
    Surface(
        color = PitchSurface,
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("news_item_${article.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = when (article.category) {
                        "MERCATO" -> OddsGoldDark
                        "VAR" -> Color(0xFF8B1A1A)
                        "TACTIQUE" -> StadiumGreenDark
                        else -> PitchSurfaceElevated
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = article.category,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${article.source} • ${article.timeAgo}",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = article.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = article.subtitle,
                fontSize = 12.sp,
                color = TextSecondary,
                maxLines = 2
            )
        }
    }
}

@Composable
fun ArticleReadingDialog(
    article: NewsArticle,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = PitchDark,
            shape = RoundedCornerShape(20.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(PitchBorder)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = StadiumGreenDark,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = article.category,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = article.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimary,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${article.source} • Publié ${article.timeAgo} • ${article.readTimeMinutes} min de lecture",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = PitchBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = PitchSurface,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "« ${article.subtitle} »",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = StadiumGreen,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = article.fullContent,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StadiumGreen,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Fermer la lecture", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
