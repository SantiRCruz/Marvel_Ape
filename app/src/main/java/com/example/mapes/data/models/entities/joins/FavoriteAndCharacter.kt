package com.example.mapes.data.models.entities.joins

data class FavoriteAndCharacter(
    val id_favorite:Int,
    val character_id:Int,
    val user_id:Int,
    val id:Int,
    val name:String,
    val description:String,
    val modified:String,
    val image:String
)
