package com.example.capstonebangkit.api

import retrofit2.Call
import retrofit2.http.GET


interface UserService {
    @get:GET("posts/")
    val allUsers: Call<List<UserResponse?>?>?
}