package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity

class PostRepositoryImpl(
    private val dao: PostDao,
) : PostRepository {
    override val posts: LiveData<List<Post>> = dao.getAll().map {
        it.map(PostEntity::toDto)
    }

    override suspend fun getAll() {
        val posts = PostsApi.service.getAll()
        dao.insert(posts.map(PostEntity::fromDto))
    }

    override suspend fun likeById(id: Long): Post {
        TODO("Not yet implemented")
    }

    override suspend fun save(post: Post): Post {
        TODO("Not yet implemented")
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun disLikeById(id: Long): Post {
        TODO("Not yet implemented")
    }

}


