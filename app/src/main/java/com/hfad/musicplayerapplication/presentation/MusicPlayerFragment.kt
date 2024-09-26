package com.hfad.musicplayerapplication.presentation

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
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hfad.musicplayerapplication.R
import java.io.IOException


class MusicPlayerFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var uri: String
    private lateinit var bitmapImage: Bitmap
    private lateinit var title: String

    private lateinit var seekBar: SeekBar
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            uri = MusicPlayerFragmentArgs.fromBundle(it).mp3
            bitmapImage = MusicPlayerFragmentArgs.fromBundle(it).bitmap
            title = MusicPlayerFragmentArgs.fromBundle(it).title
            //Toast.makeText(requireContext(), "MUSIC PLAYER FRAGMENT: $uri", Toast.LENGTH_SHORT).show()
        }

        return inflater.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imAlbum = view.findViewById<ImageView>(R.id.albumCover)
        imAlbum.setImageBitmap(bitmapImage)


            val textViewTitle = view.findViewById<TextView>(R.id.songTitle)
            textViewTitle.text = title

            seekBar = view.findViewById(R.id.progressBar)
            handler = Handler()

            try {
                val contentResolver = requireContext().contentResolver
                val fileDescriptor = contentResolver.openFileDescriptor(Uri.parse(uri), "r")

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

    }