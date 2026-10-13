package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kgcfootball.data.model.SeasonAwardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonAwardDao {
    @Query("SELECT * FROM season_awards ORDER BY awardedAt DESC")
    fun getAllAwards(): Flow<List<SeasonAwardEntity>>

    @Query("SELECT * FROM season_awards WHERE leagueId = :leagueId ORDER BY awardedAt DESC")
    fun getAwardsByLeague(leagueId: String): Flow<List<SeasonAwardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAward(award: SeasonAwardEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAwards(awards: List<SeasonAwardEntity>)
}
