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
import com.example.dalleralpha1_0_0.api.Information
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var buttons: List<Button>
    private val totalLevels = 4 // 假設有 4 關


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment製作出一個layout
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //放一次戲的初始化操作
        super.onViewCreated(view, savedInstanceState)

        // 初始化按鈕列表
        buttons = listOf(
            view.findViewById(R.id.level1),
            view.findViewById(R.id.level2),
            view.findViewById(R.id.level3),
            view.findViewById(R.id.level4)
        )

        // 設置按鈕點擊事件
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                fetchLevelInformation("level.${index + 1}")
            }
        }
    }
    private fun fetchReward(){
        Api.infoService.getInfo().enqueue(object : Callback<Info>{
            override fun onResponse(call: Call<Info>, response: Response<Info>) {
                if (response.isSuccessful && response.body() != null) {
                    val info = response.body()!!
                    val reward = view?.findViewById<TextView>(R.id.reward)
                    reward?.text = info.score.toString()
                } else {
                    Log.e("fetchReward", "API 回應不正確: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Info>, t: Throwable) {
                Log.e("fetchReward", "請求失敗: $t")
            }
        })
    }

    private fun fetchLevelInformation(levelId: String) {
        Api.levelInformationService.getInformation(levelId).enqueue(object : Callback<Information> {
            override fun onResponse(call: Call<Information>, response: Response<Information>) {
                if (response.isSuccessful) {
                    val information = response.body()  // 獲取返回的關卡資訊
                    // 將問題列表封裝到 Bundle 中，key是"information"
                    if (information != null) {
                        val bundle = Bundle().apply {
                            putParcelable("information", information)
                            putString("levelId", levelId) // 傳遞 levelId
                            putInt("levelNumber",information.levelNumber) //傳遞levelNumber
                            putInt("reward",information.reward) //傳遞此關通關成功會得到的鑽石數量
                        }
                        // 導航到 StartFragment 並傳遞數據
                        val startFragment = StartFragment().apply {
                            arguments = bundle
                        }
                        //替換成關卡資訊的頁面
                        val menuActivity = activity as? MenuActivity
                        menuActivity?.hideBottomNavigation()
                        menuActivity?.replaceFragment(startFragment)

                        Log.d("fetchLevelInformation", "成功獲取關卡資訊: {$information}")
                    } else {
                        // 如果 questions 為 null，顯示錯誤訊息
                        Log.e("API Error", "No questions found in response")
                    }
                } else {
                    // 處理API錯誤
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    Log.e("fetchLevelInformation", "Error code: $errorCode, Error body: $errorBody")
                }
            }
            override fun onFailure(call: Call<Information>, t: Throwable) {
                // 處理請求錯誤
                Log.e("fetchLevelInformation", "請求失敗: $t")
            }
        })
    }
    private fun updateButtonStates() {
        val sharedPreferences = requireContext().getSharedPreferences("GameProgress", Context.MODE_PRIVATE)

        // 動態檢查各關卡的解鎖狀態
        buttons.forEachIndexed { index, button ->
            val isUnlocked = sharedPreferences.getBoolean("level${index + 1}Unlocked", index == 0)
            button.isEnabled = isUnlocked
        }
    }
    override fun onResume() { //放需要數據刷新的東西
        super.onResume()
        fetchReward() // 更新 reward TextView
        updateButtonStates() // 更新按鈕狀態
    }
}




