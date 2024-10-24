package com.hfad.musicplayerapplication.presentation.screens

import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentMusicPlayerBinding
import com.hfad.musicplayerapplication.di.TokenData
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit



class MusicPlayerFragment : Fragment() {

    private var _binding: FragmentMusicPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var uri: Uri
    private var bitmapImage: Bitmap? = null
    private lateinit var title: String

    private lateinit var seekBar: SeekBar
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var handler: Handler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            uri = MusicPlayerFragmentArgs.fromBundle(it).mp3
            bitmapImage = MusicPlayerFragmentArgs.fromBundle(it).bitmap
            title = MusicPlayerFragmentArgs.fromBundle(it).title

        }
        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MAIN_TAG", "$bitmapImage")
        if (bitmapImage != null) {
            binding.albumCover.setImageBitmap(bitmapImage)
        }
        else{
            binding.albumCover.setImageResource(R.drawable.ic_launcher_background)
        }

        binding.songTitle.text = title
        seekBar = view.findViewById(R.id.progressBar)
        handler = Handler(Looper.getMainLooper())

        ///////

        Log.d("MY_TAG", "uri $uri")
        Log.d("MY_TAG", "scheme ${uri.scheme}")
        if (uri.scheme == "content"){
            try {
                val contentResolver = requireContext().contentResolver

                Log.d("MY_TAG", "contentResolver $contentResolver")
                val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")

                Log.d("MY_TAG", "fileDescriptor $fileDescriptor")
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
        if (uri.scheme == "https")
        {
           // mediaPlayer.release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(uri.toString())
                prepare()
                start()

                seekBar.max = duration
                updateSeekBarRunnable = object : Runnable {
                    override fun run() {
                        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                            seekBar.progress = mediaPlayer.currentPosition // Обновите прогресс SeekBar
                        }

                       // seekBar.progress = currentPosition // Обновите прогресс SeekBar
                        handler.postDelayed(this, 1000) // Обновляйте каждую секунду
                    }
                }
                handler.post(updateSeekBarRunnable)
            }
        }


        val currentTime = binding.currentTime
        val totalTime = binding.totalTime

        totalTime.text = formatTime(mediaPlayer.duration)

        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer.currentPosition
                currentTime.text = formatTime(mediaPlayer.currentPosition)
                handler.postDelayed(this, 1000) // Обновляем каждую секунду
            }
        }

        binding.imageButtonPause.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.pause()
                handler.removeCallbacks(updateSeekBarRunnable)
                binding.imageButtonPause.visibility = View.GONE
                binding.imageButtonPlay.visibility = View.VISIBLE
            }
        }

        binding.imageButtonPlay.setOnClickListener {
            if (!mediaPlayer.isPlaying){
                mediaPlayer.start()
                handler.post(updateSeekBarRunnable)
                binding.imageButtonPause.visibility = View.VISIBLE
                binding.imageButtonPlay.visibility = View.GONE
            }
        }

        mediaPlayer.setOnCompletionListener {
            Toast.makeText(requireContext(), "Track finished", Toast.LENGTH_SHORT).show()
            mediaPlayer.seekTo(0) // Вернуться в начало трека
            seekBar.progress = 0
            handler.removeCallbacks(updateSeekBarRunnable) // Остановить обновление
        }

        ///////////
        binding.imageButtonGroup.setOnClickListener {
            val userList = mutableListOf<String>()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                Firebase.firestore.collection("users")
                    //.document(it).collection("friends")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val userName = document.id
                            userList.add(userName)
                        }
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Какому другу отправить трек?")
                            .setItems(userList.toTypedArray()) { dialog, which ->
                                val selectedUser = userList[which]

                                //onItemClicked(selectedUser)

                                val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                                Log.d("currentUser", "${currentUser}")
                                sendTokenToServer(selectedUser, currentUser!!)

                                Toast.makeText(
                                    requireContext(),
                                    "Вы выбрали: $selectedUser",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .setNegativeButton("Отмена") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Ошибка загрузки пользователей",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Перемотка аудиотрека на новое положение
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Остановка обновления позиции ползунка во время перемотки
                handler.removeCallbacks(updateSeekBarRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Возобновление обновления позиции ползунка после завершения перемотки
                handler.post(updateSeekBarRunnable)
            }
        })
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = timeInMillis / 1000 / 60
        val seconds = timeInMillis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }


    private fun sendTokenToServer(userId: String, currentUser: String){
        val url = "https://ktor-server-n1ro.onrender.com/save-userId"
        val body = TokenData(userId =  userId, currentUser = currentUser)

        val jsonBody = Gson().toJson(body)
        val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Время ожидания подключения
            .writeTimeout(30, TimeUnit.SECONDS)   // Время ожидания записи данных
            .readTimeout(30, TimeUnit.SECONDS)    // Время ожидания чтения данных
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Tag", "call: $call, e: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Tag", "Токен успешно отправлен на сервер")
            }
        })
    }


    private fun onItemClicked(userId: String) {
        val storageRef = Firebase.storage.reference
        val fileRef = storageRef.child("uploads/${uri.lastPathSegment}")
        val uploadTask = fileRef.putFile(uri)

        val progressBar: ProgressBar = view?.findViewById(R.id.progressBar) ?: return
        progressBar.visibility = View.VISIBLE

        uploadTask.addOnFailureListener {
            Toast.makeText(requireContext(), "failure", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(requireContext(), "file success upload", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE

            fileRef.downloadUrl.addOnSuccessListener { uri ->
                saveMusicUrl(userId, uri.toString())
                Toast.makeText(requireContext(), "USERID AND URI success upload", Toast.LENGTH_SHORT).show()
            }

        }.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
            progressBar.progress = progress.toInt()
        }
    }

    private fun saveMusicUrl(userId: String, musicUrl: String) {
        val db = Firebase.firestore
        val musicData = hashMapOf(
            "musicUrl" to musicUrl
        )

        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result){

                Log.d("MusicPlayerFragment", document.id)
                if (document.getString("name").toString() == userId){
                    Log.d("MusicPlayerFragment", "///////////////////////////: ${document.id}")
                    Log.d("MusicPlayerFragment", "///////////////////////////: ${document.getString("name").toString()}")

                    db.collection("users").document(document.id)
                        .collection("music").add(musicData)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Music URL saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error saving music URL", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacks(updateSeekBarRunnable) // Остановите обновление SeekBar
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        handler.removeCallbacks(updateSeekBarRunnable) // Остановите обновление SeekBar
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}