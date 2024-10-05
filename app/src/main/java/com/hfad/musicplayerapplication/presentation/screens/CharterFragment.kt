package com.hfad.musicplayerapplication.presentation.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.hfad.musicplayerapplication.databinding.FragmentCharterBinding
import com.hfad.musicplayerapplication.presentation.adapters.TrackAdapter
import com.hfad.musicplayerapplication.presentation.viewmodels.CharterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CharterFragment : Fragment() {
    private var _binding: FragmentCharterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CharterViewModel by viewModels()

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}