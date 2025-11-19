package com.example.mycar.network.dto

data class AlertRequest(
    val userEmail: String,
    val title: String,
    val message: String,
    val date: String
)
