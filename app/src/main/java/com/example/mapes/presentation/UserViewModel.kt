package com.example.mapes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marvelapes.data.models.entities.user.UserEntity
import com.example.marvelapes.domain.user.UserRepo
import com.example.mapes.core.Result
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class UserViewModel(private val repo : UserRepo) :ViewModel(){
    fun saveUser(usersEntity: UserEntity): StateFlow<Result<Long>> = flow {
        kotlin.runCatching {
            repo.saveUser(usersEntity)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    fun getOneUser(email: String): StateFlow<Result<UserEntity>> = flow {
        kotlin.runCatching {
            repo.getOneUser(email)
        }.onSuccess {
            emit(Result.Success(it))
        }.onFailure {
            emit(Result.Failure(Exception(it.message)))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

}
class UserViewModelFactory(private val repo : UserRepo): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UserRepo::class.java).newInstance(repo)
    }
}