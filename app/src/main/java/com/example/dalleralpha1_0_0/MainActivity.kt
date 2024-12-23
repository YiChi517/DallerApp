package com.example.dalleralpha1_0_0

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.LoginRequest
import com.example.dalleralpha1_0_0.api.LoginResponse
import com.example.dalleralpha1_0_0.api.QuestionsResponse
import com.example.dalleralpha1_0_0.api.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 使用 applicationContext 來初始化 TokenManager
        tokenManager = TokenManager(applicationContext)  // 確保使用正確的 Context

        //連結登入畫面的EditText
        val ac =findViewById<EditText>(R.id.ac_content)
        val pw =findViewById<EditText>(R.id.pw_content)

        val login=findViewById<Button>(R.id.login)
        val register = findViewById<Button>(R.id.register)

        register.setOnClickListener{
            //跳去註冊
            val intent=Intent()
            intent.setClass(this@MainActivity,RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener{
            when {
                ac.text.toString().trim().isBlank() && pw.text.toString().trim().isBlank() -> Toast.makeText(this@MainActivity,"請輸入帳密", Toast.LENGTH_SHORT).show()
                ac.text.toString().trim().isBlank() -> Toast.makeText(this@MainActivity,"請輸入帳號", Toast.LENGTH_SHORT).show()
                pw.text.toString().trim().isBlank() -> Toast.makeText(this@MainActivity,"請輸入密碼", Toast.LENGTH_SHORT).show()
                else -> {
                    //登入api
                    Api.retrofitService.login(LoginRequest(ac.text.toString(),pw.text.toString())).enqueue(object:retrofit2.Callback<LoginResponse>{
                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
                        {
                            if (response.isSuccessful){
                                when (response.body()?.success) {
                                    "true" -> {
                                        val token = response.body()?.message ?: ""
                                        tokenManager.saveToken(token) // 保存 Token
                                        Toast.makeText(this@MainActivity,"登入成功",Toast.LENGTH_SHORT).show()
                                        Log.d("success!", response.body().toString())
                                        //僅跳頁，尚未傳值
                                        val intent=Intent()
                                        intent.setClass(this@MainActivity,MenuActivity::class.java)
                                        startActivity(intent)
                                    }
                                    "false" ->{
                                        Toast.makeText(this@MainActivity,"帳密錯誤，登入失敗",Toast.LENGTH_SHORT).show()
                                        Log.d("success!", response.body().toString())
                                    }else->{
                                    Log.d("failure", "Unknown success value: " + response.body()?.success)
                                }
                                }
                            }
                            else{
                                Toast.makeText(this@MainActivity,"發生未預期的錯誤",Toast.LENGTH_SHORT).show()
                                Log.d("success!", response.body().toString())
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@MainActivity,"Something went wrong $t",Toast.LENGTH_SHORT).show()
                            Log.e("fail",t.toString())
                        }

                    })
                }
            }
        }
    }
}
