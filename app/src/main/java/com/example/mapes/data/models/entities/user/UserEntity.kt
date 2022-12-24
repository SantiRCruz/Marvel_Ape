package com.example.marvelapes.data.models.entities.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id_user: Int,
    val names: String,
    val identification: String,
    @ColumnInfo(name = "email")
    val email: String,
    val phone: String,
    val password: String,
    val photo: String,
)