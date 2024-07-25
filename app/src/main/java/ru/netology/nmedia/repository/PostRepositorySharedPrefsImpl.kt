package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositorySharedPrefsImpl(context: Context) : PostRepository {
    private val gson = Gson()
    private val prefs = context.getSharedPreferences("repo", Context.MODE_PRIVATE)
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val key = "posts"
    private var nextId: Long = 2
    private var posts = emptyList<Post>()
        private set(value) {
            field = value
            sync()
        }
    private val data = MutableLiveData(posts)

    init {
        prefs.getString(key, null)?.let { it ->
            posts = gson.fromJson(it, type)

            nextId = posts.maxOfOrNull { it.id }?.inc() ?: 2

            data.value = posts
        }
    }


    override fun getAll(): LiveData<List<Post>> = data


    override fun save(post: Post) {
        when (post.id) {

            1L -> posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "?",// todo
                    likedByMe = false,
                    published = "now"//todo
                )
            ) + posts

            0L -> posts = listOf(
                post.copy(
                    id = nextId++,
                    author = "me",// todo
                    likedByMe = false,
                    published = "now"//todo
                )
            ) + posts


            else -> posts = posts.map {
                if (it.id != post.id) it else it.copy(content = post.content)
            }
        }
        data.value = posts
    }


    override fun likeById(id: Long) {
        posts = posts.map {

            if (it.id != id) it else {
                when (it.likedByMe) {
                    true -> it.copy(
                        likedByMe = !it.likedByMe,
                        likes = it.likes - 1
                    )

                    else -> it.copy(
                        likedByMe = !it.likedByMe,
                        likes = it.likes + 1
                    )
                }
            }
        }
        data.value = posts
    }

    override fun shareById(id: Long) {
        posts = posts.map {
            if (it.id != id) it else it.copy(sharesCount = it.sharesCount + 1)
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        data.value = posts
    }


    private fun sync() {
        prefs.edit().putString(key, gson.toJson(posts)).apply()
    }
}
