package com.example.phonebookapp

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AvatarApiService {
    @GET("{avatarKey}.png")
    fun getAvatar(@Path("avatarKey") avatarKey: String): Call<ResponseBody>
}

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.multiavatar.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: AvatarApiService = retrofit.create(AvatarApiService::class.java)
}