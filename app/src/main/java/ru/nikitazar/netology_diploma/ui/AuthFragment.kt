package ru.nikitazar.netology_diploma.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.nikitazar.netology_diploma.R
import ru.nikitazar.netology_diploma.databinding.FragmentAuthBinding
import ru.nikitazar.netology_diploma.viewModel.PostViewModel

@AndroidEntryPoint
class AuthFragment : Fragment() {

    private val postViewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(inflater, container, false)

        with(binding) {

            btSignIn.setOnClickListener {
                val login = login.text.toString()
                val pass = password.text.toString()
               postViewModel.updateUser(login, pass)
                //findNavController().navigateUp() //TODO navigate to list
            }

            btSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_authFragment_to_registrationFragment)
            }
        }

        return binding.root
    }
}