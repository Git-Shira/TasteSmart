package com.example.mypostsapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.example.mypostsapp.room.PostListConverter
import java.io.Serializable

@Entity(tableName = "users")
data class User(@PrimaryKey var uid: String = "",
                var name: String? = null,
                var email: String? = null,
                var image: String? = null) : Serializable