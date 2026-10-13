package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bet_tickets")
data class BetTicketEntity(
    @PrimaryKey val id: String,
    val placedAt: Long = System.currentTimeMillis(),
    val stake: Double,
    val totalOdds: Double,
    val potentialWin: Double,
    val status: String = "PENDING", // PENDING, WON, LOST, CASHED_OUT
    val type: String = "SIMPLE",     // SIMPLE, COMBINE
    val settledAt: Long? = null,
    val cashoutAmount: Double? = null,
    val currentCashoutValue: Double = 0.0
)

@Entity(tableName = "bet_items")
data class BetItemEntity(
    @PrimaryKey val id: String,
    val ticketId: String,
    val matchId: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val marketType: String, // 1X2, OVER_UNDER_2_5, DOUBLE_CHANCE, BTTS, EXACT_SCORE, NEXT_GOAL
    val marketNameFr: String,
    val selectionKey: String, // "1", "X", "2", "OVER", "UNDER", "1X", "12", "X2", "YES", "NO", "2-1", etc.
    val selectionLabel: String,
    val odds: Double,
    val status: String = "PENDING" // PENDING, WON, LOST
)

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val balance: Double = 25000.0,
    val currencySymbol: String = "KGC", // or FCFA
    val totalBetsPlaced: Int = 0,
    val totalWonBets: Int = 0,
    val totalLostBets: Int = 0,
    val totalProfit: Double = 0.0,
    val biggestWin: Double = 0.0,
    val lastDailyBonusTime: Long = 0L,
    val pinCode: String? = null,
    val isPinProtectionActive: Boolean = false,
    val simulationStyle: String = "VIRTUEL_EXPRESS", // VIRTUEL_EXPRESS, VIRTUEL_MOYEN, TEMPS_REEL
    val vipXp: Int = 1200
)
