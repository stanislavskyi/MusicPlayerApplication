package com.hfad.musicplayerapplication.presentation.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Carousel
import com.hfad.musicplayerapplication.presentation.MainActivity
import com.hfad.musicplayerapplication.presentation.adapters.CarouselAdapter
import com.hfad.musicplayerapplication.presentation.adapters.CategoryAdapter
import com.hfad.musicplayerapplication.presentation.fcm.PushService
import com.hfad.musicplayerapplication.presentation.fcm.PushService.Companion

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
        //listenMusicFromFirebase(userId.toString())

        val buttonNotification: Button = view.findViewById(R.id.buttonNotificationService)
        buttonNotification.setOnClickListener {
            checkNotificationPermission()
        }
    }

    val openRequestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                showHandsUpNotification()
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                openRequestPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showHandsUpNotification()
            }
        } else {
            showHandsUpNotification()
        }
    }

    private fun showHandsUpNotification() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationManager =
            requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Заголовок уведомления")
            .setContentText("Текст уведомления")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            .setFullScreenIntent(pendingIntent, true)


        notificationManager.notify(6666, builder.build())
    }

    private fun listenMusicFromFirebase(userId: String) {


        val db = Firebase.firestore

        db.collection("users").document(userId).collection("music")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(requireContext(), "Listen failed $e", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                for (doc in snapshots!!) {
                    val musicUrl = doc.getString("musicUrl") ?: ""

                    val action = HomeFragmentDirections.actionHomeFragmentToMusicPlayerFragment(
                        mp3 = musicUrl.toUri(),
                        title = "null",
                        bitmap = null
                    )
                    findNavController().navigate(action)

                }

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

    companion object {
        private const val CHANNEL_ID = "channel_id22"
        private const val CHANNEL_NAME = "channel_name22"
    }
}