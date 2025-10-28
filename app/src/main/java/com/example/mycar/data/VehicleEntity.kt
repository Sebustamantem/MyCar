package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val km: String,
    val soapDate: String,
    val permisoCirculacionDate: String,
    val revisionTecnicaDate: String
)

