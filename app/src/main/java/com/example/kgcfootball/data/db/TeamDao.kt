package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kgcfootball.data.model.TeamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Query("SELECT * FROM teams WHERE leagueId = :leagueId ORDER BY points DESC, (goalsFor - goalsAgainst) DESC, goalsFor DESC")
    fun getStandingsByLeague(leagueId: String): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE leagueId = :leagueId")
    suspend fun getTeamsByLeagueSync(leagueId: String): List<TeamEntity>

    @Query("SELECT * FROM teams")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams")
    suspend fun getAllTeamsSync(): List<TeamEntity>

    @Query("SELECT * FROM teams WHERE id = :id LIMIT 1")
    suspend fun getTeamById(id: String): TeamEntity?

    @Query("SELECT * FROM teams WHERE id = :id LIMIT 1")
    fun getTeamFlow(id: String): Flow<TeamEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Update
    suspend fun updateTeam(team: TeamEntity)

    @Update
    suspend fun updateTeams(teams: List<TeamEntity>)
}
