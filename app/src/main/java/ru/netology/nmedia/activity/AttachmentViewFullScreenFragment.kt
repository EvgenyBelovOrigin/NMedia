package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback

import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.paging.map
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentViewFullScreenBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.loadAttachmentView
import ru.netology.nmedia.viewmodel.PostViewModel

class AttachmentViewFullScreenFragment : Fragment(

) {
    private val viewModel: PostViewModel by activityViewModels()
    private val postId = arguments?.textArg?.toLong() ?: { error() }
    private val baseUrl = BuildConfig.BASE_URL
    private var post: Post? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAttachmentViewFullScreenBinding.inflate(inflater, container, false)
//        val viewModel: PostViewModel by viewModels()
//        val postId = arguments?.textArg?.toLong() ?: { error() }
//        val baseUrl = BuildConfig.BASE_URL

        lifecycleScope.launch {
            viewModel.data.collectLatest { data ->
                data.map {
                    if (it.id == postId) {
                        post = it
                    }
                }

                binding.attachmentViewFullScreen
                    .loadAttachmentView("$baseUrl/media/${post?.attachment?.url}")
                binding.like.isChecked = post?.likedByMe ?: false //?????
                binding.like.text = "${post?.likes}"
                binding.like.setOnClickListener {
                    if (post != null) {
                        viewModel.likeById(post!!)
                    } else {
                        error()
                    }
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_close_full_screen_view, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.close) {
                        findNavController().navigateUp()
                        return true
                    } else {
                        return false
                    }
                }
            },
            viewLifecycleOwner,
        )

        return binding.root
    }

    private fun error() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.error)
            .setMessage(R.string.error_loading)
            .setPositiveButton(R.string.ok) {
                    _, _,
                ->
                findNavController().navigateUp()
            }
            .show()
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}