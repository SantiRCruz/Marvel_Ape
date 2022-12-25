package com.example.mapes.domain.favorite

import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter

interface FavoriteRepo {
    suspend fun saveFavorite(favoriteEntity: FavoriteEntity):Long
    suspend fun getDbFavorites(id_user:Int): List<FavoriteAndCharacter>
    suspend fun isLiked(id_user:Int,id_character:Int): List<FavoriteEntity>
    suspend fun deleteFavorite(id_user:Int,id_character:Int): Int
}