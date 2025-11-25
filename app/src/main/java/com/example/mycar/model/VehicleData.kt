package com.example.mycar.model

data class VehicleData(
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val km: Int,
    val soapDate: String,
    val permisoCirculacionDate: String,
    val revisionTecnicaDate: String
)
