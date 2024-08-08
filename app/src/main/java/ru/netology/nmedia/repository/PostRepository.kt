package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callback: GetAllCallback)
    fun likeById(id: Long): Post
    fun save(post: Post)
    fun removeById(id: Long)
    fun disLikeById(id: Long): Post

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(exceptiin: Exception)
    }
}
