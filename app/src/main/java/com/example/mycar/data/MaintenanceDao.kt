package com.example.mycar.data

import androidx.room.*

@Dao
interface MaintenanceDao {

    @Query("SELECT * FROM maintenance")
    fun getAll(): List<MaintenanceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: MaintenanceEntity)

    @Query("DELETE FROM maintenance WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM maintenance")
    fun clear()
}
