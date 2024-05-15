package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val post = Post(
            id = 1,
            author = "Нетология. Университет интернет-профессий будущего",
            content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            published = "7 мая в 12:23",
            likedByMe = false,
            likesCount = 999,
            sharesCount = 10999,
            viewsCount = 9900

        )
        with(binding) {
            tvAuthor.text = post.author
            tvPublished.text = post.published
            tvContent.text = post.content
            tvLikesCount.text = post.getShortCountTypeValue(post.likesCount)
            tvSharesCount.text = post.getShortCountTypeValue(post.sharesCount)
            tvViewsCount.text = post.getShortCountTypeValue(post.viewsCount)
            if (post.likedByMe) {
                ibLikes.setImageResource(R.drawable.clicked_likes)
            }
            ibLikes.setOnClickListener {
                post.likedByMe = !post.likedByMe
                ibLikes.setImageResource(
                    if (post.likedByMe) R.drawable.clicked_likes else R.drawable.likes
                )
                if (post.likedByMe) post.likesCount++ else post.likesCount--
                tvLikesCount.text = post.getShortCountTypeValue(post.likesCount)
                println("like")
            }
            ibShares.setOnClickListener {
                post.sharesCount++
                tvSharesCount.text = post.getShortCountTypeValue(post.sharesCount)
            }
            root.setOnClickListener {
                println("root")
            }
            ivAvatar.setOnClickListener {
                println("avatar")
            }
        }


    }
}