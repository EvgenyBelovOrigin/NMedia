package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAttachmentViewFullScreenBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class AttachmentViewFullScreenFragment : Fragment() {

//    companion object {
//        var Bundle.textArg: String? by StringArg
//    }

    private val viewModel: PostViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentAttachmentViewFullScreenBinding.inflate(
            inflater,
            container,
            false
        )


//        binding.edit.setText(viewModel.edited.value?.content)
//        arguments?.textArg
//            ?.let(binding.edit::setText)
//
//        binding.edit.requestFocus()

        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == ImagePicker.RESULT_ERROR) {
                    Snackbar.make(
                        binding.root,
                        ImagePicker.getError(it.data),
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val uri = it.data?.data ?: return@registerForActivityResult
                    viewModel.updatePhoto(uri, uri.toFile())
                }
            }


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
}