package com.example.mapes.data.localdb.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveFavorite(favoriteEntity: FavoriteEntity):Long

    @Query("SELECT * FROM favoriteentity as f JOIN characterentity as c ON f.character_id = c.id where user_id = :id_user")
    suspend fun getDbFavorites(id_user:Int): List<FavoriteAndCharacter>

    @Query("SELECT * FROM favoriteentity  where user_id = :id_user AND character_id = :id_character")
    suspend fun isLiked(id_user:Int,id_character:Int): List<FavoriteEntity>

    @Query("DELETE FROM favoriteentity where user_id = :id_user AND character_id = :id_character")
    suspend fun deleteFavorite(id_user:Int,id_character:Int): Int
}