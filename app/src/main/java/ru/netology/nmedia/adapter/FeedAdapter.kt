package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.CardTimeSeparatorBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.TimeSeparator
import ru.netology.nmedia.dto.TimeSeparatorValues
import ru.netology.nmedia.util.loadAttachmentView
import ru.netology.nmedia.util.loadAvatar


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onShowAttachmentViewFullScreen(post: Post) {}
}

class FeedAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position.coerceAtMost(itemCount-1))) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimeSeparator -> R.layout.card_time_separator
            null -> throw IllegalArgumentException("unknown item type")
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onInteractionListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }

            R.layout.card_time_separator -> {
                val binding =
                    CardTimeSeparatorBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                TimeSeparatorViewHolder(binding)
            }

            else -> throw IllegalArgumentException("unknown item type")
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TimeSeparator -> (holder as? TimeSeparatorViewHolder)?.bind(item)
            null -> throw IllegalArgumentException("unknown item type")

        }

    }
}

class AdViewHolder(
    private val binding: CardAdBinding,

    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.loadAttachmentView("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class TimeSeparatorViewHolder(
    private val binding: CardTimeSeparatorBinding,

    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(timeSeparator: TimeSeparator) {
        binding.timeSeparator.setText(
            when (timeSeparator.value) {
                TimeSeparatorValues.TODAY -> R.string.today
                TimeSeparatorValues.YESTERDAY -> R.string.yesterday
                TimeSeparatorValues.A_WEEK_AGO -> R.string.a_week_ago
            }
        )
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    private val baseUrl = BuildConfig.BASE_URL

    fun bind(post: Post) {
        binding.apply {
            avatar.loadAvatar(post.authorAvatar?.let { "$baseUrl/avatars/$it" })
            attachmentImage.isVisible = !post.attachment?.url.isNullOrBlank()
            attachmentImage.loadAttachmentView("$baseUrl/media/${post.attachment?.url}")
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"
            saving.isVisible = !post.isSaved

            menu.isVisible = post.ownedByMe

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            attachmentImage.setOnClickListener {
                onInteractionListener.onShowAttachmentViewFullScreen(post)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {

        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}
