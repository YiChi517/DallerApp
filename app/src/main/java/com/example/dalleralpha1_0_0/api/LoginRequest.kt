package com.example.dalleralpha1_0_0.api

import com.google.gson.annotations.SerializedName


data class LoginRequest(
    @SerializedName("email")var email:String,
    @SerializedName("password")var password:String
)
