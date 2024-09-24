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
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.domain.Carousel
import com.hfad.musicplayerapplication.presentation.adapters.CarouselAdapter
import com.hfad.musicplayerapplication.presentation.adapters.CategoryAdapter
import com.hfad.musicplayerapplication.presentation.adapters.TrackAdapter
import com.hfad.musicplayerapplication.presentation.viewmodels.TrackViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {



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

    }
}