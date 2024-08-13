package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(callback: GetCallback<List<Post>>)
    fun likeById(id: Long, callback: GetCallback<Post>)
    fun save(post: Post, callback: GetCallback<Post>)
    fun removeById(id: Long, callback: GetCallback<Unit>)
    fun disLikeById(id: Long, callback: GetCallback<Post>)

    interface GetCallback<T> {
        fun onSuccess(value: T)
        fun onError(e: Throwable)
    }
}
