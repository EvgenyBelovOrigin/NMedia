package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentOpenPostBinding
import ru.netology.nmedia.tools.StringArg
import ru.netology.nmedia.tools.Tools
import ru.netology.nmedia.viemodel.PostViewModel

class OpenPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOpenPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by activityViewModels()

        val openPostId = arguments?.textArg?.toLong()
        val tools = Tools()

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            if (posts.firstOrNull { it.id == openPostId } == null) {
                findNavController().navigateUp()
                return@observe
            }

            posts.first { it.id == openPostId }.let { post ->
                with(binding.openPost) {
                    tvAuthor.text = post.author
                    tvPublished.text = post.published
                    tvContent.text = post.content
                    ibShares.text = tools.getShortCountTypeValue(post.sharesCount)
                    tvViewsCount.text = tools.getShortCountTypeValue(post.viewsCount)
                    ibLikes.isChecked = post.likedByMe
                    ibLikes.text = tools.getShortCountTypeValue(post.likesCount)
                    ibLikes.setOnClickListener {
                        viewModel.likeById(post.id)
                    }
                    ibShares.setOnClickListener {
                        val intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, post.content)
                            type = "text/plain"
                        }

                        val shareIntent =
                            Intent.createChooser(intent, getString(R.string.chooser_share_post))
                        startActivity(shareIntent)
                        viewModel.shareById(post.id)
                    }


                    ibMenu.setOnClickListener {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.options_post)
                            setOnMenuItemClickListener { item ->
                                when (item.itemId) {
                                    R.id.remove -> {
                                        viewModel.removeById(post.id)
                                        true
                                    }

                                    R.id.edit -> {
                                        viewModel.edit(post)
                                        findNavController().navigateUp()
                                        true
                                    }


                                    else -> false
                                }
                            }
                        }.show()

                    }
                    videoImage.isVisible = !post.video.isNullOrBlank()
                    videoImage.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                        startActivity(intent)
                    }


                }
            }
        }
        return binding.root
    }


    companion object {
        var Bundle.textArg: String? by StringArg
    }
}
