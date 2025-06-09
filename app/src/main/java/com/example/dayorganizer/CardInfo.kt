package com.example.dayorganizer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Card")
data class CardInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "prority") var prority: Int,
    @ColumnInfo(name = "category") var category: String?,
    @ColumnInfo(name = "isDone") var isdone: Boolean,
    @ColumnInfo(name = "isOverDue") var isoverdue: Boolean,
    @ColumnInfo(name = "isRepeating") var isrepeating: Boolean,
    @ColumnInfo(name = "userid") var userid: Int
    )
