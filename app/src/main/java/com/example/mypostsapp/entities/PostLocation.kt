package com.example.mypostsapp.entities

import java.io.Serializable


data class PostLocation(val latitude: Double = 0.0,
                        val longitude: Double = 0.0,
                        val address: String = ""): Serializable {}


