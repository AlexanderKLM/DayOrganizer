package com.example.dayorganizer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private val datalist: ArrayList<CardInfo>): RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout,parent, false)
        return CardViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val dataitem = datalist[position]
        holder.cardtitle.text = dataitem.title
        holder.cardinfo.text = dataitem.desc

        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return datalist.size
    }


    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val cardtitle:TextView = itemView.findViewById(R.id.TVcardName)
        val cardinfo:TextView = itemView.findViewById(R.id.TVcardinfo)

    }

}