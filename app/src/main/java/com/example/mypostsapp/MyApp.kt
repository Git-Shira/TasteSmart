package com.example.mypostsapp

import android.app.Application
import com.example.mypostsapp.room.RoomManager

class MyApp : Application() {


    override fun onCreate() {
        super.onCreate()
        RoomManager.initManager(this)
    }
}