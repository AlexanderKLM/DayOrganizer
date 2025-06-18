package com.example.dayorganizer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dayorganizer.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.Manifest
import android.widget.Toast
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), CardClickListener, OnCardSavedListener {

    private lateinit var binding: ActivityMainBinding
    private val cardViewModel: CardViewModel by viewModels {
        CardViewModelFactory(
            (application as CardApplication).repository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 100)
        }

        binding.newCardButton.setOnClickListener{
            NewCardFragment(null,this).show(supportFragmentManager, "newcardfragment")
        }

        binding.logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FirebaseLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener {
            val refreshedUser = FirebaseAuth.getInstance().currentUser
            val name = refreshedUser?.displayName ?: refreshedUser?.email ?: "Пользователь"
            binding.usernametextview.text = name

        }

        setupCalendarRecyclerView()
        setRecyclerView()


    }
    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, FirebaseLogin::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }

    private fun setRecyclerView() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cardViewModel.groupedCards.collect { grouped ->

                    val items = mutableListOf<CardItem>()

                    grouped["В процессе"]?.takeIf { it.isNotEmpty() }?.let {
                        items += CardItem.Header("В процессе")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    grouped["Срок прошёл"]?.takeIf { it.isNotEmpty() }?.let {
                        items += CardItem.Header("Срок прошёл")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    grouped["Доделать"]?.takeIf { it.isNotEmpty() }?.let {
                        items += CardItem.Header("Доделать")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    grouped["Выполнено"]?.takeIf { it.isNotEmpty() }?.let {
                        items += CardItem.Header("Выполнено")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    binding.recyclerview.apply {
                        layoutManager = LinearLayoutManager(applicationContext)
                        adapter = CardAdapter(items, this@MainActivity)
                    }
                }
            }
        }
    }

    override fun editCard(cardInfo: CardInfo)
    {
        NewCardFragment(cardInfo, this).show(supportFragmentManager,"newcardfragment")
    }

    override fun completeCard(cardInfo: CardInfo) {
        val updated = cardInfo.copy(isdone = true)
        cardViewModel.updateCards(updated)
    }

    private fun setupCalendarRecyclerView() {
        val today = LocalDate.now()
        val startDate = today.minusMonths(6)
        val endDate = today.plusMonths(6)
        val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1

        val dates = List(totalDays) { index ->
            CalendarDate(startDate.plusDays(index.toLong()))
        }

        val calendarAdapter = CalendarAdapter(dates) { selectedDate ->
            updateCalendarHeader(selectedDate)
            cardViewModel.setFilterDate(selectedDate.toString())
        }

        binding.dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
            adapter = calendarAdapter
            val todayPosition = dates.indexOfFirst { it.date == today }
            if (todayPosition >= 0) {
                scrollToPosition(todayPosition)
            }
        }
        updateCalendarHeader(today)
        cardViewModel.setFilterDate(today.toString())
    }

    private fun updateCalendarHeader(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy, EEEE", Locale("ru"))
        val formattedDate = date.format(formatter)
        binding.calendarHeaderText.text = formattedDate.replaceFirstChar { it.uppercase() }
    }
    override fun onCardSaved(card: CardInfo) {
        Toast.makeText(this, "Задача сохранена: ${card.title}", Toast.LENGTH_SHORT).show()
        scheduleNotification(card)
    }
    private fun scheduleNotification(card: CardInfo) {
        (cardViewModel::class.java.getDeclaredMethod("scheduleNotification", Context::class.java, CardInfo::class.java)
            .apply { isAccessible = true })
            .invoke(cardViewModel, this, card)
    }
}
