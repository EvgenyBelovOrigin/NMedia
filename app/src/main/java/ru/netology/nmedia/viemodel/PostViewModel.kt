package ru.netology.nmedia.viemodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likesCount = 0,
    sharesCount = 0,
    viewsCount = 0,
    video = null
)
private val intentHandler = Post(
    id = 1,
    content = "",
    author = "",
    likedByMe = false,
    published = "",
    likesCount = 0,
    sharesCount = 0,
    viewsCount = 0,
    video = null
)

class PostViewModel : ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoryImpl()
    val data = repository.getAll()
    val edited = MutableLiveData(empty)
    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun empty() {
        edited.value = empty
    }

    fun intentHandler() {
        edited.value = intentHandler
    }


    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }


    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun removeById(id: Long) = repository.removeById(id)
}