package com.hfad.musicplayerapplication.presentation.services

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.transition.Hold
import com.google.common.util.concurrent.MoreExecutors
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.MiniPlayerBinding
import com.hfad.musicplayerapplication.presentation.MainActivity
import com.hfad.musicplayerapplication.presentation.viewmodels.SharedViewModel
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MiniPlayerFragment : Fragment() {

    private var _binding: MiniPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currentPosition = intent.getLongExtra("CURRENT_POSITION", 0L)
            val duration =
                intent.getLongExtra("DURATION", 1L)  // по умолчанию 1, чтобы избежать деления на 0

            if (duration > 0) {
                val progress = (currentPosition * 100 / duration).toInt()
                binding.trackProgress.progress = progress
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val componentName = ComponentName(requireContext(), MusicPlayerService::class.java)
        val sessionToken = SessionToken(requireContext(), componentName)

        val controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {

                val controller = controllerFuture.get()

                binding.pauseButton.setOnClickListener {
                    controller.pause()
                    it.visibility = View.GONE
                    binding.playPauseButton.visibility = View.VISIBLE
                }

                binding.playPauseButton.setOnClickListener {
                    controller.play()
                    it.visibility = View.GONE
                    binding.pauseButton.visibility = View.VISIBLE
                }

            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MiniPlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.currentTrack.observe(viewLifecycleOwner) { track ->

            Log.d("MY_TAG_URI", "trackUri123: ${track.uri?.scheme}")

            if (track.uri?.scheme == "content") {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(requireContext(), track.uri)

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)

                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

                val albumArt = retriever.embeddedPicture
                val bitmap: Bitmap? = if (albumArt != null) {
                    BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
                } else {
                    BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
                }


                Palette.from(bitmap!!).generate { palette ->
                    val dominantColor = palette?.getDominantColor(Color.BLACK)
                    val vibrantColor = palette?.getVibrantColor(Color.BLACK)
                    val mutedColor = palette?.getMutedColor(Color.BLACK)

                    binding.miniPlayerCard.setCardBackgroundColor(ColorStateList.valueOf(dominantColor!!))
                }


                Log.d(
                    "MY_TAG_URI",
                    "title $title, albumArt $albumArt, bitmap: $bitmap trackUri ${track.uri}"
                )


                binding.trackTitle.text = title
                binding.trackArtist.text = artist

                Glide.with(requireContext())
                    .load(bitmap)  // передаем URI альбома
                    //.placeholder(R.drawable.ic_launcher_background) // изображение-заглушка
//                .error(R.drawable.error_image) // изображение, если ошибка
                    .into(binding.albumArt) // целевой ImageView

            } else {
                binding.trackTitle.text = track.title
                binding.trackArtist.text = track.artist

//                Glide.with(requireContext())
//                    .load(track.cover_xl)  // передаем URI альбома
//                    //.placeholder(R.drawable.ic_launcher_background) // изображение-заглушка
////                .error(R.drawable.error_image) // изображение, если ошибка
//                    .into(binding.albumArt) // целевой ImageView

                Picasso.get()
                    .load("https://e-cdns-images.dzcdn.net/images/cover/${track.md5_image}/250x250.jpg")
                    .into(binding.albumArt)


                Glide.with(this)
                    .asBitmap()  // Загружаем как Bitmap
                    .load(track.cover_xl)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            // Используй Bitmap (resource) здесь

                            Palette.from(resource).generate { palette ->
                                val dominantColor = palette?.getDominantColor(Color.WHITE)
                                val vibrantColor = palette?.getVibrantColor(Color.BLACK)
                                val mutedColor = palette?.getMutedColor(Color.BLACK)

                                binding.miniPlayerCard.setCardBackgroundColor(ColorStateList.valueOf(dominantColor!!))
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // Очистка ресурсов, если нужно
                        }
                    })
            }
        }

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(progressReceiver, IntentFilter("PROGRESS_UPDATE_ACTION"))

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        view.setOnClickListener {
            (activity as MainActivity).showFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(progressReceiver)
        _binding = null
    }
}