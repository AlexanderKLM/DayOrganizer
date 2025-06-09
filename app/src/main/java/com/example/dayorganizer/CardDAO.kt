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
    @Query("SELECT * FROM Card ORDER BY id ASC")
    fun allCardsShow(): Flow<List<CardInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(cardinfo: CardInfo)

    @Update
    suspend fun updateCards(cardinfo: CardInfo)

    @Delete
    suspend fun deleteCard(cardinfo: CardInfo)
}