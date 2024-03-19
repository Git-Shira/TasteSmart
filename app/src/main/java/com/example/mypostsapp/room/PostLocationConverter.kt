package com.example.mypostsapp.room

import androidx.room.TypeConverter

import com.example.mypostsapp.entities.PostLocation
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PostLocationConverter {
    @TypeConverter
    fun fromList(location: PostLocation): String {
        val gson = Gson()
        return gson.toJson(location)
    }

    @TypeConverter
    fun toList(data: String): PostLocation {
        val locationType = object : TypeToken<PostLocation>() {}.type
        return Gson().fromJson(data, locationType)
    }
}