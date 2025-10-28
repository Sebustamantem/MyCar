package com.example.mycar.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        MaintenanceEntity::class,
        AlertEntity::class
    ],
    version = 5
)
abstract class MyCarDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun alertDao(): AlertDao
}
