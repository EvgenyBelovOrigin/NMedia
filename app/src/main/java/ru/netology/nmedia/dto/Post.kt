package ru.netology.nmedia.dto

sealed interface FeedItem {
    val id: Long
}

data class Post(
    override val id: Long,
    val author: String,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatar: String?,
    val isSaved: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
) : FeedItem

data class Ad(
    override val id: Long,
    val image: String,
) : FeedItem

data class TimeSeparator(
    val value: TimeSeparatorValues,
) : FeedItem {
    override val id: Long = value.ordinal.toLong()

}

enum class TimeSeparatorValues {
    TODAY,
    YESTERDAY,
    A_WEEK_AGO,
}

data class Attachment(
    val url: String,
    val type: AttachmentType,
)

enum class AttachmentType {
    IMAGE
}

