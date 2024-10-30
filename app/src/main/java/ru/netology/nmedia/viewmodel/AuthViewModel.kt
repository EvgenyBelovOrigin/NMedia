package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import ru.netology.nmedia.auth.AppAuth

class AuthViewModel(
    private val appAuth: AppAuth
) : ViewModel() {
    val data = appAuth.authState.asLiveData()

    val authenticated: Boolean
        get() = appAuth.authState.value?.id != 0L

}