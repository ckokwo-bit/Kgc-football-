package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_events")
data class MatchEventEntity(
    @PrimaryKey val id: String,
    val matchId: String,
    val minute: Int,
    val type: String, // GOAL, PENALTY_GOAL, MISSED_PENALTY, YELLOW_CARD, RED_CARD, VAR_DECISION, WOODWORK, BIG_SAVE
    val teamId: String,
    val playerName: String,
    val description: String,
    val scoreAtEvent: String = ""
)
