package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vehiclePlate: String,
    val type: String,
    val date: String,
    val km: String,
    val notes: String
)
