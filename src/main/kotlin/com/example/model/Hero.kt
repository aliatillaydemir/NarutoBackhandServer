package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Hero(  //when we send request, we don't use this class directly. we use ApiResponse class.
    val id: Int,
    val name: String,
    val image: String,
    val about: String,
    val rating: Double,
    val power: Int,
    val month: String,
    val day: String,
    val family: List<String>,
    val abilities: List<String>,
    val natureTypes: List<String>
)
