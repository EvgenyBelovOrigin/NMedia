package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentOpenPostBinding
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

        var openPostId = 0L
        viewModel.openPostId.observe(viewLifecycleOwner) {
            openPostId = it
        }
        val tools = Tools()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.emptyOpenPostData()
            findNavController().navigateUp()
        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->

            val post = posts.find { it.id == openPostId } ?: return@observe

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
                                    findNavController().navigateUp()
                                    true
                                }

                                R.id.edit -> {
                                    viewModel.edit(post)
                                    viewModel.emptyOpenPostData()
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
        return binding.root
    }

}
