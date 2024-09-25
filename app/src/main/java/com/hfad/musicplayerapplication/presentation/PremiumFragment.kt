package com.hfad.musicplayerapplication.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentPremiumBinding
import com.hfad.musicplayerapplication.databinding.FragmentRegisterBinding


class PremiumFragment : Fragment() {

    private var _binding: FragmentPremiumBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        binding.button12.setOnClickListener {
            val user = hashMapOf(
                "isPremium" to true,
                "subscriptionDate" to FieldValue.serverTimestamp()
            )
            userId?.let {
                db.collection("users").document(it).set(user, SetOptions.merge())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
