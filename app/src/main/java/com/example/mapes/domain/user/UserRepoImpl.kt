package com.example.marvelapes.domain.user


import com.example.marvelapes.data.localdb.user.UserDao
import com.example.marvelapes.data.models.entities.user.UserEntity

class UserRepoImpl(private val dao : UserDao) : UserRepo {
    override suspend fun saveUser(usersEntity: UserEntity): Long = dao.saveUser(usersEntity)
    override suspend fun getOneUser(email: String): UserEntity = dao.getOneUser(email)
}