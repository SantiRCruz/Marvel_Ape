package com.example.mapes.data.localdb.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mapes.data.models.entities.character.CharacterEntity

@Dao
interface CharacterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCharacter(characterEntity: CharacterEntity):Long

    @Query("SELECT * FROM characterentity")
    suspend fun getDbCharacters(): List<CharacterEntity>
}