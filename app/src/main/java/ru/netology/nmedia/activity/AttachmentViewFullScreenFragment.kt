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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentAttachmentViewFullScreenBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.loadAttachmentView
import ru.netology.nmedia.viewmodel.PostViewModel

class AttachmentViewFullScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAttachmentViewFullScreenBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by activityViewModels()
        val postId = arguments?.textArg?.toLong() ?: { error() }
        val baseUrl = BuildConfig.BASE_URL

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                val posts: List<Post> = viewModel.data.take(20) as List<Post>
                val post = posts.firstOrNull{it.id == postId}
//                { data ->
//                    val post = data.map { it.id == postId } ?: null
//                    if (post == null) {
//                        error()

                binding.attachmentViewFullScreen
                    .loadAttachmentView("$baseUrl/media/${post?.attachment?.url}")
                binding.like.isChecked = post?.likedByMe ?: false //?????
                binding.like.text = "${post?.likes}"
                binding.like.setOnClickListener {
                    if (post != null) {
                        viewModel.likeById(post)
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

    fun error() {
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