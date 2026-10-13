package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "season_awards")
data class SeasonAwardEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val leagueName: String,
    val seasonNumber: Int,
    val seasonLabel: String,
    val championTeamId: String,
    val championTeamName: String,
    val runnerUpTeamName: String,
    val ballonDorPlayerName: String,
    val ballonDorTeamName: String,
    val soulierDorPlayerName: String,
    val soulierDorGoals: Int,
    val soulierDorTeamName: String,
    val gantDorPlayerName: String,
    val gantDorTeamName: String,
    val awardedAt: Long = System.currentTimeMillis()
)
