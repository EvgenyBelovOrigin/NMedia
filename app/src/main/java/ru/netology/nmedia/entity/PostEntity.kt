package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
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
    val attachment: Boolean,

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
        attachment,
        null
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.authorAvatar,
                true,
                !dto.attachment?.url.isNullOrBlank()
            )

    }
}
@Entity
data class AttachEntity
    (
    @PrimaryKey
    val idPost: Long,
    val url: String,
    val description: String,
    val type: String
)



