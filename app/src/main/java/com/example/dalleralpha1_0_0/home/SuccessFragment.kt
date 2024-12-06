package com.example.dalleralpha1_0_0.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.Info
import com.example.dalleralpha1_0_0.api.UpdateRequest
import com.example.dalleralpha1_0_0.api.UpdateScoreRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuccessFragment(private val rightAnswer:Int,private var levelid:String,private var currentLevel: Int,private var reward:Int) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_success, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        view.findViewById<TextView>(R.id.center).text = rightAnswer.toString()
        view.findViewById<TextView>(R.id.award_contnet).text = reward.toString()

        // 點擊獲取獎勵，ToolBar的鑽石會增加(這個還沒寫)
        val back = view.findViewById<Button>(R.id.back)
        back.setOnClickListener {
            // 解鎖下一關（例如：將下一關的解鎖狀態設為 true）
            val sharedPreferences = requireContext().getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
            val nextLevel = currentLevel.plus(1)
            if (nextLevel <= 4) {
                sharedPreferences.edit().putBoolean("level$nextLevel" + "Unlocked", true).apply()
            }
            //存記錄到rewards這張表
            Api.sendToRewardService.saveTorewards(levelid,UpdateRequest(levelid,reward)).enqueue(object: Callback<Void> { // 如果 API 不返回內容，可以使用 `Void` 作為泛型
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Log.d("sendRequestWithoutResponse", "請求成功，狀態碼: ${response.code()}")
                    } else {
                        Log.e("sendRequestWithoutResponse", "API 回應不正確: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("sendRequestWithoutResponse", "請求失敗: $t")
                }
            })
            //打更新reward數量至info
            Api.infoService.getInfo().enqueue(object : Callback<Info>{
                override fun onResponse(call: Call<Info>, response: Response<Info>) {
                    if (response.isSuccessful && response.body() != null) {
                        val info = response.body()!!
                        var originalReward :Int = info.score
                        //原本的加上這關獲得的
                        originalReward += reward
                        updateScore(originalReward)
                    } else {
                        Log.e("fetchReward", "API 回應不正確: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<Info>, t: Throwable) {
                    Log.e("fetchReward", "請求失敗: $t")
                }
            })
            //返回HomeFragment
            val menuActivity = activity as? MenuActivity
            menuActivity?.replaceFragment(HomeFragment())
            menuActivity?.showBottomNavigation()
        }
    }
    private fun updateScore(score:Int){
        Api.updateScoreService.updateScore(UpdateScoreRequest(score)).enqueue(object: Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("sendRequestWithoutResponse", "請求成功，狀態碼: ${response.code()}")
                } else {
                    Log.e("sendRequestWithoutResponse", "API 回應不正確: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("sendRequestWithoutResponse", "請求失敗: $t")
            }
        })
    }
}