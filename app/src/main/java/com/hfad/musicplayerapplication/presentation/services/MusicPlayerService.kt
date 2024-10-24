package com.hfad.musicplayerapplication.presentation.services

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
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

    private fun stopProgressUpdates() {
        handler.removeCallbacks(updateProgressRunnable) // Останавливаем обновления
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_START_URI, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_START_URI)
        }
        track?.let {
            playTrack(it)
        }


        return START_STICKY
    }

    private fun playTrack(trackUri: Track) {
        val mediaItem = MediaItem.fromUri(trackUri.preview)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        startProgressUpdates()  // Начинаем обновлять прогресс

        val intent = Intent("MUSIC_PLAYER_ACTION").apply {
            putExtra("TRACK_TITLE", trackUri.title) // Название трека
            putExtra("TRACK_ARTIST", trackUri.title)   // Исполнитель
            putExtra("TRACK_ALBUM_ART", trackUri.cover_xl)     // URI альбомной обложки (пример)
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
