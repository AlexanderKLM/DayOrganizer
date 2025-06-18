package com.example.dayorganizer

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dayorganizer.databinding.CardLayoutBinding

class CardAdapter(
    private val items: List<CardItem>,
    private val clickListener: CardClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CARD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is CardItem.Header -> TYPE_HEADER
            is CardItem.Card -> TYPE_CARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val textView = TextView(parent.context).apply {
                    setPadding(32, 24, 0, 12)
                    textSize = 18f
                    setTextColor(Color.WHITE)
                }
                object : RecyclerView.ViewHolder(textView) {}
            }
            TYPE_CARD -> {
                val binding = CardLayoutBinding.inflate(inflater, parent, false)
                CardViewHolder(parent.context, binding, clickListener)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is CardItem.Header -> {
                (holder.itemView as TextView).text = item.title
            }
            is CardItem.Card -> {
                (holder as CardViewHolder).bindCard(item.cardInfo)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}