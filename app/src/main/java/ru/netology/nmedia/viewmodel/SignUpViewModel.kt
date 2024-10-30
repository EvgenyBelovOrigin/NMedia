package ru.netology.nmedia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File

class SignUpViewModel(
    private val repository: PostRepository,
) : ViewModel() {
    val noAvatar = PhotoModel()
    val _signedUp = SingleLiveEvent<Unit>()
    val signedUp: LiveData<Unit>
        get() = _signedUp
    val _wrongPassConfirm = SingleLiveEvent<Unit>()
    val wrongPassConfirm: LiveData<Unit>
        get() = _wrongPassConfirm
    val _exception = SingleLiveEvent<Unit>()
    val exception: LiveData<Unit>
        get() = _exception

    private val _avatar = MutableLiveData<PhotoModel>(noAvatar)
    val avatar: LiveData<PhotoModel>
        get() = _avatar


    fun signUp(login: String, password: String, passConfirm: String, name: String) {
        if (password != passConfirm) {
            _wrongPassConfirm.value = Unit
        } else {

            viewModelScope.launch {
                try {
                    _avatar.value?.file?.let { file ->
                        repository.signUpWithAvatar(login, password, name, MediaUpload(file))
                    } ?: repository.signUp(login, password, name)

                    _signedUp.value = Unit

                } catch (e: Exception) {
                    _exception.value = Unit

                }
            }
        }


    }

    fun updateAvatar(uri: Uri, file: File) {
        _avatar.value = PhotoModel(uri, file)
    }

    fun clearAvatar() {
        _avatar.value = noAvatar
    }
}