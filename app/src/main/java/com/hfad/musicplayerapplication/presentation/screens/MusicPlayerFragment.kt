package com.hfad.musicplayerapplication.presentation.screens

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
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialContainerTransform
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.hfad.musicplayerapplication.R
import com.hfad.musicplayerapplication.databinding.FragmentMusicPlayerBinding
import com.hfad.musicplayerapplication.di.TokenData
import com.hfad.musicplayerapplication.domain.entity.Track
import com.hfad.musicplayerapplication.presentation.services.MusicPlayerService
import com.hfad.musicplayerapplication.presentation.viewmodels.SharedViewModel
import com.squareup.picasso.Picasso
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

    private val progressReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currentPosition = intent.getLongExtra("CURRENT_POSITION", 0L)
            val duration =
                intent.getLongExtra("DURATION", 1L)  // по умолчанию 1, чтобы избежать деления на 0

            if (duration > 0) {
                val progress = (currentPosition * 100 / duration).toInt()
                binding.progressBar.progress = progress
                binding.currentTime.text = formatTime(currentPosition.toInt())
                binding.totalTime.text = formatTime(duration.toInt())
            }
        }
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = timeInMillis / 1000 / 60
        val seconds = timeInMillis / 1000 % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    //private lateinit var seekBar: SeekBar
    private lateinit var updateSeekBarRunnable: Runnable
    private lateinit var handler: Handler

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //uri = MusicPlayerFragmentArgs.fromBundle(it).mp3
//            bitmapImage = MusicPlayerFragmentArgs.fromBundle(it).bitmap
//            title = MusicPlayerFragmentArgs.fromBundle(it).title
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

