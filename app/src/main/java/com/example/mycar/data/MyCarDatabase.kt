package com.example.mycar.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        AlertEntity::class,
        MaintenanceEntity::class     // <--- AGREGADO
    ],
    version = 4,
    exportSchema = false
)
abstract class MyCarDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun alertDao(): AlertDao
    abstract fun maintenanceDao(): MaintenanceDao   // <--- AGREGADO
}
