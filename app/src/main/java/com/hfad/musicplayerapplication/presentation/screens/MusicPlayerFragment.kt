package com.hfad.musicplayerapplication.presentation.screens

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentMusicPlayerBinding
import java.io.IOException


class MusicPlayerFragment : Fragment() {

    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var uri: Uri
    private var bitmapImage: Bitmap? = null
    private lateinit var title: String

    private lateinit var seekBar: SeekBar
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            uri = MusicPlayerFragmentArgs.fromBundle(it).mp3
            bitmapImage = MusicPlayerFragmentArgs.fromBundle(it).bitmap
            title = MusicPlayerFragmentArgs.fromBundle(it).title

        }

        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imAlbum = view.findViewById<ImageView>(R.id.albumCover)

        Log.d("MAIN_TAG", "$bitmapImage")


        if (bitmapImage != null) {
            imAlbum.setImageBitmap(bitmapImage)
        }
        else{
            imAlbum.setImageResource(R.drawable.ic_launcher_background)
        }


        val textViewTitle = view.findViewById<TextView>(R.id.songTitle)
        textViewTitle.text = title

        seekBar = view.findViewById(R.id.progressBar)
        handler = Handler()

        try {
            val contentResolver = requireContext().contentResolver
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")

            if (fileDescriptor != null) {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(fileDescriptor.fileDescriptor) // Используйте fileDescriptor
                    prepare()
                    start()
                    seekBar.max = duration // Установите максимальное значение SeekBar

                    updateSeekBarRunnable = object : Runnable {
                        override fun run() {
                            seekBar.progress = currentPosition // Обновите прогресс SeekBar
                            handler.postDelayed(this, 1000) // Обновляйте каждую секунду
                        }
                    }
                    handler.post(updateSeekBarRunnable) // Запустите обновление SeekBar
                }
                fileDescriptor.close()
            } else {
                Log.e("MUSIC_PLAYER", "Failed to open file descriptor for URI: $uri")
            }
        } catch (e: IOException) {
            Log.e("MUSIC_PLAYER", "Error setting data source: ${e.message}")
        }

        binding.group.setOnClickListener {
            val userList = mutableListOf<String>()

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            userId?.let {
                Firebase.firestore.collection("users")
                    .document(it).collection("friends")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val userName = document.id
                            userList.add(userName)
                        }

                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Какому другу отправить трек?")
                            .setItems(userList.toTypedArray()) { dialog, which ->
                                val selectedUser = userList[which]

                                onItemClicked(selectedUser)


                                Toast.makeText(
                                    requireContext(),
                                    "Вы выбрали: $selectedUser",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .setNegativeButton("Отмена") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки пользователей",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun onItemClicked(userId: String) {

        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("uploads/${uri.lastPathSegment}")
        val uploadTask = fileRef.putFile(uri)


        val progressBar: ProgressBar = view?.findViewById(R.id.progressBar) ?: return
        progressBar.visibility = View.VISIBLE


        uploadTask.addOnFailureListener {
            Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(requireContext(), "file success upload", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                saveMusicUrl(userId, uri.toString())
                Toast.makeText(requireContext(), "USERID AND URI success upload", Toast.LENGTH_SHORT).show()
            }

        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBar.progress = progress.toInt()
        }

    }

    private fun saveMusicUrl(userId: String, musicUrl: String) {
        val db = Firebase.firestore
        val musicData = hashMapOf(
            "musicUrl" to musicUrl
        )

        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result){

                Log.d("MusicPlayerFragment", document.id)

                if (document.getString("name").toString() == userId){
                    Log.d("MusicPlayerFragment", "///////////////////////////: ${document.id}")
                    Log.d("MusicPlayerFragment", "///////////////////////////: ${document.getString("name").toString()}")

                    db.collection("users").document(document.id)
                        .collection("music").add(musicData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Music URL saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error saving music URL", Toast.LENGTH_SHORT).show()
                        }

                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        mediaPlayer.release()
        handler.removeCallbacks(updateSeekBarRunnable) // Остановите обновление SeekBar
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}