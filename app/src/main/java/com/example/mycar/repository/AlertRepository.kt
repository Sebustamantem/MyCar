package com.example.mycar.repository

import com.example.mycar.network.RetrofitInstance
import com.example.mycar.network.dto.AlertRequest

class AlertRepository {

    private val api = RetrofitInstance.api

    suspend fun getAlerts(email: String) =
        api.getAlerts(email)

    suspend fun addAlert(req: AlertRequest) =
        api.addAlert(req)

    suspend fun deleteAlert(id: Long) =
        api.deleteAlert(id)
}
