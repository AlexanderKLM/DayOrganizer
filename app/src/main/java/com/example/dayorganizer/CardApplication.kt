package com.example.dayorganizer

import android.app.Application

class CardApplication : Application() {

        private val database by lazy { CardDatabase.getDatabase(this) }
        val repository by lazy { CardDBRepository(database.CardDAO()) }

}