package com.hfad.musicplayerapplication.presentation.paging

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.deezer.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: DeezerApiService by lazy {
        retrofit.create(DeezerApiService::class.java)
    }
}