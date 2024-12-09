package com.example.dalleralpha1_0_0

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dalleralpha1_0_0.R.id.start
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.LoginRequest
import com.example.dalleralpha1_0_0.api.LoginResponse
import com.example.dalleralpha1_0_0.api.RegisterRequest
import com.example.dalleralpha1_0_0.api.RegisterResponse
import com.example.dalleralpha1_0_0.api.TokenManager
import retrofit2.Call
import retrofit2.Response

class RegisterActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //連結登入畫面的EditText
        val name =findViewById<EditText>(R.id.name_content)
        val ac =findViewById<EditText>(R.id.ac_content)
        val pw =findViewById<EditText>(R.id.pw_content)

        val register= findViewById<Button>(R.id.start)
        val back = findViewById<Button>(R.id.backtologin)

        register.setOnClickListener{
            //打api的時候再打開
            Api.registerService.register(RegisterRequest(ac.text.toString(),name.text.toString(),pw.text.toString())).enqueue(object:retrofit2.Callback<RegisterResponse>{
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>)
                {
                    if (response.isSuccessful){
                        when (response.body()?.success) {
                            "true" -> {
                                Toast.makeText(this@RegisterActivity,"註冊成功", Toast.LENGTH_SHORT).show()
                                Log.d("success!", response.body().toString())
                                //僅跳頁，尚未傳值
                                val intent= Intent()
                                intent.setClass(this@RegisterActivity,MainActivity::class.java)
                                startActivity(intent)
                            }
                            "false" ->{
                                Toast.makeText(this@RegisterActivity,"註冊失敗", Toast.LENGTH_SHORT).show()
                                Log.d("success!", response.body().toString())
                            }else->{
                            Log.d("failure", "Unknown success value: " + response.body()?.success)
                        }
                        }
                    }
                    else{
                        Toast.makeText(this@RegisterActivity,"發生未預期的錯誤", Toast.LENGTH_SHORT).show()
                        Log.d("success!", response.body().toString())
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity,"Something went wrong $t", Toast.LENGTH_SHORT).show()
                    Log.e("fail",t.toString())
                }

            })
        }
        back.setOnClickListener{
            val intent= Intent()
            intent.setClass(this@RegisterActivity,MainActivity::class.java)
            startActivity(intent)
        }
    }
}