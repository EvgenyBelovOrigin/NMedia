package ru.netology.nmedia.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import java.io.File
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
    private val appAuth: AppAuth,
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class) // new
//    val data: LiveData<FeedModel> = appAuth.authState.flatMapLatest { token ->
//        repository.posts.map {
//            FeedModel(it.map { post ->
//                post.copy(ownedByMe = post.authorId == token?.id)
//
//            }, it.isEmpty())
//        }.catch {
//            it.printStackTrace()
//        }
//    }.asLiveData(Dispatchers.Default)


    val data: Flow<PagingData<Post>> = appAuth.authState
        .flatMapLatest { token ->
            repository.posts.map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == token?.id)
                }
            }
        }.flowOn(Dispatchers.Default)

//    private val dataWhole: LiveData<FeedModel> = repository.postsWhole.map {
//        FeedModel(it, it.isEmpty())
//    }.catch {
//        it.printStackTrace()
//    }.asLiveData(Dispatchers.Default)


    private val _dataState = MutableLiveData(FeedModelState())
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    val edited = MutableLiveData(empty)
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

    private val _requestSignIn = SingleLiveEvent<Unit>()
    val requestSignIn: LiveData<Unit>
        get() = _requestSignIn


    init {
        loadPosts()
    }

//    val newPostsCount: LiveData<Int> = dataWhole.switchMap { feedModel ->
//        repository.getNewer(feedModel.posts.firstOrNull()?.id?.toInt() ?: 0, feedModel.posts.size)
//            .asLiveData(Dispatchers.Default, 1_000)
//    }

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
            viewModelScope.launch {
                try {
                    _photo.value?.file?.let { file ->
                        repository.saveWithAttachment(it, MediaUpload(file))
                    } ?: repository.save(it)

                    _dataState.value = FeedModelState()
                    edited.value = empty
                    _photo.value = noPhoto
                    _postCreated.value = Unit

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
        if (appAuth.authState.value?.id == 0L) {
            _requestSignIn.value = Unit
        } else {
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

    fun makeOld() {
        viewModelScope.launch {
            try {
                repository.makeOld()

            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }
    }

    fun clearPhoto() {
        _photo.value = noPhoto
    }

    fun updatePhoto(uri: Uri, file: File) {
        _photo.value = PhotoModel(uri, file)
    }
}
