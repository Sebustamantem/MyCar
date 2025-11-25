package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val type: String,
    val vehiclePlate: String,
    val date: String,
    val km: Int,   // <--- AHORA ES INT
    val notes: String
)
