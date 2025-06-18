package com.example.dayorganizer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {

    @Query("SELECT * FROM Card WHERE date = :date AND userid = :userId ORDER BY priority DESC")
    fun getCardsByDate(date: String, userId: String): Flow<List<CardInfo>>

    @Query("SELECT * FROM Card WHERE userid = :userId")
    fun getAllCards(userId: String): Flow<List<CardInfo>>

    @Insert
    suspend fun insert(card: CardInfo): Long

    @Update
    suspend fun updateCards(cardinfo: CardInfo)

    @Delete
    suspend fun deleteCard(cardinfo: CardInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<CardInfo>)

    @Query("DELETE FROM Card WHERE date  IS NOT NULL < :thresholdDate")
    suspend fun deleteOldCards(thresholdDate: String): Int
}