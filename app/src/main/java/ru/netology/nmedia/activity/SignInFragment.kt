package ru.netology.nmedia.activity

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
import ru.netology.nmedia.viewmodel.SignInViewModel

class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
            binding.loginEdit.requestFocus()
        }
        binding.signInButton.setOnClickListener {
            viewModel.signIn(binding.loginEdit.text.toString(), binding.passEdit.text.toString())
            binding.progress.isVisible = true
            binding.loginEdit.clearFocus()
            binding.passEdit.clearFocus()
        }
        viewModel.signedIn.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            findNavController().navigateUp()
        }
        viewModel.notFoundException.observe(viewLifecycleOwner) {
            binding.progress.isVisible = false
            binding.error.isVisible = true
        }
        viewModel.exception.observe(viewLifecycleOwner) {
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

        binding.loginEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
        }
        binding.passEdit.setOnFocusChangeListener { _, _ ->
            binding.error.isVisible = false
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
}