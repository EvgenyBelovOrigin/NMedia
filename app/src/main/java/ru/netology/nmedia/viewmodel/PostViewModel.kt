package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
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
    isSaved = true,
    attachmentIsEnable = false,
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao()
    )


    val data: LiveData<FeedModel> = repository.posts.map {
        FeedModel(it, it.isEmpty())
    }.catch {
        it.printStackTrace()
    }.asLiveData(Dispatchers.Default)


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
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                    edited.value = empty

                } catch (e: Exception) {
                    _dataState.value = FeedModelState(onSaveError = true)
                }
            }
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
            try {
                if (!post.likedByMe) {
                    repository.likeById(post.id)
                } else {
                    repository.disLikeById(post.id)
                }
            } catch (e: Exception) {
                _onLikeError.value = post.id
            }
        }
    }

    fun refresh() {
        _dataState.value = FeedModelState(refreshing = true)

        viewModelScope.launch {
            try {
                repository.getAll()
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }


    fun removeById(id: Long) {

        viewModelScope.launch {
            try {
                repository.removeById(id)
            } catch (e: Exception) {
                _dataState.value = FeedModelState(onDeleteError = true)
            }
        }
    }

}
