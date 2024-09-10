package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _onLikeError = SingleLiveEvent<Long>()
    val onLikeError: LiveData<Long>
        get() = _onLikeError

    private val _onDeleteError = SingleLiveEvent<Unit>()
    val onDeleteError: LiveData<Unit>
        get() = _onDeleteError

    private val _onSaveError = SingleLiveEvent<Unit>()
    val onSaveError: LiveData<Unit>
        get() = _onSaveError

    init {
        loadPosts()
    }

    fun loadPosts() {
        _dataState.value = FeedModelState(loading = true)

        viewModelScope.launch {

            try {
                repository.getAll()
                _dataState.value = FeedModelState()

            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }}

        fun save() {
            viewModelScope.launch {

                edited.value?.let {
                    repository.save(it)
                    // todo
                    _postCreated.value = Unit
                }
                edited.value = empty
            }
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

        fun likeById(post: Post) {
            viewModelScope.launch {

                if (!post.likedByMe) {
                    repository.likeById(post.id)
                    //todo
                } else {
                    repository.disLikeById(post.id)
                    //todo
                }
            }
        }

        fun removeById(id: Long) {
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
            viewModelScope.launch {
                repository.removeById(id)
                //todo
            }
        }

        fun onSuccessLikeById(value: Post, post: Post) {
            val postUpdated = value
            val newPosts = _data.value?.posts.orEmpty().map {
                if (it.id == post.id) {
                    postUpdated
                } else {
                    it
                }
            }
            _data.postValue(_data.value?.copy(posts = newPosts))
        }
    }
