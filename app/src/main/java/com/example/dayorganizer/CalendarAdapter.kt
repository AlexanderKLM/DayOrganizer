package com.example.dayorganizer

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class CalendarAdapter(
    private val dates: List<CalendarDate>,
    private val onDateSelected: (LocalDate) -> Unit

) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selectedPosition = -1

    inner class CalendarViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val textView = TextView(parent.context).apply {
            val sizeInDp = 50
            val scale = context.resources.displayMetrics.density
            val sizeInPx = (sizeInDp * scale + 0.5f).toInt()

            val layoutParams = ViewGroup.MarginLayoutParams(sizeInPx, sizeInPx)
            layoutParams.setMargins(8, 8, 8, 0)
            this.layoutParams = layoutParams

            gravity = Gravity.CENTER
            textSize = 16f
            background = ContextCompat.getDrawable(context, R.drawable.calendar_date_select)
        }
        return CalendarViewHolder(textView)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        if (adapterPosition == RecyclerView.NO_POSITION) return

        val item = dates[adapterPosition]
        val date = item.date

        holder.textView.text = date.dayOfMonth.toString()

        if (adapterPosition == selectedPosition) {
            holder.textView.setTextColor(Color.BLACK)
            holder.textView.background = ContextCompat.getDrawable(
                holder.textView.context,
                R.drawable.calendar_date_select
            )
        } else {
            holder.textView.setTextColor(Color.WHITE)
            holder.textView.background = ContextCompat.getDrawable(
                holder.textView.context,
                R.drawable.calendar_date_default
            )
        }

        holder.textView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = adapterPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onDateSelected(date)
        }
    }

    override fun getItemCount() = dates.size
}