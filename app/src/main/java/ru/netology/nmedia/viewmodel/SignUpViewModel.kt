package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.RunTimeError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(
            context = application
        ).postDao(),
    )
    val _signedUp = SingleLiveEvent<Unit>()
    val signedUp: LiveData<Unit>
        get() = _signedUp
    val _wrongPassConfirm = SingleLiveEvent<Unit>()
    val wrongPassConfirm: LiveData<Unit>
        get() = _wrongPassConfirm
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception


    fun signUp(login: String, password: String, passConfirm: String, name: String) {
        if (password != passConfirm) {
            _wrongPassConfirm.value = Unit
        } else {

            viewModelScope.launch {
                try {
                    repository.signUp(login, password, name)
                    _signedUp.value = Unit

                } catch (e: Exception) {
                    _exception.value = Unit

                }
            }
        }


    }
}