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
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentViewFullScreenBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.util.loadAttachmentView
import ru.netology.nmedia.viewmodel.PostViewModel

class AttachmentViewFullScreenFragment : Fragment(

) {

    private val viewModel: PostViewModel by activityViewModels()
    private val baseUrl = BuildConfig.BASE_URL


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAttachmentViewFullScreenBinding.inflate(inflater, container, false)
//        val postId = arguments?.textArg?.toLong() ?: { error() }
//        var pagingDataPost: List<Post>? = null
        val attachmentUrl = arguments?.textArg


//        lifecycleScope.launch {
//            viewModel.data.collectLatest { posts ->
//                posts.map { pagingDataPost = pagingDataPost?.plus(it) }
//            }
//        }
//        val post = pagingDataPost?.firstOrNull { it.id == postId }


        binding.attachmentViewFullScreen
            .loadAttachmentView("$baseUrl/media/$attachmentUrl")
//        binding.like.isChecked = post?.likedByMe ?: false //?????
//        binding.like.text = "${post?.likes}"
//        binding.like.setOnClickListener {
//            if (post != null) {
//                viewModel.likeById(post!!)
//            } else {
//                error()
//            }
//        }

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