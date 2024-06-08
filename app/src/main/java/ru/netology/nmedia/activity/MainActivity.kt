package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.tools.AndroidUtils
import ru.netology.nmedia.viemodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()


        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }
        }
        )

        binding.list.adapter = adapter

        viewModel.data.observe(this) { posts ->
            val newPost = posts.size > adapter.currentList.size
            adapter.submitList(posts)
            if (newPost) {
                binding.list.smoothScrollToPosition(0)
            }

        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)

            }
            with(binding) {
                groupEditPost.visibility = View.GONE
                editedPostText.setText("")
            }

        }
        binding.close.setOnClickListener {
            viewModel.empty()
            with(binding.content) {
                setText("")
                clearFocus()
                AndroidUtils.hideKeyboard(this)
            }

            with(binding) {
                groupEditPost.visibility = View.GONE
                editedPostText.setText("")
            }
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {

                return@observe
            }
            binding.groupEditPost.visibility = View.VISIBLE
            binding.editedPostText.setText(post.content)
            with(binding.content) {
                requestFocus()
                setText(post.content)
                setSelection(post.content.length)

            }
        }

//        viewModel.isAdded.observe(this) { isAdded ->
//            if (isAdded) {
//                binding.list.smoothScrollToPosition(0)
//            }
//
//        }


    }
}
