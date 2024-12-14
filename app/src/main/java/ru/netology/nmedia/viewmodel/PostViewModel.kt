package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorId = 0L,
    likedByMe = false,
    likes = 0,
    published = "",
    authorAvatar = "",
    isSaved = true,
    attachment = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository,
) : ViewModel() {

    val data: Flow<PagingData<Post>> =
        repository.posts

    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post>
        get() = _edited

    val noPhoto = PhotoModel()

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _onLikeError = SingleLiveEvent<Long>()
    val onLikeError: LiveData<Long>
        get() = _onLikeError

    private val _photo = MutableLiveData<PhotoModel>(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    fun save() {
        _edited.value?.let {
            viewModelScope.launch {
                try {
                    repository.save(it)
                    _dataState.value = FeedModelState()
                    _edited.value = empty
                    _photo.value = noPhoto
                    _postCreated.value = Unit

                } catch (e: Exception) {
                    _dataState.value = FeedModelState(onSaveError = true)
                }
            }
        }
    }

    fun edit(post: Post) {
        _edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (_edited.value?.content == text) {
            return
        }
        _edited.value = _edited.value?.copy(content = text)
    }

    fun clearEdited() {
        _edited.value = empty
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
