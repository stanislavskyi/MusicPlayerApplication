package com.hfad.musicplayerapplication.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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

        val auth = FirebaseAuth.getInstance()

        binding.loginTextView.setOnClickListener{
            findNavController().navigate(R.id.loginFragment)
        }

        binding.registerButton.setOnClickListener {

            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()


            binding.emailEditText.addTextChangedListener {
                binding.emailEditTextLayout.error = null
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Registration successful", Toast.LENGTH_SHORT)
                            .show()

                        findNavController().navigate(R.id.homeFragment)

                    } else {
                        binding.emailEditTextLayout.error = "This login already exists"
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}