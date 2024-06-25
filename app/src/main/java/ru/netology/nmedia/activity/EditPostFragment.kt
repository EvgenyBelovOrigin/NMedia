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
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.tools.StringArg
import ru.netology.nmedia.viemodel.PostViewModel

class EditPostFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentEditPostBinding.inflate(inflater, container, false)
        val viewModel: PostViewModel by activityViewModels()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.empty()
            findNavController().navigateUp()
        }

        val text = arguments?.textArg

        binding.edit.setText(text)
        binding.editedPostText.text = text
        binding.edit.requestFocus()
        binding.edit.setSelection(text?.length ?: 0)
        binding.ok.setOnClickListener {
            if (binding.edit.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    getString(R.string.error_empty_content),
                    Toast.LENGTH_LONG
                ).show()
                viewModel.empty()

            } else {
                viewModel.changeContent(binding.edit.text.toString())
                viewModel.save()
            }
            findNavController().navigateUp()

        }
        binding.close.setOnClickListener {
            viewModel.empty()
            findNavController().navigateUp()
        }
        return binding.root
    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }


}

