package com.example.dayorganizer

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "Card")
data class CardInfo(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "desc") var desc: String,
    @ColumnInfo(name = "date") var date: String?,
    @ColumnInfo(name = "time") var time: String?,
    @ColumnInfo(name = "priority") var priority: Int,
    @ColumnInfo(name = "isDone") var isdone: Boolean,
    @ColumnInfo(name = "isOverDue") var isoverdue: Boolean,
    @ColumnInfo(name = "isRepeating") var isrepeating: Boolean,
    @ColumnInfo(name = "userid") var userid: String
) {

    fun timefill(): LocalTime? = try {
        if (time.isNullOrEmpty()) null else LocalTime.parse(time, timeFormatter)
    } catch (e: Exception) {
        null
    }

    fun datefill(): LocalDate? = try {
        if (date.isNullOrEmpty()) null else LocalDate.parse(date, dateFormatter)
    } catch (e: Exception) {
        null
    }
    companion object {
        val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_TIME
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE
    }


}
