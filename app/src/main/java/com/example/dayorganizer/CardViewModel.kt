package com.example.dayorganizer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class CardViewModel(private val repository: CardDBRepository) : ViewModel() {

    private val selectedDate = MutableStateFlow(LocalDate.now().toString())
    private val ticker = flow {
        while (true) {
            emit(Unit)
            delay(5_000)
        }
    }

    val groupedCards: StateFlow<Map<String, List<CardInfo>>> = combine(
        selectedDate, ticker
    ) { date, _ -> date }
        .flatMapLatest { date ->
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            repository.getAllCards(userId).map { allCards ->
                val now = LocalDateTime.now()
                val selectedDateParsed = LocalDate.parse(date)

                val combined = allCards.map { card ->
                    val cardDate = card.date?.let { LocalDate.parse(it) }
                    val cardTime = card.time?.let { LocalTime.parse(it) }
                    val cardDateTime = if (cardDate != null && cardTime != null)
                        LocalDateTime.of(cardDate, cardTime)
                    else cardDate?.atStartOfDay()

                    val isOverdue = cardDateTime?.isBefore(now) ?: false

                    card.copy(isoverdue = isOverdue)
                }

                val inProgress = mutableListOf<CardInfo>()
                val overdueToday = mutableListOf<CardInfo>()
                val redoLater = mutableListOf<CardInfo>()
                val done = mutableListOf<CardInfo>()

                combined.forEach { card ->
                    val cardDate = card.date?.let { LocalDate.parse(it) }

                    when {
                        card.isdone && cardDate == selectedDateParsed -> done += card

                        cardDate == selectedDateParsed && card.isoverdue -> overdueToday += card

                        cardDate == selectedDateParsed && !card.isoverdue -> inProgress += card

                        cardDate != null && cardDate < selectedDateParsed && card.isoverdue && !card.isdone -> redoLater += card
                    }
                }

                mapOf(
                    "В процессе" to inProgress.sortedWith(
                        compareBy { it.time?.let { t -> LocalTime.parse(t) } ?: LocalTime.MAX }
                    ),
                    "Срок прошёл" to overdueToday.sortedByDescending { it.priority },
                    "Доделать" to redoLater.sortedByDescending { it.priority },
                    "Выполнено" to done
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    fun setFilterDate(date: String) {
        selectedDate.value = date
    }

    fun insertCard(card: CardInfo, context: Context): LiveData<CardInfo> {
        val result = MutableLiveData<CardInfo>()
        viewModelScope.launch {
            val id = repository.insert(card)
            val updatedCard = card.copy(id = id.toInt())
            scheduleNotification(context, updatedCard)
            result.postValue(updatedCard)
        }
        return result
    }

    fun updateCards(card: CardInfo) {
        viewModelScope.launch {
            repository.updateCards(card)
        }
    }
    fun deleteCard(cardInfo: CardInfo) = viewModelScope.launch {
        repository.deleteCard(cardInfo)
    }
    private fun scheduleNotification(context: Context, card: CardInfo) {
        val date = card.datefill()
        val time = card.timefill()

        if (date == null || time == null) return

        val dateTime = LocalDateTime.of(date, time)
        val triggerAtMillis = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("title", card.title)
            putExtra("desc", card.desc)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            card.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
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