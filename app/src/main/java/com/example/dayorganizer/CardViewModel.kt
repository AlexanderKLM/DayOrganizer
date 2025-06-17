package com.example.dayorganizer

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class CardViewModel(private val repository: CardDBRepository) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now().toString())
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(60_000)
        }
    }

    val groupedCards: StateFlow<Map<String, List<CardInfo>>> = combine(
        selectedDate, ticker
    ) { date, _ -> date }
        .flatMapLatest { date ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            repository.getAllCards(userId).map { allCards ->
                val now = LocalDateTime.now()
                val currentDate = LocalDate.parse(date)

                val repeatedCardsToday = allCards.filter { card ->
                    val isOverdue = card.date?.let {
                        val cardDate = LocalDate.parse(it)
                        if (card.time != null) {
                            val cardTime = LocalTime.parse(card.time)
                            LocalDateTime.of(cardDate, cardTime).isBefore(now)
                        } else {
                            cardDate.isBefore(now.toLocalDate())
                        }
                    } ?: false

                    !card.isdone &&
                            card.priority >= 3 &&
                            isOverdue &&
                            (currentDate >= LocalDate.now())
                }.map { it.copy(date = currentDate.toString()) }

                val dateCards = allCards.filter { it.date == date }

                val combined = (dateCards + repeatedCardsToday).distinctBy { it.id }

                combined.map { card ->
                    val isOverdue = card.date?.let { d ->
                        val cardDate = LocalDate.parse(d)
                        if (card.time != null) {
                            val cardTime = LocalTime.parse(card.time)
                            LocalDateTime.of(cardDate, cardTime).isBefore(now)
                        } else {
                            cardDate.isBefore(now.toLocalDate())
                        }
                    } ?: false

                    card.copy(isoverdue = isOverdue)
                }.groupBy {
                    when {
                        it.isdone -> "Выполнено"
                        it.isoverdue -> "Срок прошёл"
                        else -> "В процессе"
                    }
                }.mapValues { (key, list) ->
                    if (key == "Срок прошёл") {
                        list.sortedByDescending { it.priority }
                    } else {
                        list
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    fun setFilterDate(date: String) {
        selectedDate.value = date
    }

    fun insertCard(cardInfo: CardInfo) = viewModelScope.launch {
        repository.insertCards(cardInfo)
    }

    fun updateCards(cardInfo: CardInfo) = viewModelScope.launch {
        repository.updateCards(cardInfo)
    }

    fun deleteCard(cardInfo: CardInfo) = viewModelScope.launch {
        repository.deleteCard(cardInfo)
    }
}
class CardViewModelFactory(
    private val repository: CardDBRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}