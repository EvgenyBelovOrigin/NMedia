package ru.netology.nmedia.entity

import androidx.paging.PagingData
import androidx.paging.map
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
    val authorId: Long,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val authorAvatatar: String?,
    val isSaved: Boolean,
    val isNewPost: Boolean,

    @Embedded
    val attachment: Attachment?,

    ) {
    fun toDto() = Post(
        id,
        author,
        authorId,
        content,
        published,
        likedByMe,
        likes,
        authorAvatatar,
        isSaved,
        attachment = attachment
    )

    companion object {
        fun fromDto(dto: Post, isNewPost: Boolean) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.content,
                dto.published,
                dto.likedByMe,
                dto.likes,
                dto.authorAvatar,
                true,
                isNewPost,
                dto.attachment
            )

        fun toDtoPagingData(entity: PagingData<PostEntity>): PagingData<Post> =
            entity.map {
                it.toDto()
            }


    }
}



