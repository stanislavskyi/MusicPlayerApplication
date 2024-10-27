package com.hfad.musicplayerapplication.presentation.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.entity.Track

class MusicPlayerService : MediaSessionService() {

    private lateinit var exoPlayer: ExoPlayer
    private var mediaSession: MediaSession? = null
    private val handler = Handler(Looper.getMainLooper()) // Handler, привязанный к основному потоку

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, exoPlayer).build()

    }


    private val updateProgressRunnable = object : Runnable {
        override fun run() {
                val currentPosition = exoPlayer.currentPosition
                val duration = exoPlayer.duration

                val intent = Intent("PROGRESS_UPDATE_ACTION").apply {
                    putExtra("CURRENT_POSITION", currentPosition)
                    putExtra("DURATION", duration)
                }

                LocalBroadcastManager.getInstance(this@MusicPlayerService).sendBroadcast(intent)

                handler.postDelayed(this, 500)

        }
    }

    private fun startProgressUpdates() {
        handler.post(updateProgressRunnable) // Запускаем обновления
    }
//
    private fun stopProgressUpdates() {
        handler.removeCallbacks(updateProgressRunnable) // Останавливаем обновления
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
//        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent?.getParcelableExtra(EXTRA_START_URI, Track::class.java)
//        } else {
//            @Suppress("DEPRECATION")
//            intent?.getParcelableExtra(EXTRA_START_URI)
//        }


        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_START_URI, Track::class.java)
        }else{
            intent?.getParcelableExtra(EXTRA_START_URI, Track::class.java)
        }

        track?.let {
            playTrack(it)
        }


        return START_STICKY
    }

    private fun playTrack(trackUri: Track) {
        var uri: Uri? = null

        val x = trackUri.uri

        if (Uri.parse(trackUri.preview).scheme == "https"){
            uri = Uri.parse(trackUri.preview)
        }

        Log.d("MY_TAG_URI", "x ${x?.scheme}")
        if(x?.scheme == "content"){
            uri = x
        }


        Log.d("MY_TAG_URI", "uri $uri")

        val mediaItem = MediaItem.fromUri(uri!!)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        // Извлекаем информацию о треке
//        val projection = arrayOf(
//            MediaStore.Audio.Media.DISPLAY_NAME, // Название трека
//            MediaStore.Audio.Media.ARTIST,        // Исполнитель
//            MediaStore.Audio.Media.ALBUM          // Альбом
//        )

//        val cursor = contentResolver.query(trackUri, projection, null, null, null)
//        cursor?.use {
//            if (it.moveToFirst()) {
//                val trackTitle = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
//                val trackArtist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
//                val trackAlbum = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM))
//
//
//                // Отправляем информацию через LocalBroadcastManager
//                Log.d("MY_TAG_URI", "trackTitle $trackTitle, trackArtist $trackArtist, trackAlbum $trackAlbum")
//            }
//        }


        startProgressUpdates()  // Начинаем обновлять прогресс



        val intent = Intent("MUSIC_PLAYER_ACTION").apply {
            putExtra("TRACK_URI", trackUri)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onDestroy() {
        stopProgressUpdates() // Останавливаем обновления прогресса при уничтожении сервиса

        exoPlayer.stop()
        exoPlayer.release()

        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

    companion object {

        private const val EXTRA_START_URI = "start"
        fun newIntent(context: Context, start: Track): Intent {
            return Intent(context, MusicPlayerService::class.java).apply {
                putExtra(EXTRA_START_URI, start)
            }
        }
    }
}
