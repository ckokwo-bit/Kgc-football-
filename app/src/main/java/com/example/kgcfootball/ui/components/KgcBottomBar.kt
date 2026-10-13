package com.example.kgcfootball.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.example.kgcfootball.ui.viewmodel.AppNavTab
import com.example.ui.theme.*

@Composable
fun KgcBottomBar(
    selectedTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit,
    couponCount: Int,
    isLiveRunning: Boolean
) {
    NavigationBar(
        containerColor = PitchDark,
        tonalElevation = 8.dp
    ) {
        // 1. MATCHS
        NavigationBarItem(
            selected = selectedTab == AppNavTab.MATCHES,
            onClick = { onTabSelected(AppNavTab.MATCHES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Matchs"
                )
            },
            label = { Text("Matchs", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_matches")
        )

        // 2. DIRECT (LIVE)
        NavigationBarItem(
            selected = selectedTab == AppNavTab.LIVE,
            onClick = { onTabSelected(AppNavTab.LIVE) },
            icon = {
                BadgedBox(
                    badge = {
                        if (isLiveRunning) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(CardRed)
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Direct"
                    )
                }
            },
            label = { Text("Direct", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_live")
        )

        // 3. PARIS (SPORTSBOOK)
        NavigationBarItem(
            selected = selectedTab == AppNavTab.SPORTSBOOK,
            onClick = { onTabSelected(AppNavTab.SPORTSBOOK) },
            icon = {
                BadgedBox(
                    badge = {
                        if (couponCount > 0) {
                            Badge(
                                containerColor = OddsGold,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = couponCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ConfirmationNumber,
                        contentDescription = "Paris Sportifs"
                    )
                }
            },
            label = { Text("Paris", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_sportsbook")
        )

        // 4. ACTUALITÉS (NEWS)
        NavigationBarItem(
            selected = selectedTab == AppNavTab.NEWS,
            onClick = { onTabSelected(AppNavTab.NEWS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Newspaper,
                    contentDescription = "Actualités"
                )
            },
            label = { Text("Actus", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_news")
        )

        // 5. CLASSEMENT
        NavigationBarItem(
            selected = selectedTab == AppNavTab.STANDINGS,
            onClick = { onTabSelected(AppNavTab.STANDINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Classement"
                )
            },
            label = { Text("Tableau", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_standings")
        )

        // 6. WALLET
        NavigationBarItem(
            selected = selectedTab == AppNavTab.WALLET,
            onClick = { onTabSelected(AppNavTab.WALLET) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = "Bankroll"
                )
            },
            label = { Text("Bankroll", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = StadiumGreen,
                selectedTextColor = StadiumGreen,
                indicatorColor = PitchSurfaceElevated,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_tab_wallet")
        )
    }
}
