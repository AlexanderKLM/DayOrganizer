package com.example.dayorganizer

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class CardDBRepository (private val cardDAO: CardDAO){


    @WorkerThread
    suspend fun insert(cardInfo: CardInfo): Long{
       return cardDAO.insert(cardInfo)
    }

    @WorkerThread
    suspend fun updateCards(cardInfo: CardInfo){
        cardDAO.updateCards(cardInfo)
    }

    @WorkerThread
    suspend fun deleteCard(cardInfo: CardInfo) {
        cardDAO.deleteCard(cardInfo)
    }

    fun getAllCards(userId: String): Flow<List<CardInfo>> {
        return cardDAO.getAllCards(userId)
    }

    @WorkerThread
    suspend fun deleteOldCards(fiveMonthsAgoDate: String): Int {
        return cardDAO.deleteOldCards(fiveMonthsAgoDate)
    }

}