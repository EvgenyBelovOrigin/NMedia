package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.AttachmentViewFullScreenFragment.Companion.textArg
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.concurrent.thread

class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val authViewModel = AuthViewModel()
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
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
            }

            override fun onShowAttachmentViewFullScreen(post: Post) {
                findNavController().navigate(R.id.action_feedFragment_to_attachmentViewFullScreen,
                    Bundle().apply {
                        textArg = post.id.toString()
                    })
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val newPost =
                state.posts.size > adapter.currentList.size && adapter.currentList.size > 0
            adapter.submitList(state.posts)
            if (newPost) {
                binding.list.smoothScrollToPosition(0)
            }
            binding.emptyText.isVisible = state.empty
        }


        viewModel.newPostsCount.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.refreshPosts.isVisible = true
            }
        }

        binding.refreshPosts.setOnClickListener {
            viewModel.makeOld()
            binding.refreshPosts.isVisible = false
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) {
                        viewModel.loadPosts()
                    }
                    .show()
            }
            if (state.onDeleteError) {
                Toast.makeText(
                    activity,
                    R.string.error_delete,
                    Toast.LENGTH_LONG
                ).show()
            }
            if (state.onSaveError) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.error)
                    .setMessage(R.string.error_saving)
                    .setPositiveButton(R.string.try_again) {
                            _, _,
                        ->
                        findNavController().navigate(R.id.newPostFragment)
                    }
                    .setNegativeButton(R.string.return_to_posts, null)
                    .show()
            }

            binding.swiperefresh.isRefreshing = state.refreshing
        }

        binding.fab.setOnClickListener {
            if (!authViewModel.authenticated) {
                requestSignIn()
            }else {
                findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        viewModel.onLikeError.observe(viewLifecycleOwner) { id ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.error)
                .setMessage(R.string.error_like)
                .setPositiveButton(R.string.ok, null)
                .show()
            adapter.currentList.indexOfFirst { it.id == id }
                .takeIf { it != -1 }
                ?.let(adapter::notifyItemChanged)
        }
        viewModel.requestSignIn.observe(viewLifecycleOwner) {
            requestSignIn()
        }

        return binding.root
    }

    fun requestSignIn() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.requestSignInTitle)
            .setMessage(R.string.requestSignInMessage)
            .setPositiveButton(R.string.ok) {
                    _, _,
                ->
                findNavController().navigate(R.id.signInFragment)
            }
            .setNegativeButton(R.string.return_to_posts, null)
            .show()
    }
}
