package com.example.dayorganizer

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
import com.example.dayorganizer.databinding.CardLayoutBinding

class CardViewHolder (
    private val context: Context,
    private val binding: CardLayoutBinding,
    private val clickListener: CardClickListener) : RecyclerView.ViewHolder(binding.root)
    {

    fun bindCard(cardInfo: CardInfo)
    {
        binding.cardtitle.text = cardInfo.title
        binding.carddesc.text = cardInfo.desc
        val formattedDate = cardInfo.datefill()?.format(CardInfo.dateFormatter) ?: "Нет даты"
        binding.carddate.text = formattedDate
        val formattedTime = cardInfo.dueTime()?.format(CardInfo.timeFormatter) ?: "Нет времени"
        binding.cardtime.text = formattedTime

        setCardBorder(cardInfo.priority)
        binding.root.setOnClickListener {
            clickListener.editCard(cardInfo)
        }
    }
        private fun setCardBorder(priority: Int) {
            val borderColor = when (priority) {
                1 -> ContextCompat.getColor(context, R.color.priority_gray)
                2 -> ContextCompat.getColor(context, R.color.priority_blue)
                3 -> ContextCompat.getColor(context, R.color.priority_purple)
                4 -> ContextCompat.getColor(context, R.color.priority_red)
                else -> ContextCompat.getColor(context, R.color.priority_gray)
            }

                val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setColor(Color.parseColor("#2D2D2D"))
                setStroke(6, borderColor)
                cornerRadius = 24f
            }


            binding.Cardlayout.background = drawable
        }


}