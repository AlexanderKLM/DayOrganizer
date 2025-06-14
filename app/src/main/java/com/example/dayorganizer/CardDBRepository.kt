package com.example.dayorganizer

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class CardDBRepository (private val cardDAO: CardDAO){

    val allCardsShow: Flow<List<CardInfo>> = cardDAO.allCardsShow()


    @WorkerThread
    suspend fun insertCards(cardInfo: CardInfo){
        cardDAO.insertCard(cardInfo)
    }

    @WorkerThread
    suspend fun updateCards(cardInfo: CardInfo){
        cardDAO.updateCards(cardInfo)
    }

    @WorkerThread
    suspend fun deleteCard(cardInfo: CardInfo) {
        cardDAO.deleteCard(cardInfo)
    }
    fun getCardsByDate(date: String): Flow<List<CardInfo>> {
        return cardDAO.getCardsByDate(date)
    }
}