package com.example.marvelapes.domain.user

import com.example.marvelapes.data.models.entities.user.UserEntity


interface UserRepo {
    suspend fun saveUser(usersEntity: UserEntity):Long
    suspend fun getOneUser(email:String):UserEntity
}