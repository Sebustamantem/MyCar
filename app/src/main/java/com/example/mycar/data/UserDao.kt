package com.example.mycar.data

import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUser(email: String): UserEntity?

    @Query("DELETE FROM users")
    fun clear()
}
