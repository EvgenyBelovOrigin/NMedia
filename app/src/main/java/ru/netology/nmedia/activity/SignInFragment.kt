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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.viewmodel.SignInViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

@AndroidEntryPoint
class SignInFragment : Fragment() {
//    private val dependencyContainer = DependencyContainer.getInstance()
    private val viewModel: SignInViewModel by viewModels()
//        ownerProducer = ::requireParentFragment,
//        factoryProducer = {
//            ViewModelFactory(dependencyContainer.repository, dependencyContainer.appAuth)
//        }
//    )

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
            binding.progress.isVisible = true
            viewModel.signIn(binding.loginEdit.text.toString(), binding.passEdit.text.toString())
            binding.signInFrame.clearFocus()
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