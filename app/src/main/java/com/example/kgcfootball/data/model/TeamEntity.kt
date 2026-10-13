package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val leagueId: String,
    val name: String,
    val shortName: String,
    val stadiumName: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val attackRating: Int,
    val defenseRating: Int,
    val midfieldRating: Int,
    val overallRating: Int,
    val tacticStyle: String, // TIKI_TAKA, GEGENPRESS, CONTRE_ATTAQUE, CATENACCIO, EQUILIBRE
    val formation: String,   // 4-3-3, 4-2-3-1, 4-4-2, 3-5-2
    val played: Int = 0,
    val won: Int = 0,
    val drawn: Int = 0,
    val lost: Int = 0,
    val goalsFor: Int = 0,
    val goalsAgainst: Int = 0,
    val points: Int = 0,
    val formString: String = "V,N,V,D,V", // W=V, D=N, L=D
    // Personnalité et tactique de l'entraîneur (comme dans la vraie vie)
    val coachName: String = "Entraîneur KGC",
    val coachPersonality: String = "Tacticien Ambitieux & Exigeant",
    val coachTacticalQuote: String = "Intensité dans les duels et transition chirurgicale.",
    val starPlayerName: String = "Buteur Star",
    // Données historiques H2H et moyennes de buts pour analyses d'avant-match
    val recentH2hWins: Int = 3,
    val recentH2hDraws: Int = 1,
    val recentH2hLosses: Int = 1,
    val avgGoalsScored: Float = 2.2f,
    val avgGoalsConceded: Float = 0.9f
) {
    val goalDifference: Int get() = goalsFor - goalsAgainst
}
