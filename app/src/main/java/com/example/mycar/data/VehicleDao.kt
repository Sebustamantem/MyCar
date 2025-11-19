package com.example.mycar.data

import androidx.room.*

@Dao
interface VehicleDao {

    @Query("SELECT * FROM vehicles WHERE ownerEmail = :email")
    fun getAllByEmail(email: String): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vehicle: VehicleEntity)

    @Query("DELETE FROM vehicles WHERE plate = :plate")
    fun deleteByPlate(plate: String)

    @Query("DELETE FROM vehicles")
    fun clear()
}
