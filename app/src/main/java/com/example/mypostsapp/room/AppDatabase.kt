package com.example.mypostsapp.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mypostsapp.entities.Post
import com.example.mypostsapp.entities.User

@Database(entities = [User::class, Post::class], version = 1)
@TypeConverters(PostLocationConverter::class, UssrConverter::class, StringsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}