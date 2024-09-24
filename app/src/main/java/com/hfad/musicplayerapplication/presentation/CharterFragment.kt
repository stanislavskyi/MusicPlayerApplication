package com.hfad.musicplayerapplication.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.presentation.adapters.TrackAdapter
import com.hfad.musicplayerapplication.presentation.viewmodels.TrackViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class CharterFragment : Fragment() {

    private val viewModel: TrackViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_charter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerViewTopMusic = view.findViewById<RecyclerView>(R.id.recyclerViewTopMusic)
        recyclerViewTopMusic.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        val trackAdapter = TrackAdapter()
        recyclerViewTopMusic.adapter = trackAdapter

        lifecycleScope.launch {
            viewModel.tracks.collectLatest { pagingData ->
                trackAdapter.submitData(pagingData)
            }
        }
    }


}