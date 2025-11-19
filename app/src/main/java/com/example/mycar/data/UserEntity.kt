package com.example.mycar.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,   // usamos el email como ID único
    val name: String,
    val lastName: String,
    val phone: String
)
