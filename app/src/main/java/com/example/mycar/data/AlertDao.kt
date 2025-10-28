package com.example.mycar.data

import androidx.room.*

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alert: AlertEntity)

    @Query("SELECT * FROM alerts ORDER BY id DESC")
    suspend fun getAll(): List<AlertEntity>

    @Query("DELETE FROM alerts WHERE id = :alertId")
    suspend fun deleteById(alertId: Int)
}