//        val track = Track(
//            id = 1,
//        title = "",
//         duration = 1,
//        link = "",
//         preview = "",
//         md5_image = "",
//         cover_xl = "",
//            artist = "",
//         uri = uri
//        )
//        val intent = MusicPlayerService.newIntent(requireContext(), track)
//        requireContext().startService(intent)

        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(progressReceiver, IntentFilter("PROGRESS_UPDATE_ACTION"))

        //handler = Handler(Looper.getMainLooper()) // Инициализация Handler

        _binding = FragmentMusicPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onStart() {
        super.onStart()

        val componentName = ComponentName(requireContext(), MusicPlayerService::class.java)
        val sessionToken = SessionToken(requireContext(), componentName)

        val controllerFuture = MediaController.Builder(requireContext(), sessionToken).buildAsync()
        controllerFuture.addListener(
            {

                val controller = controllerFuture.get()

                // Настройка кнопок паузы и воспроизведения
                binding.imageButtonPause.setOnClickListener {
                    controller.pause()
                    it.visibility = View.GONE
                    binding.imageButtonPlay.visibility = View.VISIBLE
                    //handler.removeCallbacks(updateSeekBarRunnable) // Остановить обновление SeekBar
                }

                binding.imageButtonPlay.setOnClickListener {
                    controller.play()
                    it.visibility = View.GONE
                    binding.imageButtonPause.visibility = View.VISIBLE
                    //startSeekBarUpdate(controller) // Запуск обновления SeekBar
                }



//                binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//                    override fun onProgressChanged(
//                        seekBar: SeekBar?,
//                        progress: Int,
//                        fromUser: Boolean
//                    ) {
//                        controller.seekTo(progress.toLong())
//                    }
//
//                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                        handler.removeCallbacks(updateSeekBarRunnable)
//                    }
//
//                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                        handler.post(updateSeekBarRunnable)
//                    }
//
//                })

            },
            MoreExecutors.directExecutor()
        )
    }

    private fun startSeekBarUpdate(controller: MediaController) {
        updateSeekBarRunnable = object : Runnable {
            override fun run() {
                val currentPosition = controller.currentPosition
                binding.progressBar.progress = currentPosition.toInt() // Обновите SeekBar
                binding.currentTime.text = formatTime(currentPosition.toInt()) // Обновите время
                handler.postDelayed(this, 1000) // Обновление каждую секунду
            }
        }
        handler.post(updateSeekBarRunnable) // Запускаем обновление
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

                Log.d(
                    "MY_TAG_URI",
                    "title $title, albumArt $albumArt, bitmap: $bitmap trackUri ${track.uri}"
                )


                Palette.from(bitmap!!).generate { palette ->
                    val dominantColor = palette?.getDominantColor(Color.WHITE)
                    val vibrantColor = palette?.getVibrantColor(Color.BLACK)
                    val mutedColor = palette?.getMutedColor(Color.BLACK)


                    binding.layoutFragmentMusicPlayer.setBackgroundColor(dominantColor!!)

                }


                binding.songTitle.text = title
                binding.artistName.text = artist

                Glide.with(requireContext())
                    .load(bitmap)  // передаем URI альбома
                    //.placeholder(R.drawable.ic_launcher_background) // изображение-заглушка
//                .error(R.drawable.error_image) // изображение, если ошибка
                    .into(binding.albumCover) // целевой ImageView
            }
            else{
                Picasso.get()
                    .load("https://e-cdns-images.dzcdn.net/images/cover/${track.md5_image}/1000x1000.jpg")
                    .into(binding.albumCover)

                binding.songTitle.text = track.title
                binding.artistName.text = track.artist

                Picasso.get()
                    .load("https://e-cdns-images.dzcdn.net/images/cover/${track.md5_image}/1000x1000.jpg")
                    .into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            // Используй Bitmap здесь

                            Palette.from(bitmap!!).generate { palette ->
                                val dominantColor = palette?.getDominantColor(Color.WHITE)
                                val vibrantColor = palette?.getVibrantColor(Color.BLACK)
                                val mutedColor = palette?.getMutedColor(Color.BLACK)

                                binding.layoutFragmentMusicPlayer.setBackgroundColor(dominantColor!!)
                            }
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                            // Обработка ошибки
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                            // Подготовка к загрузке
                        }
                    })
            }


        }

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

    }



    /*
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            Log.d("MAIN_TAG", "$bitmapImage")
    //        if (bitmapImage != null) {
    //            binding.albumCover.setImageBitmap(bitmapImage)
    //        }
    //        else{
                binding.albumCover.setImageResource(R.drawable.ic_launcher_background)
            //}

            binding.songTitle.text = "title"
            //seekBar = view.findViewById(R.id.progressBar)
            //handler = Handler(Looper.getMainLooper())

            ///////

            Log.d("MY_TAG", "uri $uri")
            Log.d("MY_TAG", "scheme ${uri.scheme}")

    //        if (uri.scheme == "content"){
    //            try {
    //                val contentResolver = requireContext().contentResolver
    //
    //                Log.d("MY_TAG", "contentResolver $contentResolver")
    //                val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")
    //
    //                Log.d("MY_TAG", "fileDescriptor $fileDescriptor")
    //                if (fileDescriptor != null) {
    //                    mediaPlayer = MediaPlayer().apply {
    //                        setDataSource(fileDescriptor.fileDescriptor) // Используйте fileDescriptor
    //                        prepare()
    //                        start()
    //                        seekBar.max = duration // Установите максимальное значение SeekBar
    //
    //                        updateSeekBarRunnable = object : Runnable {
    //                            override fun run() {
    //                                seekBar.progress = currentPosition // Обновите прогресс SeekBar
    //                                handler.postDelayed(this, 1000) // Обновляйте каждую секунду
    //                            }
    //                        }
    //                        handler.post(updateSeekBarRunnable) // Запустите обновление SeekBar
    //                    }
    //                    fileDescriptor.close()
    //                } else {
    //                    Log.e("MUSIC_PLAYER", "Failed to open file descriptor for URI: $uri")
    //                }
    //            } catch (e: IOException) {
    //                Log.e("MUSIC_PLAYER", "Error setting data source: ${e.message}")
    //            }
    //        }
    //        if (uri.scheme == "https")
    //        {
    //           // mediaPlayer.release()
    //
    //            mediaPlayer = MediaPlayer().apply {
    //                setDataSource(uri.toString())
    //                prepare()
    //                start()
    //
    //                seekBar.max = duration
    //                updateSeekBarRunnable = object : Runnable {
    //                    override fun run() {
    //                        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
    //                            seekBar.progress = mediaPlayer.currentPosition // Обновите прогресс SeekBar
    //                        }
    //
    //                       // seekBar.progress = currentPosition // Обновите прогресс SeekBar
    //                        handler.postDelayed(this, 1000) // Обновляйте каждую секунду
    //                    }
    //                }
    //                handler.post(updateSeekBarRunnable)
    //            }
    //        }


    //        val currentTime = binding.currentTime
    //        val totalTime = binding.totalTime
    //
    //        totalTime.text = formatTime(mediaPlayer.duration)
    //
    //        updateSeekBarRunnable = object : Runnable {
    //            override fun run() {
    //                seekBar.progress = mediaPlayer.currentPosition
    //                currentTime.text = formatTime(mediaPlayer.currentPosition)
    //                handler.postDelayed(this, 1000) // Обновляем каждую секунду
    //            }
    //        }

    //        binding.imageButtonPause.setOnClickListener {
    //            if(mediaPlayer.isPlaying){
    //                mediaPlayer.pause()
    //                handler.removeCallbacks(updateSeekBarRunnable)
    //                binding.imageButtonPause.visibility = View.GONE
    //                binding.imageButtonPlay.visibility = View.VISIBLE
    //            }
    //        }
    //
    //        binding.imageButtonPlay.setOnClickListener {
    //            if (!mediaPlayer.isPlaying){
    //                mediaPlayer.start()
    //                handler.post(updateSeekBarRunnable)
    //                binding.imageButtonPause.visibility = View.VISIBLE
    //                binding.imageButtonPlay.visibility = View.GONE
    //            }
    //        }

    //        mediaPlayer.setOnCompletionListener {
    //            Toast.makeText(requireContext(), "Track finished", Toast.LENGTH_SHORT).show()
    //            mediaPlayer.seekTo(0) // Вернуться в начало трека
    //            seekBar.progress = 0
    //            handler.removeCallbacks(updateSeekBarRunnable) // Остановить обновление
    //        }

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

    //        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    //            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    //                if (fromUser) {
    //                    // Перемотка аудиотрека на новое положение
    //                    mediaPlayer.seekTo(progress)
    //                }
    //            }
    //
    //            override fun onStartTrackingTouch(seekBar: SeekBar?) {
    //                // Остановка обновления позиции ползунка во время перемотки
    //                handler.removeCallbacks(updateSeekBarRunnable)
    //            }
    //
    //            override fun onStopTrackingTouch(seekBar: SeekBar?) {
    //                // Возобновление обновления позиции ползунка после завершения перемотки
    //                handler.post(updateSeekBarRunnable)
    //            }
    //        })
        }

        private fun formatTime(timeInMillis: Int): String {
            val minutes = timeInMillis / 1000 / 60
            val seconds = timeInMillis / 1000 % 60
            return String.format("%02d:%02d", minutes, seconds)
        }

*/
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

/*
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
    //        if (::mediaPlayer.isInitialized) {
    //            mediaPlayer.release()
    //        }
            //handler.removeCallbacks(updateSeekBarRunnable) // Остановите обновление SeekBar
        }

        override fun onDestroy() {
            super.onDestroy()
    //        if (this::mediaPlayer.isInitialized) {
    //            mediaPlayer.release()
    //        }
            //handler.removeCallbacks(updateSeekBarRunnable) // Остановите обновление SeekBar
        }

    */

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(progressReceiver)
        _binding = null
    }
}