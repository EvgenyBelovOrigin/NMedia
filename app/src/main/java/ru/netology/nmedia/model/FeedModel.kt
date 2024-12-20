package ru.netology.nmedia.model

import ru.netology.nmedia.dto.Post

data class FeedModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)

data class FeedModelState(
    val refreshing: Boolean = false,
    val loading: Boolean = false,
    val error: Boolean = false,
    val onDeleteError: Boolean = false,
    val onSaveError: Boolean = false
)
