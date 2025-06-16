package com.example.dayorganizer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dayorganizer.databinding.CardLayoutBinding

class CardAdapter(
    private val cardsInfo: List<CardInfo>,
    private val clickListener: CardClickListener): RecyclerView.Adapter<CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(from, parent,false)
        return CardViewHolder(parent.context, binding, clickListener)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
       holder.bindCard(cardsInfo[position])
    }

    override fun getItemCount(): Int {
        return cardsInfo.size
    }

}