package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val author: String,
    val authorId:Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String?,
    val isSaved:Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)

data class Attachment(
    val url: String,
//    val description: String,
    val type: AttachmentType,
)
enum class AttachmentType {
    IMAGE
}

