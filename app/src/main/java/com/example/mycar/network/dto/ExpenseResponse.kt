package com.example.mycar.network.dto

data class ExpenseResponse(
    val id: Long,
    val vehicleId: Long,
    val vehiclePlate: String,
    val category: String,
    val type: String,
    val date: String,
    val amount: Int,
    val km: Int?,
    val notes: String?
)
