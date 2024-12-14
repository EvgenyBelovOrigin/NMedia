package ru.netology.nmedia.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.AttachmentType
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.MediaUpload
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.AppError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.RunTimeError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val dao: PostDao,
) : PostRepository {
    override val posts = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false)
    ) {
        dao.getAllPaging()
    }.flow.map { PostEntity.toDtoPagingData(it) }

    override suspend fun makeOld() {
        dao.makeOld()
    }

    override suspend fun likeById(id: Long) {
        try {
            dao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun save(post: Post) {
        try {
            dao.insert(PostEntity.fromDto(post, false).copy(isSaved = false))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }


    override suspend fun removeById(id: Long) {
        try {
            dao.removeById(id)
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun disLikeById(id: Long) {
        try {
            dao.likeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}


