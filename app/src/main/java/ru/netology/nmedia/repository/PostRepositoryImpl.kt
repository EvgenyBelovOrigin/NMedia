package ru.netology.nmedia.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.dto.Post

class PostRepositoryImpl : PostRepository {

    override fun getAll(callback: PostRepository.GetCallback<List<Post>>) {
        PostsApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))
                    }

                }

                override fun onFailure(call: Call<List<Post>>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }

    override fun save(post: Post, callback: PostRepository.GetCallback<Post>) {
        PostsApi.service.save(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))
                    }

                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }

    override fun removeById(id: Long, callback: PostRepository.GetCallback<Unit>) {
        PostsApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))

                    }
                }

                override fun onFailure(call: Call<Unit>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }

    override fun disLikeById(id: Long, callback: PostRepository.GetCallback<Post>) {
        PostsApi.service.disLikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))
                    }
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }

    override fun likeById(id: Long, callback: PostRepository.GetCallback<Post>) {
        PostsApi.service.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(call: Call<Post>, response: Response<Post>) {
                    if (response.isSuccessful) {
                        callback.onSuccess(
                            response.body() ?: throw RuntimeException("body is null")
                        )
                    } else {
                        callback.onError(RuntimeException("Bad code received"))

                    }
                }

                override fun onFailure(call: Call<Post>, e: Throwable) {
                    callback.onError(e)
                }
            })
    }
}


