package com.example.dalleralpha1_0_0

import android.app.Application
import com.example.dalleralpha1_0_0.api.TokenManager

class MyApplication : Application() {
    companion object {
        lateinit var tokenManager: TokenManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        tokenManager = TokenManager(this) // 初始化 TokenManager
    }
}