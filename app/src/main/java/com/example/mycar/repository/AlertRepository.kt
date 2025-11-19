package com.example.mycar.repository

import com.example.mycar.network.RetrofitClient
import com.example.mycar.network.dto.AlertRequest
import com.example.mycar.network.dto.AlertResponse

class AlertRepository {

    private val api = RetrofitClient.apiAlerts

    suspend fun getAlerts(email: String): List<AlertResponse> =
        api.getAlerts(email)

    suspend fun addAlert(request: AlertRequest): AlertResponse =
        api.addAlert(request)

    suspend fun deleteAlert(id: Long) =
        api.deleteAlert(id)
}
