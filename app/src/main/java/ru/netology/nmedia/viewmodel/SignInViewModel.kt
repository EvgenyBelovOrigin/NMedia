package ru.netology.nmedia.viewmodel

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.RunTimeError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(
            context = application
        ).postDao(),
    )
    val _signedIn = SingleLiveEvent<Unit>()
    val signedIn: LiveData<Unit>
        get() = _signedIn
    val _notFoundException = SingleLiveEvent<Unit>()
    val notFoundException: LiveData<Unit>
        get() = _notFoundException
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception


    fun signIn(login: String, password: String) {
        viewModelScope.launch {
            try {
                repository.signIn(login, password)
                _signedIn.value = Unit

            } catch (
                e: RunTimeError,
            ) {
                _notFoundException.value = Unit
            }
            catch (e: Exception){
                _exception.value = Unit

            }
        }

    }
}