package com.hfad.musicplayerapplication.presentation.screens

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.hfad.musicplayerapplication.databinding.FragmentCharterBinding
import com.hfad.musicplayerapplication.presentation.adapters.TrackAdapter
import com.hfad.musicplayerapplication.presentation.services.MusicPlayerService
import com.hfad.musicplayerapplication.presentation.viewmodels.CharterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class CharterFragment : Fragment() {
    private var _binding: FragmentCharterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharterViewModel by viewModels()

    override fun onStart() {
        super.onStart()

        val componentName = ComponentName(requireContext(), MusicPlayerService::class.java)
        val sessionToken = SessionToken(requireContext(), componentName)

        val controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {

                // Call controllerFuture.get() to retrieve the MediaController.
                // MediaController implements the Player interface, so it can be
                // attached to the PlayerView UI component.

                //binding.playerView.setPlayer(controllerFuture.get())

            },
            MoreExecutors.directExecutor()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharterBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val trackAdapter = TrackAdapter()
        binding.recyclerViewTopMusic.adapter = trackAdapter

        lifecycleScope.launch {
            viewModel.tracks.collectLatest { pagingData ->
                trackAdapter.submitData(pagingData)
            }
        }

        trackAdapter.onCharterTrackItemClickListener = {
            val intent = MusicPlayerService.newIntent(requireContext(), it)
            requireContext().startService(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}