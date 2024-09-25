package com.hfad.musicplayerapplication.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentLibraryBinding
import com.hfad.musicplayerapplication.domain.Audio
import com.hfad.musicplayerapplication.presentation.adapters.MusicAdapter

class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private lateinit var musicRecyclerView: RecyclerView
    private lateinit var musicAdapter: MusicAdapter
    private val musicList = mutableListOf<Audio>()
    private val openFilePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            displayAudioInfo(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        musicAdapter = MusicAdapter()
        musicRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            3
        )
        musicRecyclerView.adapter = musicAdapter

        val loadMusicButton: Button = view.findViewById(R.id.loadMusicButton)
        loadMusicButton.setOnClickListener {
            openFilePicker.launch(arrayOf("audio/*"))
        }
    }

    private fun displayAudioInfo(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), uri)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val albumArt = retriever.embeddedPicture

        var bitmap: Bitmap?

        Log.d("MAIN_TAG", "$albumArt")
        if (albumArt != null){
            bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
        } else{
            bitmap = null
        }

        musicList.add(Audio(title, bitmap))
        musicAdapter.submitList(musicList.toList())

        retriever.release()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}