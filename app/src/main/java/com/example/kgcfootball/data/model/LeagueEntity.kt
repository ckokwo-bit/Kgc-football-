package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leagues")
data class LeagueEntity(
    @PrimaryKey val id: String,
    val name: String,
    val country: String,
    val flagEmoji: String,
    val currentRound: Int = 1,
    val totalRounds: Int = 10,
    val season: String = "2026/2027"
)
