package com.example.dalleralpha1_0_0.api

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


private const val url = "http://192.168.31.221:9674/v1/"

private val okHttp = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor()) // 使用全局的 TokenManager
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(url)
    .client(okHttp)
    .build()

interface LoginService {
    //登入
    @POST("users/login")
    fun login(@Body LoginRequest: LoginRequest): Call<LoginResponse>
}

interface RegisterService {
    //註冊
    @POST("users/register")
    fun register(@Body RegisterRequest: RegisterRequest): Call<RegisterResponse>
}

interface LevelInformationService {
    // 獲取關卡資訊
    @GET("games/levels/{levelId}")
    fun getInformation(@Path("levelId") levelId: String): Call<Information>
}

interface LevelService {
    // 獲取特定關卡問題
    @GET("games/levels/{levelId}/questions")
    fun getQuestionsByLevel(@Path("levelId") levelId: String): Call<List<Question>>
}

interface InfoService {
    // 獲取個人資料
    @GET("users/info")
    fun getInfo(): Call<Info>
}

interface SendToRewardService {
    // 存記錄到rewards這張表
    @POST("games/levels/{levelId}/rewards")
    fun saveTorewards(@Path("levelId") levelId: String,@Body UpdateRequest:UpdateRequest):Call<Void>
}

interface UpdateScoreService {
    // 更新總鑽石數量至info
    @POST("users/update")
    fun updateScore(@Body UpdateScoreRequest:UpdateScoreRequest):Call<Void>
}

interface UpdateNameService {
    // 更新名字頭貼至info
    @POST("users/updateName")
    fun updateNamePhoto(@Body UpdateNameRequest:UpdateNameRequest):Call<Void>
}

object Api {
    val retrofitService: LoginService = retrofit.create(LoginService::class.java) //登入
    val levelInformationService: LevelInformationService = retrofit.create(LevelInformationService::class.java) //打關卡資訊
    val levelService: LevelService = retrofit.create(LevelService::class.java) //打題目
    val infoService : InfoService = retrofit.create(InfoService::class.java) //打關卡資訊
    val sendToRewardService : SendToRewardService = retrofit.create(SendToRewardService::class.java) //存記錄到rewards這張表
    val updateScoreService : UpdateScoreService = retrofit.create(UpdateScoreService::class.java) //更新Info
    val registerService : RegisterService = retrofit.create(RegisterService::class.java) //註冊
    val updateNameService : UpdateNameService = retrofit.create(UpdateNameService::class.java) //更新姓名頭貼
}

