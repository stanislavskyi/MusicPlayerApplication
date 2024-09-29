package com.hfad.musicplayerapplication.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentLoginBinding
import com.hfad.musicplayerapplication.presentation.viewmodels.LoginViewModel
import com.hfad.musicplayerapplication.presentation.viewmodels.PremiumViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerTextView.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }


        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            passwordAndEmailTextChangedListener()


            viewModel.login(email, password, {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.accoutnFragment)
            }, { errorMessage ->
                binding.emailEditTextLayout.error = errorMessage
                binding.passwordEditTextLayout.error = errorMessage
            })
        }

    }

    private fun passwordAndEmailTextChangedListener() {
        binding.emailEditText.addTextChangedListener {
            binding.emailEditTextLayout.error = null
            binding.passwordEditTextLayout.error = null
        }
        binding.passwordEditText.addTextChangedListener {
            binding.passwordEditTextLayout.error = null
            binding.emailEditTextLayout.error = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}