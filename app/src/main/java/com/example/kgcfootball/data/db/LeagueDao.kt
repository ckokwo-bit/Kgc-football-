package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kgcfootball.data.model.LeagueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Query("SELECT * FROM leagues")
    fun getAllLeagues(): Flow<List<LeagueEntity>>

    @Query("SELECT * FROM leagues WHERE id = :id LIMIT 1")
    suspend fun getLeagueById(id: String): LeagueEntity?

    @Query("SELECT * FROM leagues WHERE id = :id LIMIT 1")
    fun getLeagueFlow(id: String): Flow<LeagueEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeagues(leagues: List<LeagueEntity>)

    @Update
    suspend fun updateLeague(league: LeagueEntity)
}
