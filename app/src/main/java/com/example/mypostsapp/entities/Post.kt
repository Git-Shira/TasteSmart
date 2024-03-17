package com.example.mypostsapp.entities

import java.io.Serializable

data class Post(var uid: String?=null, var description: String? = null,
                var image: String ?= null,
                var createdUser: User ?= null, var likeUserIds: List<String> ?= arrayListOf(), var created: Long ?= null ): Serializable {
}
