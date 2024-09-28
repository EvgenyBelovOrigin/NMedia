package ru.netology.nmedia.activity

import android.content.DialogInterface
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
        val postId = arguments?.textArg?.toLong() ?: { error()}
//        var post: Post? = null
        val baseUrl = BuildConfig.BASE_URL
        viewModel.data.observe(viewLifecycleOwner) { data ->
            val post = data.posts.firstOrNull { it.id == postId } ?: null
            if (post == null) {
                error()
            }
            binding.attachmentViewFullScreen
                .loadAttachmentView("$baseUrl/media/${post?.attachment?.url}")
            binding.like.isChecked = post?.likedByMe ?: false //?????
            binding.like.text = "${post?.likes}"
        }



        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
//        binding.attachmentViewFullScreen
//            .loadAttachmentView("$baseUrl/media/${post?.attachment?.url}")




//        binding.like.setOnClickListener()


//        binding.edit.setText(viewModel.edited.value?.content)
//        arguments?.textArg
//            ?.let(binding.edit::setText)
//
//        binding.edit.requestFocus()


        requireActivity().addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_new_post, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    if (menuItem.itemId == R.id.save) {
//                        viewModel.changeContent(binding.edit.text.toString())
//                        viewModel.save()
//                        AndroidUtils.hideKeyboard(requireView())
                        return true
                    } else {
                        return false
                    }
                }
            },
            viewLifecycleOwner,
        )

        //        viewModel.postCreated.observe(viewLifecycleOwner) {
////            viewModel.loadPosts()
//            findNavController().navigateUp()
//        }

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
            .show()    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}