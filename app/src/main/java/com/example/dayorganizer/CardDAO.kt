package com.example.dayorganizer

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDAO {

    @Query("SELECT * FROM Card WHERE userid = :userId")
    fun getAllCards(userId: String): Flow<List<CardInfo>>

    @Insert
    suspend fun insert(card: CardInfo): Long

    @Update
    suspend fun updateCards(cardinfo: CardInfo)

    @Delete
    suspend fun deleteCard(cardinfo: CardInfo)

    @Query("DELETE FROM Card WHERE date < :fiveMonthsAgoDate")
    suspend fun deleteOldCards(fiveMonthsAgoDate: String): Int

}