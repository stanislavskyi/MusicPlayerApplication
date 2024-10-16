package com.hfad.musicplayerapplication.presentation.screens

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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentLibraryBinding
import com.hfad.musicplayerapplication.domain.entity.Audio
import com.hfad.musicplayerapplication.presentation.BottomSheetListener
import com.hfad.musicplayerapplication.presentation.ModalBottomSheet
import com.hfad.musicplayerapplication.presentation.adapters.MusicAdapter

class LibraryFragment : Fragment(), BottomSheetListener {

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

        binding.floatingActionButtonSheet.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(this)
            modalBottomSheet.show(childFragmentManager, ModalBottomSheet.TAG)
        }

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        musicAdapter = MusicAdapter { item ->
            onItemClicked(item)
        }

        musicRecyclerView.adapter = musicAdapter
    }

    override fun onUploadTrackClicked() {
        openFilePicker.launch(arrayOf("audio/*"))
    }

    private fun displayAudioInfo(uri: Uri) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(requireContext(), uri)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val albumArt = retriever.embeddedPicture

        Log.d("MAIN_TAG", "$albumArt")

        var bitmap: Bitmap?

        if (albumArt != null) {
            bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
        } else {
            bitmap = BitmapFactory.decodeResource(requireContext().resources, R.drawable.downloading_20px)
        }

        val audioItem = Audio(title, bitmap, uri)
        musicList.add(audioItem)
        musicAdapter.submitList(musicList.toList())
        retriever.release()

    }

    private fun onItemClicked(item: Audio) {
        if (item.uri != null){
            Log.d("MAIN_TAG", "${item.imageLong}, ${item.uri}, ${item.title}")
            val action = LibraryFragmentDirections.actionLibraryFragmentToMusicPlayerFragment(mp3 = item.uri, bitmap = item.imageLong, title = item.title!!)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}