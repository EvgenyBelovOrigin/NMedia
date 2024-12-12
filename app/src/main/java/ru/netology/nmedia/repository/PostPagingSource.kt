package ru.netology.nmedia.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import okio.IOException
import retrofit2.HttpException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import javax.inject.Inject

class PostPagingSource @Inject constructor(
//    private val service: ApiService,
    private val dao: PostDao,
) : PagingSource<Long, Post>() {
    override fun getRefreshKey(state: PagingState<Long, Post>): Long? = null

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, Post> {
        try {
            val result = when (params) {
                is LoadParams.Append -> {
//                    service.getBefore(id = params.key, count = params.loadSize)
                    dao.getLatest(loadSize = params.loadSize)

                }

                is LoadParams.Prepend -> return LoadResult.Page(
                    data = emptyList(),
                    prevKey = params.key,
                    nextKey = null
                )

                is LoadParams.Refresh -> dao.getLatest(params.loadSize)
            }
//            if (!result.isSuccessful) {
//                throw HttpException(result)
//            }
//            val data = result.body().orEmpty()
//            val data = result
            return LoadResult.Page(
                data = result.map { PostEntity.toDto(it) },
                prevKey = params.key,
                nextKey = result.lastOrNull()?.id
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        }
    }
}