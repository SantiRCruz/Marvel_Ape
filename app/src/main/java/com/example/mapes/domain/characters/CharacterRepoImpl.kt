package com.example.marvelapes.domain.characters

import com.example.mapes.data.localdb.character.CharacterDao
import com.example.mapes.data.models.entities.character.CharacterEntity
import com.example.marvelapes.data.models.web.BaseResponse
import com.example.marvelapes.data.rest.WebService

class CharacterRepoImpl(private val rest:WebService,private val dao : CharacterDao): CharacterRepo {
    //web
    override suspend fun getCharacters(): BaseResponse = rest.getCharacters()

    //local
    override suspend fun saveCharacter(characterEntity: CharacterEntity): Long = dao.saveCharacter(characterEntity)
    override suspend fun getDbCharacters(): List<CharacterEntity> = dao.getDbCharacters()

}