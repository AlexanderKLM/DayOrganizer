package com.example.dayorganizer

interface CardClickListener {
    fun editCard(cardInfo: CardInfo)
    fun completeCard(cardInfo: CardInfo)
}