package com.example.kgcfootball.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kgcfootball.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        LeagueEntity::class,
        TeamEntity::class,
        PlayerEntity::class,
        MatchFixtureEntity::class,
        MatchEventEntity::class,
        BetTicketEntity::class,
        BetItemEntity::class,
        UserWalletEntity::class,
        SeasonAwardEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao
    abstract fun playerDao(): PlayerDao
    abstract fun matchDao(): MatchDao
    abstract fun betDao(): BetDao
    abstract fun userWalletDao(): UserWalletDao
    abstract fun seasonAwardDao(): SeasonAwardDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kgc_football.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    seedDatabase(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(database: AppDatabase) {
            database.leagueDao().insertLeagues(DatabaseSeeder.getInitialLeagues())
            database.teamDao().insertTeams(DatabaseSeeder.getInitialTeams())
            database.playerDao().insertPlayers(DatabaseSeeder.getInitialPlayers())
            database.matchDao().insertFixtures(DatabaseSeeder.getInitialFixtures())
            if (database.userWalletDao().getWalletSync() == null) {
                database.userWalletDao().insertWallet(
                    UserWalletEntity(
                        id = 1,
                        balance = 25000.0,
                        currencySymbol = "KGC",
                        totalBetsPlaced = 0,
                        totalWonBets = 0,
                        totalLostBets = 0,
                        totalProfit = 0.0,
                        biggestWin = 0.0
                    )
                )
            }
        }
    }
}
