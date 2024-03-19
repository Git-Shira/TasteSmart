package com.example.mypostsapp.room

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class StringsConverter {
    @TypeConverter
    fun fromList(list: ArrayList<String>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toList(data: String?): ArrayList<String>? {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
         data?.let {
            return Gson().fromJson(data, listType)
        } ?: run {
            return null
         }
    }
}