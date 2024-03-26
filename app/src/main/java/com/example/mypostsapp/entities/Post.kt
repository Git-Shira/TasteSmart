package com.example.mypostsapp.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.example.mypostsapp.room.PostLocationConverter
import com.example.mypostsapp.room.StringsConverter
import com.example.mypostsapp.room.UssrConverter
import java.io.Serializable

@Entity(tableName = "posts")
data class Post(
    @PrimaryKey var uid: String = "",
    var description: String? = null,
    var image: String? = null,
    @TypeConverters(UssrConverter::class) var createdUser: User? = null,
    @TypeConverters(StringsConverter::class)  var likeUserIds: ArrayList<String>? = arrayListOf(),
    var created: Long? = null,
    @TypeConverters(PostLocationConverter::class) var location: PostLocation? = null
) : Serializable
