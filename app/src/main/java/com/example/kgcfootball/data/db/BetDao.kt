package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.kgcfootball.data.model.BetItemEntity
import com.example.kgcfootball.data.model.BetTicketEntity
import kotlinx.coroutines.flow.Flow

data class BetTicketWithItems(
    val ticket: BetTicketEntity,
    val items: List<BetItemEntity>
)

@Dao
interface BetDao {
    @Query("SELECT * FROM bet_tickets ORDER BY placedAt DESC")
    fun getAllTickets(): Flow<List<BetTicketEntity>>

    @Query("SELECT * FROM bet_tickets WHERE status = 'PENDING'")
    suspend fun getPendingTicketsSync(): List<BetTicketEntity>

    @Query("SELECT * FROM bet_items WHERE ticketId = :ticketId")
    fun getItemsForTicket(ticketId: String): Flow<List<BetItemEntity>>

    @Query("SELECT * FROM bet_items WHERE ticketId = :ticketId")
    suspend fun getItemsForTicketSync(ticketId: String): List<BetItemEntity>

    @Query("SELECT * FROM bet_items WHERE matchId = :matchId")
    suspend fun getItemsForMatchSync(matchId: String): List<BetItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: BetTicketEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBetItems(items: List<BetItemEntity>)

    @Update
    suspend fun updateTicket(ticket: BetTicketEntity)

    @Update
    suspend fun updateBetItem(item: BetItemEntity)

    @Update
    suspend fun updateBetItems(items: List<BetItemEntity>)
}
