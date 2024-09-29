package com.hfad.musicplayerapplication.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.databinding.FragmentAccountBinding
import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.presentation.adapters.FriendsAdapter
import com.hfad.musicplayerapplication.presentation.viewmodels.AccountViewModel


class AccountFragment : Fragment() {
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val friendAdapter = FriendsAdapter{ item ->
            onItemClicked(item)
        }
        binding.recyclerViewFriends.adapter = friendAdapter

        val db = Firebase.firestore

        val list = mutableListOf<Friend>()

        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    //Log.d(TAG, "${document.id} => ${document.data}")

                    val name = document.getString("name") ?: "Unknown"

                    //list.add(Friend("${document.id}"))
                    list.add(Friend(name))

                    friendAdapter.submitList(list)
                    Toast.makeText(requireContext(), "Loaded ${list.size} friends", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "$exception", Toast.LENGTH_SHORT).show()
            }
    }

    private fun onItemClicked(item: Friend) {
        val db = Firebase.firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid

            val user = hashMapOf(
                "friendId" to item.name
            )

            userId?.let {
                db.collection("users")
                    .document(it)
                    .collection("friends")
                    .document(item.name)
                    //.set(mapOf("friendId" to item.name))
                    .set(user, SetOptions.merge())
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Friend added successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(requireContext(), "Error: $exception", Toast.LENGTH_SHORT).show()
                    }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}