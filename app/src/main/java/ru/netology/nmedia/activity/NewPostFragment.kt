package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
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

        val viewModel: PostViewModel by activityViewModels(
//            ownerProducer = ::requireParentFragment
        )

        arguments?.textArg?.let(binding.edit::setText)
        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank()) {
//                Toast.makeText(
//                    this@NewPostFragment,
//                    getString(R.string.error_empty_content),
//                    Toast.LENGTH_SHORT
//                ).show()
                activity?.setResult(Activity.RESULT_CANCELED, intent)
            } else {
                viewModel.changeContent(binding.edit.text.toString())
                viewModel.save()
                findNavController().navigateUp()
//                val content = binding.edit.text.toString()
//                intent.putExtra(Intent.EXTRA_TEXT, content)
//                activity?.setResult(Activity.RESULT_OK, intent)
            }


        }
        return binding.root

    }

    companion object {
        var Bundle.textArg: String? by StringArg
    }
}


