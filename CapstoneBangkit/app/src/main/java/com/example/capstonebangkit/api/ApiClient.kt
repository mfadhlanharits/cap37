package com.example.capstonebangkit.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit: Retrofit
        private get() {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val okHttpClient: OkHttpClient =
                OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
            return Retrofit.Builder()
                .baseUrl("https://arcane-dawn-09638.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        val userService: UserService
        get() = retrofit.create(UserService::class.java)
}