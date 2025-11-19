package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey val id: Long,
    val userEmail: String,
    val title: String,
    val message: String,
    val date: String
)
