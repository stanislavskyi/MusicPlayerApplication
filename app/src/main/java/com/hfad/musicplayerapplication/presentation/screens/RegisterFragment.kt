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
import com.hfad.musicplayerapplication.databinding.FragmentRegisterBinding
import com.hfad.musicplayerapplication.presentation.viewmodels.LoginViewModel
import com.hfad.musicplayerapplication.presentation.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val auth = FirebaseAuth.getInstance()

        binding.loginTextView.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }

        binding.registerButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()


            binding.emailEditText.addTextChangedListener {
                binding.emailEditTextLayout.error = null
            }

            viewModel.register(email, password, {
                Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.homeFragment)
            }, { errorMessage ->
                binding.emailEditTextLayout.error = errorMessage
            })


//            auth.createUserWithEmailAndPassword(email, password)
//                .addOnCompleteListener(requireActivity()) { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT)
//                            .show()
//
//                        findNavController().navigate(R.id.homeFragment)
//
//                    } else {
//                        binding.emailEditTextLayout.error = "This login already exists"
//                    }
//                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}