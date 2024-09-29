package com.hfad.musicplayerapplication.presentation.screens

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Carousel
import com.hfad.musicplayerapplication.presentation.adapters.CarouselAdapter
import com.hfad.musicplayerapplication.presentation.adapters.CategoryAdapter

class HomeFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentMusicUrl: String? = null  // Чтобы отслеживать уже проигранную музыку

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.carouselRecyclerView)
        recyclerView.layoutManager = CarouselLayoutManager()

        val snapHelper = CarouselSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        val items = listOf(
            Carousel(R.drawable.ic_launcher_background),
            Carousel(R.drawable.ic_launcher_background),
            Carousel(R.drawable.ic_launcher_background),
            Carousel(R.drawable.ic_launcher_background),
            Carousel(R.drawable.ic_launcher_background),
            Carousel(R.drawable.ic_launcher_background)
        )

        recyclerView.adapter = CarouselAdapter(items)

        val recyclerViewCategory = view.findViewById<RecyclerView>(R.id.recyclerViewCategory)
        recyclerViewCategory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerViewCategory.adapter = CategoryAdapter(items)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        listenForMusicUpdates(userId.toString())

    }

    private fun listenForMusicUpdates(userId: String) {
        val db = Firebase.firestore

        db.collection("users").document(userId)
            .collection("music")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Listen failed: $e", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (doc in snapshots!!) {
                    val musicUrl = doc.getString("musicUrl") ?: ""

                    // Проверяем, что это новый трек
                    if (musicUrl != currentMusicUrl) {
                        currentMusicUrl = musicUrl
                        playMusic(musicUrl)
                    }
                }
            }
    }

    private fun getMusicUrl(userId: String, callback: (String) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").document(userId)
            .collection("music").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val musicUrl = document.getString("musicUrl") ?: ""
                    callback(musicUrl)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error getting music URL", Toast.LENGTH_SHORT).show()
            }
    }


    private fun playMusic(musicUrl: String) {
        mediaPlayer?.release()  // Освобождаем предыдущий MediaPlayer, если он уже воспроизводил музыку

        mediaPlayer = MediaPlayer().apply {
            setDataSource(musicUrl)
            prepare()
            start()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }


}