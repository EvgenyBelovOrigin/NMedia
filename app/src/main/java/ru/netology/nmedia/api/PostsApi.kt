package ru.netology.nmedia.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Token


private const val BASE_URL = "${BuildConfig.BASE_URL}/api/slow/"
//private const val BASE_URL = "${BuildConfig.BASE_URL}/api/"


private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}
private val okhttp = OkHttpClient.Builder()
    .addInterceptor(logging)
    .addInterceptor { chain ->
        AppAuth.getInstance().authState.value?.token?.let { token ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", token)
                .build()
            return@addInterceptor chain.proceed(newRequest)
        }
        chain.proceed(chain.request())
    }
    .build()
private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .client(okhttp)
    .build()

interface PostsApiService {
    @GET("posts")
    suspend fun getAll(): Response<List<Post>>

    @GET("posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): Response<List<Post>>

    @POST("posts")
    suspend fun save(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>

    @POST("posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun disLikeById(@Path("id") id: Long): Response<Post>

    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>


    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun signIn(
        @Field("login") login: String,
        @Field("pass") pass: String,
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun signUp(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String,
    ): Response<Token>


    @Multipart
    @POST("users/registration")
    suspend fun signUpWithAvatar(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part media: MultipartBody.Part,
    ): Response<Token>
}


object PostsApi {
    val service: PostsApiService by lazy {
        retrofit.create(PostsApiService::class.java)
    }
}