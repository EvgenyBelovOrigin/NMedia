package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.tools.StringArg
import ru.netology.nmedia.viemodel.PostViewModel


class NewPostFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(inflater, container, false)

        val viewModel: PostViewModel by activityViewModels()


        binding.edit.setText(viewModel.edited.value?.content)
        arguments?.textArg?.let(binding.edit::setText)

        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            if (binding.edit.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    getString(((R.string.error_empty_content))),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                viewModel.changeContent(binding.edit.text.toString())
                viewModel.save()
            }
            viewModel.isNewPost = true
            findNavController().navigateUp()


        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.changeContent(binding.edit.text.toString())
            findNavController().navigateUp()
        }
        return binding.root

    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}


