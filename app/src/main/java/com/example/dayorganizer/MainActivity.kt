package com.example.dayorganizer

import android.content.Intent
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

class MainActivity : AppCompatActivity(), CardClickListener {

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

        binding.newCardButton.setOnClickListener{
            NewCardFragment(null).show(supportFragmentManager, "newcardfragment")
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

                    grouped["В процессе"]?.let {
                        items += CardItem.Header("В процессе")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    grouped["Выполнено"]?.let {
                        items += CardItem.Header("Выполнено")
                        items += it.map { card -> CardItem.Card(card) }
                    }

                    grouped["Срок прошёл"]?.let {
                        items += CardItem.Header("Срок прошёл")
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
        NewCardFragment(cardInfo).show(supportFragmentManager,"newcardfragment")
    }

    override fun completeCard(cardInfo: CardInfo) {
        val updated = cardInfo.copy(isdone = true)
        cardViewModel.updateCards(updated)
    }

    private fun setupCalendarRecyclerView() {
        val today = LocalDate.now()
        val dates = List(30) { CalendarDate(today.plusDays(it.toLong())) }

        val calendarAdapter = CalendarAdapter(dates) { selectedDate ->
            updateCalendarHeader(selectedDate)
            cardViewModel.setFilterDate(selectedDate.toString())
        }

        binding.dateRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.HORIZONTAL, false)
            adapter = calendarAdapter
        }
        updateCalendarHeader(today)
        cardViewModel.setFilterDate(today.toString())
    }

    private fun updateCalendarHeader(date: LocalDate) {
        val formatter = DateTimeFormatter.ofPattern("LLLL yyyy, EEEE", Locale("ru"))
        val formattedDate = date.format(formatter)
        binding.calendarHeaderText.text = formattedDate.replaceFirstChar { it.uppercase() }
    }
}
