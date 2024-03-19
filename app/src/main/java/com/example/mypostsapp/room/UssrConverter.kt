package com.example.mypostsapp.room

import androidx.room.TypeConverter
import com.example.mypostsapp.entities.User
import com.google.gson.Gson

class UssrConverter {
    @TypeConverter
    fun fromUser(user: User?): String? {
        return user?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toUser(data: String?): User? {
        return data?.let { Gson().fromJson(it, User::class.java) }
    }
}