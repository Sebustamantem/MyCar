package com.example.mycar.data

import androidx.room.*

@Dao
interface MaintenanceDao {
    @Insert suspend fun insert(record: MaintenanceEntity)
    @Delete suspend fun delete(record: MaintenanceEntity)
    @Query("SELECT * FROM maintenance") suspend fun getAll(): List<MaintenanceEntity>
}
