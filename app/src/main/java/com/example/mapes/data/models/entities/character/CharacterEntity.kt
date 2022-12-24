package com.example.mapes.data.models.entities.character

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id:Int,
    val name:String,
    val description:String,
    val modified:String,
    val image:String
)
