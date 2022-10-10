package com.example.deliveryappproject

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {
    val endpoint :Endpoint by lazy {
        val gson: Gson = GsonBuilder().setLenient().create()
        Retrofit.Builder().baseUrl("https://dapp.loca.lt"). addConverterFactory(
            GsonConverterFactory.create(gson)). build().create(Endpoint::class.java)
    }
}