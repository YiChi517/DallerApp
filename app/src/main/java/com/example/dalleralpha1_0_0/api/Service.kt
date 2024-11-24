package com.example.dalleralpha1_0_0.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path


private const val url = "http://192.168.31.221:9674/v1/"
private val okHttp = OkHttpClient.Builder()

interface LoginService {
    @POST("users/login")
    fun login(@Body LoginRequest: LoginRequest): Call<LoginResponse>
}

interface LevelService {
    // 獲取特定關卡問題
    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImxpdHRsZWRhQGdtYWlsLmNvbSIsInN1YiI6IkRvbGxhckF1dGhlbnRpY2F0aW9uIiwiaXNzIjoiZG9sbGFyIFNlcnZlciJ9.E6-zdWk67cR7zd06htj-SaEGQsIprVXH3H4Pm9No6edMlKi2JPhgJxP1TYUDOC3PYsjqPxcPv2eEMW947aNjhA", "Content-Type: application/json")
    @GET("games/levels/{levelId}/questions")
    fun getQuestionsByLevel(@Path("levelId") levelId: String): Call<List<Question>>

}

interface LevelInformationService {
    // 獲取關卡資訊
    @Headers("Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6ImxpdHRsZWRhQGdtYWlsLmNvbSIsInN1YiI6IkRvbGxhckF1dGhlbnRpY2F0aW9uIiwiaXNzIjoiZG9sbGFyIFNlcnZlciJ9.E6-zdWk67cR7zd06htj-SaEGQsIprVXH3H4Pm9No6edMlKi2JPhgJxP1TYUDOC3PYsjqPxcPv2eEMW947aNjhA", "Content-Type: application/json")
    @GET("games/levels/{levelId}")
    fun getInformation(@Path("levelId") levelId: String): Call<Information>
}

object Api {
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(url)
        .client(okHttp.build())
        .build()
    val retrofitService: LoginService = retrofit.create(LoginService::class.java) //登入
    val levelService: LevelService = retrofit.create(LevelService::class.java) //打題目
    val levelInformationService: LevelInformationService = retrofit.create(LevelInformationService::class.java) //打關卡資訊
}

