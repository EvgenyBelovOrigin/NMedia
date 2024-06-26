package ru.netology.nmedia.dao

import ru.netology.nmedia.dto.Post

interface PostDao {
    fun getAll(): List<Post>
    fun save(post: Post): Post
    fun shareById(id: Long)
    fun likeById(id: Long)
    fun removeById(id: Long)
}