package com.example.dayorganizer

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class CardViewModel(private val repository: CardDBRepository): ViewModel() {
    val cardsInfo: LiveData<List<CardInfo>> = repository.allCardsShow.asLiveData()

    fun insertCard(cardInfo: CardInfo) = viewModelScope.launch {
        repository.insertCards(cardInfo)
    }

    fun updateCards(cardInfo: CardInfo) = viewModelScope.launch {
        repository.updateCards(cardInfo)
    }
    fun deleteCard(cardInfo: CardInfo) = viewModelScope.launch {
        repository.deleteCard(cardInfo)
    }
    fun getCardsByDate(date: String): LiveData<List<CardInfo>> =
        repository.getCardsByDate(date).asLiveData()
}
class CardViewModelFactory(private val repository: CardDBRepository) : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if (modelClass.isAssignableFrom(CardViewModel::class.java))
            return CardViewModel(repository) as T

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}