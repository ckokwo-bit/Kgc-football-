package com.example.kgcfootball.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val teamId: String,
    val name: String,
    val number: Int,
    val position: String, // GK, DEF, MID, ATT
    val rating: Int,
    val goals: Int = 0,
    val assists: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0,
    val isInjured: Boolean = false,
    val specialty: String = "Polyvalent", // Finisseur chirurgical, Métronome visionnaire, Mur infranchissable, Roc défensif, Dribbleur supersonique
    val personality: String = "Déterminé" // Leader inspirant, Sang-froid absolu, Combattant infatigable, Prodige impétueux
)
