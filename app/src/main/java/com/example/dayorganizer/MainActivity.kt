package com.example.dayorganizer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dayorganizer.databinding.ActivityMainBinding
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity(), CardClickListener {

    private lateinit var binding: ActivityMainBinding
    private val cardViewModel: CardViewModel by viewModels {
        CardViewModelFactory((application as CardApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.newCardButton.setOnClickListener{
            NewCardFragment(null).show(supportFragmentManager, "newcardfragment")
        }
        setRecyclerView()
    }

    private fun setRecyclerView()
    {
        val mainActivity = this
        cardViewModel.cardsInfo.observe(this){
         binding.recyclerview.apply {
             layoutManager = LinearLayoutManager(applicationContext)
             adapter = CardAdapter(it, mainActivity)
         }
        }
    }

    override fun editCard(cardInfo: CardInfo)
    {
        NewCardFragment(cardInfo).show(supportFragmentManager,"newcardfragment")
    }

    override fun completeCard(cardInfo: CardInfo) {
        TODO("Not yet implemented")
    }
}
