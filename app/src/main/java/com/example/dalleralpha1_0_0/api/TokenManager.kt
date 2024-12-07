package com.example.dalleralpha1_0_0.api

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context?) {
    private val prefs: SharedPreferences?

    init {
        if (context == null) {
            // 處理 context 為 null 的情況，可以選擇拋出異常或初始化為默認的 SharedPreferences
            throw IllegalArgumentException("Context cannot be null")
        }
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    // 保存 Token
    fun saveToken(token: String) {
        prefs?.edit()?.putString("auth_token", token)?.apply()
    }

    // 獲取 Token
    fun getToken(): String? {
        return prefs?.getString("auth_token", null)
    }

    // 清除 Token
    fun clearToken() {
        prefs?.edit()?.remove("auth_token")?.apply()
    }
}