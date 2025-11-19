package com.example.mycar.data

import androidx.room.*

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts ORDER BY id DESC")
    fun getAll(): List<AlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alert: AlertEntity)

    @Query("DELETE FROM alerts WHERE id = :alertId")
    fun deleteById(alertId: Long)

    @Query("DELETE FROM alerts")
    fun clear()
}
