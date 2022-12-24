package com.example.marvelapes.data.localdb.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marvelapes.data.models.entities.user.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun saveUser(usersEntity: UserEntity):Long

    @Query("SELECT * FROM userentity WHERE email = :email")
    suspend fun getOneUser(email:String):UserEntity
}