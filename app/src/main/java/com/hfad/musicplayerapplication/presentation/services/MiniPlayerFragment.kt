package com.hfad.musicplayerapplication.presentation.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialContainerTransform
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.MiniPlayerBinding
import com.hfad.musicplayerapplication.presentation.MainActivity
import com.hfad.musicplayerapplication.presentation.screens.CharterFragmentDirections
import com.hfad.musicplayerapplication.presentation.screens.LibraryFragmentDirections
import com.hfad.musicplayerapplication.presentation.screens.MusicPlayerFragment
import com.hfad.musicplayerapplication.presentation.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MiniPlayerFragment : Fragment() {

    private var _binding: MiniPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedViewModel: SharedViewModel

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currentPosition = intent.getLongExtra("CURRENT_POSITION", 0L)
            val duration = intent.getLongExtra("DURATION", 1L)  // по умолчанию 1, чтобы избежать деления на 0

            if (duration > 0) {
                val progress = (currentPosition * 100 / duration).toInt()
                binding.trackProgress.progress = progress
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MiniPlayerBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.currentTrack.observe(viewLifecycleOwner) { track ->
            binding.trackTitle.text = track.title
            binding.trackArtist.text = track.artist

            Glide.with(requireContext())
                .load(track.albumArtUri)  // передаем URI альбома
//                .placeholder(R.drawable.placeholder_image) // изображение-заглушка
//                .error(R.drawable.error_image) // изображение, если ошибка
                .into(binding.albumArt) // целевой ImageView
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(progressReceiver, IntentFilter("PROGRESS_UPDATE_ACTION"))

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playPauseButton.setOnClickListener {
            Toast.makeText(requireContext(), "button", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(progressReceiver)
        _binding = null
    }
}