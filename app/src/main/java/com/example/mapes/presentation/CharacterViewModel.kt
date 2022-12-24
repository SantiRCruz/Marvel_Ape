package com.example.marvelapes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marvelapes.data.models.web.BaseResponse
import com.example.marvelapes.domain.characters.CharacterRepo
import com.example.marvelapes.core.Result
import com.example.mapes.data.models.entities.character.CharacterEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class CharacterViewModel(private val repo : CharacterRepo): ViewModel()  {

    //local
    fun saveCharacter(characterEntity: CharacterEntity): StateFlow<Result<Long>> = flow{
        kotlin.runCatching {
            repo.saveCharacter(characterEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbCharacters(): StateFlow<Result<List<CharacterEntity>>> = flow{
        kotlin.runCatching {
            repo.getDbCharacters()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    //web
    fun getCharacters(): StateFlow<Result<BaseResponse>> = flow{
        kotlin.runCatching {
            repo.getCharacters()
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())
}
class CharacterViewModelFactory(private val repo : CharacterRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(CharacterRepo::class.java).newInstance(repo)

    }
}
