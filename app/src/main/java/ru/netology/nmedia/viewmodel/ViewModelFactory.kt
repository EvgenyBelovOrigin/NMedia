package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class ViewModelFactory @Inject constructor(
//    private val repository: PostRepository,
//    private val appAuth: AppAuth,
) : ViewModel(){
//    @Suppress("UNCHECKED_CAST")
//    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
//        modelClass.isAssignableFrom(PostViewModel::class.java) -> {
//            PostViewModel(repository, appAuth) as T
//        }
//
//        modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
//            AuthViewModel(appAuth) as T
//        }
//
//        modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
//            SignInViewModel(repository) as T
//        }
//
//        modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
//            SignUpViewModel(repository) as T
//        }
//
//        else -> error("Unknown class : $modelClass")
//    }


}