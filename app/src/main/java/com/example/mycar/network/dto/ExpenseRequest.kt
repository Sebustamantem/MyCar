package com.example.mycar.network.dto

data class ExpenseRequest(
    val vehicleId: Long,
    val vehiclePlate: String,
    val category: String,
    val type: String,
    val date: String,
    val amount: Int,
    val km: Int? = null,
    val notes: String? = null
)