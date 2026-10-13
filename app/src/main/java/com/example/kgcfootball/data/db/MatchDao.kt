package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kgcfootball.data.model.MatchEventEntity
import com.example.kgcfootball.data.model.MatchFixtureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Query("SELECT * FROM fixtures WHERE leagueId = :leagueId AND round = :round")
    fun getFixturesByRound(leagueId: String, round: Int): Flow<List<MatchFixtureEntity>>

    @Query("SELECT * FROM fixtures WHERE leagueId = :leagueId AND round = :round")
    suspend fun getFixturesByRoundSync(leagueId: String, round: Int): List<MatchFixtureEntity>

    @Query("SELECT * FROM fixtures WHERE leagueId = :leagueId")
    fun getAllFixturesForLeague(leagueId: String): Flow<List<MatchFixtureEntity>>

    @Query("SELECT * FROM fixtures WHERE id = :id LIMIT 1")
    fun getFixtureFlow(id: String): Flow<MatchFixtureEntity?>

    @Query("SELECT * FROM fixtures WHERE id = :id LIMIT 1")
    suspend fun getFixtureById(id: String): MatchFixtureEntity?

    @Query("SELECT * FROM fixtures WHERE status = 'LIVE'")
    fun getLiveFixtures(): Flow<List<MatchFixtureEntity>>

    @Query("SELECT * FROM fixtures WHERE status = 'LIVE'")
    suspend fun getLiveFixturesSync(): List<MatchFixtureEntity>

    @Query("SELECT * FROM fixtures WHERE status = 'SCHEDULED'")
    suspend fun getAllScheduledFixturesSync(): List<MatchFixtureEntity>

    @Query("SELECT COUNT(*) FROM fixtures")
    suspend fun getFixtureCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFixtures(fixtures: List<MatchFixtureEntity>)

    @Update
    suspend fun updateFixture(fixture: MatchFixtureEntity)

    @Update
    suspend fun updateFixtures(fixtures: List<MatchFixtureEntity>)

    // Match Events
    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC, id ASC")
    fun getEventsByMatch(matchId: String): Flow<List<MatchEventEntity>>

    @Query("SELECT * FROM match_events WHERE matchId = :matchId ORDER BY minute ASC, id ASC")
    suspend fun getEventsByMatchSync(matchId: String): List<MatchEventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: MatchEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<MatchEventEntity>)

    @Query("DELETE FROM match_events WHERE matchId = :matchId")
    suspend fun clearEventsForMatch(matchId: String)
}
