package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.launch
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

        val intentHandler = intent?.getStringExtra(Intent.EXTRA_TEXT)
        if (intentHandler !== null) {
            viewModel.intentHandler()
            viewModel.changeContent(intentHandler.toString())
            viewModel.save()
        }


        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)// todo - need to change count after sharing done
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


        val newPostLauncher =
            registerForActivityResult(NewPostResultContract()) { result ->
                result ?: return@registerForActivityResult
                viewModel.changeContent(result)
                viewModel.save()
            }
        binding.fab.setOnClickListener {
            newPostLauncher.launch()

        }
        val editPostLauncher =
            registerForActivityResult(EditPostResultContract()) { result ->
                if (result == null) {
                    viewModel.empty()
                    return@registerForActivityResult
                }
                viewModel.changeContent(result)
                viewModel.save()
            }
        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {

                return@observe
            }
            editPostLauncher.launch(post.content)

        }


    }
}
