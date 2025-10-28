package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,           // Nombre
    val lastName: String,       // Apellido
    val email: String,
    val password: String,
    val phone: String
)
