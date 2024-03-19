package com.example.mypostsapp.room

import androidx.room.TypeConverter
import com.example.mypostsapp.entities.Post
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class PostListConverter {
    @TypeConverter
    fun fromPostsList(postList: ArrayList<Post>?): String? {
        val gson = Gson()
        return gson.toJson(postList)
    }

    @TypeConverter
    fun toPostsList(postListString: String?): ArrayList<Post>? {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<Post>>() {}.type
        return gson.fromJson(postListString, type)
    }
}