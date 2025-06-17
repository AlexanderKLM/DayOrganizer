package com.example.dayorganizer

sealed class CardItem {
    data class Header(val title: String) : CardItem()
    data class Card(val cardInfo: CardInfo) : CardItem()
}