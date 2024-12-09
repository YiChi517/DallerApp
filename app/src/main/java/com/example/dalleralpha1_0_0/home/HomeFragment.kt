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

        // 呼叫 Activity 的方法更新 Toolbar
        val menuActivity = activity as? MenuActivity
        menuActivity?.fetchReward()

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

    private fun fetchRewardAndUserLevel(){
        Api.infoService.getInfo().enqueue(object : Callback<Info>{
            override fun onResponse(call: Call<Info>, response: Response<Info>) {
                if (response.isSuccessful && response.body() != null) {
                    val info = response.body()!!
                    val userLevel = info.level // 獲取用戶最高通關等級
                    val number = Regex("\\d+").find(userLevel)?.value?.toInt() ?: 0
                    updateButtonStates(number)
                } else {
                    Log.d("fetch&Reward", "API 回應不正確: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Info>, t: Throwable) {
                Log.d("fetch&Reward", "請求失敗: $t")
            }
        })
        Log.d("HomeFragment", "fetchReward called")
    }

    private fun fetchLevelInformation(levelId: String) {
        Api.levelInformationService.getInformation(levelId).enqueue(object : Callback<Information> {
            override fun onResponse(call: Call<Information>, response: Response<Information>) {
                if (response.isSuccessful && response.body() !=null) {
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
                        Log.d("API Error", "No questions found in response")
                    }
                } else {
                    // 處理API錯誤
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string()
                    Log.d("fetchLevelInformation", "Error code: $errorCode, Error body: $errorBody")
                }
            }
            override fun onFailure(call: Call<Information>, t: Throwable) {
                // 處理請求錯誤
                Log.e("fetchLevelInformation", "請求失敗: $t")
            }
        })
    }
    private fun updateButtonStates(userLevel: Int) {
        buttons.forEachIndexed { index, button ->
            val isUnlocked = index + 1 <= userLevel + 1
            val isCleared = index + 1 <= userLevel

            button.isEnabled = isUnlocked
            button.setBackgroundResource(
                when {
                    isCleared -> R.drawable.section_btm_y // 通關背景
                    isUnlocked -> R.drawable.section_btm_gray // 解鎖背景
                    else -> R.drawable.section_btm_locked // 鎖定背景
                }
            )
        }
    }
    override fun onResume() { //放需要數據刷新的東西
        super.onResume()
        fetchRewardAndUserLevel() // 更新 Reward&UserLevel
        Log.d("HomeFragment", "Calling updateClearedButtonDrawable...")
    }
    private fun updateClearedButtonDrawable() {
        val sharedPreferences = requireContext().getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
        val isLevelCleared = sharedPreferences.getBoolean("levelCleared", false)
        val clearedLevelNumber = sharedPreferences.getInt("clearedLevelNumber", -1)

        if (isLevelCleared && clearedLevelNumber > 0 && clearedLevelNumber <= totalLevels) {
            val clearedButton = buttons[clearedLevelNumber - 1] // 找到对应的按钮
            clearedButton.setBackgroundResource(R.drawable.section_btm_y) // 修改按钮的 drawable

            // 如果不需要保留通关标记，可以重置状态
            val editor = sharedPreferences.edit()
            editor.putBoolean("levelCleared", false)
            editor.apply()
        }
    }
}




