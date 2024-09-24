package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvataatar: String?,
    val isSaved: Boolean,
    val isNewPost: Boolean,

    @Embedded
    val attachment: Attachment?,

    ) {
    fun toDto() = Post(
        id,
        author,
        content,
        published,
        likedByMe,
        likes,
        authorAvataatar,
        isSaved,
        attachment?.url.isNullOrBlank(),
        attachment = attachment
    )

    companion object {
        fun fromDto(dto: Post, isNewPost: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.authorAvatar,
                true,
                isNewPost,
                dto.attachment
            )

    }
}



