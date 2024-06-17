package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.Post

class PostRepositoryFileImpl(private val context: Context) : PostRepository {
    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val fileName = "posts.json"
    private var nextId: Long = 2
    private var posts = emptyList<Post>()
        private set(value) {
            field = value
            sync()
        }
    private val data = MutableLiveData(posts)

    init {
        val file = context.filesDir.resolve(fileName)
        if (file.exists()) {
            context.openFileInput(fileName).bufferedReader().use {
                posts = gson.fromJson(it, type)
                nextId = posts.maxOfOrNull { it.id }?.inc() ?: 2
                data.value = posts
            }
        } else {
            sync()
        }
    }


    override fun getAll(): LiveData<List<Post>> = data


    override fun save(post: Post) {
        when (post.id) {

            0L -> posts = listOf(
                post.copy(
                    id = nextId++,
                    author = context.getString(R.string.user_name),
                    likedByMe = false,
                    published = "now"// todo
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
                        likesCount = it.likesCount - 1
                    )

                    else -> it.copy(
                        likedByMe = !it.likedByMe,
                        likesCount = it.likesCount + 1
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
        context.openFileOutput(fileName, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }
}
