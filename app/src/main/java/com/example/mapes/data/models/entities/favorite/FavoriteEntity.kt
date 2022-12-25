package com.example.mapes.data.models.entities.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id_favorite:Int,
    val character_id:Int,
    val user_id:Int,
)
