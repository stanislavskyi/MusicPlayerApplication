package com.hfad.musicplayerapplication.presentation.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Audio
import com.hfad.musicplayerapplication.domain.entity.Friend
import com.hfad.musicplayerapplication.presentation.adapters.FriendsAdapter

class UserBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var userAdapter: FriendsAdapter
    private val userList = mutableListOf<Friend>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewUsers: RecyclerView = view.findViewById(R.id.recyclerViewSheetFriends)
        userAdapter = FriendsAdapter { selectedUser ->
            Toast.makeText(requireContext(), "Вы выбрали: ${selectedUser.name}", Toast.LENGTH_SHORT).show()
           // onItemClicked(selectedUser)
            // Обработка выбора пользователя
            dismiss() // Закрыть диалог после выбора
        }

        recyclerViewUsers.adapter = userAdapter

        // Получаем пользователей из Firestore
        loadUsers()
    }


    private fun onItemClicked(item: Audio) {

//        val storageRef = Firebase.storage.reference
//        val fileRef = storageRef.child("uploads/${item.uri!!.lastPathSegment}")
//        val uploadTask = fileRef.putFile(item.uri)
//
//
//        val progressBar: ProgressBar = view?.findViewById(R.id.progressBar) ?: return
//        progressBar.visibility = View.VISIBLE
//
//        val userId = FirebaseAuth.getInstance().currentUser?.uid
//
//        uploadTask.addOnFailureListener {
//            Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
//            progressBar.visibility = View.GONE
//        }.addOnSuccessListener { taskSnapshot ->
//            Toast.makeText(requireContext(), "file success upload", Toast.LENGTH_SHORT).show()
//            progressBar.visibility = View.GONE
//
//            fileRef.downloadUrl.addOnSuccessListener { uri ->
//                saveMusicUrl(userId!!, uri.toString())
//                Toast.makeText(requireContext(), "USERID AND URI success upload", Toast.LENGTH_SHORT).show()
//            }
//
//        }.addOnProgressListener { taskSnapshot ->
//            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
//            progressBar.progress = progress.toInt()
//        }

    }


    private fun loadUsers() {
        val db = Firebase.firestore
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name") ?: "Unknown"
                    userList.add(Friend(name))
                }
                userAdapter.submitList(userList)
                Toast.makeText(requireContext(), "Загружено ${userList.size} пользователей", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "$exception", Toast.LENGTH_SHORT).show()
            }
    }
}
