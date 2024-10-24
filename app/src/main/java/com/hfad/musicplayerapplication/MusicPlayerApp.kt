package com.hfad.musicplayerapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.gson.Gson
import com.hfad.musicplayerapplication.di.TokenData
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


@HiltAndroidApp
class MusicPlayerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful){
                return@addOnCompleteListener
            }

            val token = task.result

            val auth = FirebaseAuth.getInstance()
            val userId = auth.currentUser?.uid ?: ""


            sendTokenToServer(token, userId)
        }
    }

    private fun sendTokenToServer(token: String, userId: String){
        val url = "https://ktor-server-n1ro.onrender.com/save-token"
        val body = TokenData(token, userId)

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

        Log.d("Tag", "jsonBody для отправки: $jsonBody")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Tag", "call: $call, e: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("Tag", "Токен успешно отправлен на сервер")
            }
        })
    }
}