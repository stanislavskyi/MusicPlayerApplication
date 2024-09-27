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
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hfad.musicplayerapplication.MyBottomSheetFragment
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

        binding.floatingActionButtonSheet.setOnClickListener {
            val bottomSheetFragment = MyBottomSheetFragment()
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        musicRecyclerView = view.findViewById(R.id.musicRecyclerView)
        musicAdapter = MusicAdapter { item ->
            onItemClicked(item)
        }
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

        val audioItem = Audio(title, bitmap, uri)
        musicList.add(audioItem)
        musicAdapter.submitList(musicList.toList())

        retriever.release()

    }

    private fun onItemClicked(item: Audio) {
        //Toast.makeText(requireContext(), "Clicked: ${item.uri}", Toast.LENGTH_SHORT).show()
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("uploads/${item.uri!!.lastPathSegment}")
        val uploadTask = fileRef.putFile(item.uri)


        val progressBar: ProgressBar = view?.findViewById(R.id.progressBar) ?: return
        progressBar.visibility = View.VISIBLE

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        uploadTask.addOnFailureListener {
            Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(requireContext(), "file success upload", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                saveMusicUrl(userId!!, uri.toString())
                Toast.makeText(requireContext(), "USERID AND URI success upload", Toast.LENGTH_SHORT).show()
            }

        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBar.progress = progress.toInt()
        }

        if (item.uri != null){
            //val action = LibraryFragmentDirections.actionLibraryFragmentToMusicPlayerFragment(mp3 = item.uri, bitmap = item.imageLong!!, title = item.title!!)
            //findNavController().navigate(action)
        }
    }

    private fun saveMusicUrl(userId: String, musicUrl: String) {
        val db = Firebase.firestore
        val musicData = hashMapOf(
            "musicUrl" to musicUrl
        )
        //db.collection("users").document(userId)
            db.collection("users").document("LJ1rNv7LOycbg7TCmgQ6qb2BVSj1")
            .collection("music").add(musicData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Music URL saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error saving music URL", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}