package com.example.kgcfootball.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kgcfootball.data.model.UserWalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    fun getWalletFlow(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1 LIMIT 1")
    suspend fun getWalletSync(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(wallet: UserWalletEntity)

    @Update
    suspend fun updateWallet(wallet: UserWalletEntity)
}
