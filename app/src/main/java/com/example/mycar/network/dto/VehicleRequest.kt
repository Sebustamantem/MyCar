package com.example.mycar.network.dto

data class VehicleRequest(
    val ownerEmail: String,
    val brand: String,
    val model: String,
    val year: Int,
    val plate: String,
    val km: String,
    val soapDate: String,
    val permisoCirculacionDate: String,
    val revisionTecnicaDate: String
)
