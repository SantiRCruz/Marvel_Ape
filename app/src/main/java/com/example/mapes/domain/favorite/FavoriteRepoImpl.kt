package com.example.mapes.domain.favorite

import com.example.mapes.data.localdb.favorite.FavoriteDao
import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter

class FavoriteRepoImpl(private val dao : FavoriteDao):FavoriteRepo {
    override suspend fun saveFavorite(favoriteEntity: FavoriteEntity): Long = dao.saveFavorite(favoriteEntity)
    override suspend fun getDbFavorites(id_user: Int): List<FavoriteAndCharacter> = dao.getDbFavorites(id_user)
    override suspend fun isLiked(id_user: Int, id_character: Int): List<FavoriteEntity> = dao.isLiked(id_user, id_character)
    override suspend fun deleteFavorite(id_user: Int, id_character: Int): Int = dao.deleteFavorite(id_user, id_character)
}