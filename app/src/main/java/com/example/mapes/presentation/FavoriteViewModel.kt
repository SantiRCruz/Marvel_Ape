package com.example.mapes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mapes.core.Result
import com.example.mapes.data.models.entities.favorite.FavoriteEntity
import com.example.mapes.data.models.entities.joins.FavoriteAndCharacter
import com.example.mapes.domain.favorite.FavoriteRepo
import com.example.marvelapes.data.models.entities.user.UserEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class FavoriteViewModel (private val repo : FavoriteRepo) : ViewModel(){
    fun saveFavorite(favoriteEntity: FavoriteEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveFavorite(favoriteEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getDbFavorites(id_user: Int): StateFlow<Result<List<FavoriteAndCharacter>>> = flow {
        kotlin.runCatching {
            repo.getDbFavorites(id_user)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun isLiked(id_user: Int, id_character: Int): StateFlow<Result<List<FavoriteEntity>>> = flow {
        kotlin.runCatching {
            repo.isLiked(id_user,id_character)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun deleteFavorite(id_user: Int, id_character: Int): StateFlow<Result<Int>> = flow {
        kotlin.runCatching {
            repo.deleteFavorite(id_user,id_character)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class FavoriteViewModelFactory(private val repo : FavoriteRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(FavoriteRepo::class.java).newInstance(repo)
    }
}