package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String?,
    val isSaved:Boolean,
    val attachment: Attach?
)

data class Attach(val url: String, val description: String, val type: String)

