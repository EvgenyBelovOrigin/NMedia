package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likesCount: Long,
    val sharesCount: Long,
    val viewsCount: Long,
    val video: String?
)


