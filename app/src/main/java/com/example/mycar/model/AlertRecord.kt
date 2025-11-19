package com.example.mycar.model
import com.example.mycar.model.AlertRecord

data class AlertRecord(
    val id: Long,
    val title: String,
    val message: String,
    val date: String
)