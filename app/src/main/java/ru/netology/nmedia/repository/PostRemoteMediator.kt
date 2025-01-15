package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import okio.IOException
import retrofit2.HttpException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator @Inject constructor(
    private val service: ApiService,
    private val dao: PostDao,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostEntity>): MediatorResult {

        try {
            val result = when (loadType) {
                LoadType.APPEND -> {
                    val id = state.lastItemOrNull()?.id ?: return MediatorResult.Success(false)
                    service.getBefore(id, state.config.pageSize)
                }

                LoadType.PREPEND -> {
                    val id = state.firstItemOrNull()?.id ?: return MediatorResult.Success(false)
                    service.getAfter(id, state.config.pageSize)
                }

                LoadType.REFRESH -> service.getLatest(state.config.pageSize)
            }
            if (!result.isSuccessful) {
                throw HttpException(result)
            }
            val data = result.body().orEmpty()

            dao.insert(data.map { PostEntity.fromDto(it, false) })

            return MediatorResult.Success(data.isEmpty())

        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }

    }
}