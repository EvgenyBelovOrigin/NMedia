package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    likedByMe = false,
    likes = 0,
    published = ""
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAll(object : PostRepository.GetCallback<List<Post>> {
            override fun onSuccess(value: List<Post>) {
                _data.postValue(FeedModel(posts = value, empty = value.isEmpty()))
            }

            override fun onError(exceptiin: Exception) {
                error()
            }
        })
    }

    fun save() {
        edited.value?.let {
            repository.save(it, object : PostRepository.GetCallback<Post> {
                override fun onSuccess(value: Post) {
                    _postCreated.postValue(Unit)
                }

                override fun onError(exceptiin: Exception) {
                    _postCreated.postValue(Unit)
                    error()
                }
            })
        }
        edited.value = empty
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
        if (!post.likedByMe) {
            repository.likeById(post.id, object : PostRepository.GetCallback<Post> {
                override fun onSuccess(value: Post) {
                    onSuccessLikeById(value, post)
                }

                override fun onError(exceptiin: Exception) {
                    error()
                }
            })
        } else {
            repository.disLikeById(post.id, object : PostRepository.GetCallback<Post> {
                override fun onSuccess(value: Post) {
                    onSuccessLikeById(value, post)
                }

                override fun onError(exceptiin: Exception) {
                    error()
                }
            })
        }
    }

    fun removeById(id: Long) {
        val old = _data.value?.posts.orEmpty()
        _data.postValue(
            _data.value?.copy(posts = _data.value?.posts.orEmpty()
                .filter { it.id != id }
            )
        )
        repository.removeById(id, object : PostRepository.GetCallback<Unit> {
            override fun onSuccess(value: Unit) {}
            override fun onError(exceptiin: Exception) {
                _data.postValue(_data.value?.copy(posts = old))
            }
        })
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

    fun error() {
        _data.postValue(FeedModel(error = true))
    }
}
