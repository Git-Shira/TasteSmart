package com.example.mypostsapp.room

import android.content.Context
import androidx.room.Room

object RoomManager {
    lateinit var database: AppDatabase

    fun initManager(applicationContext: Context) {
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "my_database"
        ).build()

    }
}