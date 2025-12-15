package com.example.mycar.repository

import com.example.mycar.network.ApiClient
import com.example.mycar.network.dto.ExpenseRequest
import com.example.mycar.network.dto.ExpenseResponse

class ExpenseRepository {

    private val api = ApiClient.expenseService

    suspend fun listByVehicle(vehicleId: Long): List<ExpenseResponse> {
        val response = api.listByVehicle(vehicleId)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            throw Exception("Error ${response.code()} al listar gastos")
        }
    }

    suspend fun create(req: ExpenseRequest): ExpenseResponse {
        val response = api.create(req)
        return response.body()
            ?: throw Exception("Error ${response.code()} al crear gasto")
    }

    suspend fun update(id: Long, req: ExpenseRequest): ExpenseResponse {
        val response = api.update(id, req)
        return response.body()
            ?: throw Exception("Error ${response.code()} al actualizar gasto")
    }

    suspend fun delete(id: Long): Boolean {
        val response = api.delete(id)
        return response.isSuccessful // 204 = true
    }
}
