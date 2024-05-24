package ru.netology.nmedia.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.tools.Tools
import ru.netology.nmedia.viemodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val tools = Tools()
        val viewModel: PostViewModel by viewModels()
        viewModel.data.observe(this) { posts ->
            posts.map { post ->
                CardPostBinding.inflate(layoutInflater, binding.container, true).apply {
                    tvAuthor.text = post.author
                    tvPublished.text = post.published
                    tvContent.text = post.content
                    tvLikesCount.text = tools.getShortCountTypeValue(post.likesCount)
                    tvSharesCount.text = tools.getShortCountTypeValue(post.sharesCount)
                    tvViewsCount.text = tools.getShortCountTypeValue(post.viewsCount)
                    ibLikes.setImageResource(if (post.likedByMe) R.drawable.clicked_likes else R.drawable.likes)
                    ibLikes.setOnClickListener { viewModel.likeById(post.id) }
                    ibShares.setOnClickListener { viewModel.shareById(post.id) }
                }.root

            }
//            with(binding) {
//                tvAuthor.text = post.author
//                tvPublished.text = post.published
//                tvContent.text = post.content
//                tvLikesCount.text = tools.getShortCountTypeValue(post.likesCount)
//                tvSharesCount.text = tools.getShortCountTypeValue(post.sharesCount)
//                tvViewsCount.text = tools.getShortCountTypeValue(post.viewsCount)
//                if (post.likedByMe) ibLikes.setImageResource(R.drawable.clicked_likes)
//                else ibLikes.setImageResource(
//                    R.drawable.likes
//                )
//            }


        }
//        with(binding) {
//            ibLikes.setOnClickListener {
//                viewModel.like()
//            }
//            ibShares.setOnClickListener {
//                viewModel.share()
//            }
//        }


    }
}