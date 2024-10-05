package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token

interface PostRepository {
    val posts: Flow<List<Post>>
    val postsWhole: Flow<List<Post>>

    fun getNewer(id: Int, size: Int): Flow<Int>
    suspend fun makeOld()
    suspend fun getAll()
    suspend fun likeById(id: Long)
    suspend fun save(post: Post)
    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)
    suspend fun removeById(id: Long)
    suspend fun disLikeById(id: Long)
    suspend fun upload(upload: MediaUpload): Media
    suspend fun signIn(login: String, password:String)

}
