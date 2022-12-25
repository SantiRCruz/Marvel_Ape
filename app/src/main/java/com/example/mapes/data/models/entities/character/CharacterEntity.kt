package com.example.mapes.data.models.entities.character

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class CharacterEntity(
    @PrimaryKey(autoGenerate = false)
    val id:Int,
    val name:String,
    val description:String,
    val modified:String,
    val image:String
):Parcelable
