package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fixtures")
data class MatchFixtureEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val round: Int,
    val homeTeamId: String,
    val awayTeamId: String,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val status: String = "SCHEDULED", // SCHEDULED, LIVE, FINISHED
    val minute: Int = 0,
    val extraTime: Int = 0,
    val homeShots: Int = 0,
    val awayShots: Int = 0,
    val homeShotsOnTarget: Int = 0,
    val awayShotsOnTarget: Int = 0,
    val homePossession: Int = 50,
    val awayPossession: Int = 50,
    val homeFouls: Int = 0,
    val awayFouls: Int = 0,
    val homeYellowCards: Int = 0,
    val awayYellowCards: Int = 0,
    val homeRedCards: Int = 0,
    val awayRedCards: Int = 0,
    val homeCorners: Int = 0,
    val awayCorners: Int = 0,
    val homeXg: Float = 0.0f,
    val awayXg: Float = 0.0f,
    val lastEventText: String = "Coup d'envoi à venir",
    // Coordonnées 2D réalistes de la balle sur le terrain (X: 0.05..0.95, Y: 0.10..0.90)
    val ballX: Float = 0.5f,
    val ballY: Float = 0.5f,
    val playZoneDescription: String = "Construction au rond central",
    // Programme horaire réaliste du match et compte à rebours avant coup d'envoi
    val scheduledKickoffTime: String = "15:00",
    val countdownSecondsRemaining: Int = 180,
    // Horodatages pour simulation persistante en temps réel (fonctionne même app fermée)
    val kickoffTimestamp: Long = 0L,
    val lastSimulatedTimestamp: Long = 0L,
    val simulationMode: String = "VIRTUEL_EXPRESS" // VIRTUEL_EXPRESS, VIRTUEL_MOYEN, TEMPS_REEL
)
