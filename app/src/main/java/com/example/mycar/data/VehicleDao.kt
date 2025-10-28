package com.example.mycar.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VehicleDao {

    // Insertar un vehículo
    @Insert
    suspend fun insert(vehicle: VehicleEntity)

    //  Eliminar vehículo por patente (clave única)
    @Query("DELETE FROM vehicles WHERE plate = :plate")
    suspend fun deleteByPlate(plate: String)

    // Obtener todos los vehículos registrados
    @Query("SELECT * FROM vehicles")
    suspend fun getAll(): List<VehicleEntity>
}
