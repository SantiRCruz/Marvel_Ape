package com.example.marvelapes.domain.characters

import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.marvelapes.data.models.web.BaseResponse

interface CharacterRepo {
    //web
    suspend fun getCharacters(): BaseResponse

    //local db
    suspend fun saveCharacter(characterEntity: CharacterEntity):Long
    suspend fun getDbCharacters(): List<CharacterEntity>

}