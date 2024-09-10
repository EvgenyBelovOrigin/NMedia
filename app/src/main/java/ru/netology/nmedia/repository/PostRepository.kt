package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val posts: LiveData<List<Post>>
    suspend fun getAll()
    suspend fun likeById(id: Long): Post
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long)
    suspend fun disLikeById(id: Long): Post
}
