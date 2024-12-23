package com.example.dalleralpha1_0_0.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.Information
import com.example.dalleralpha1_0_0.api.Question
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StartFragment : Fragment() {

    private var information: Information? = null
    private var levelId: String? = null
    private var levelNumber: Int? = null
    private var reward = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        information = arguments?.getParcelable("information")
        levelId = arguments?.getString("levelId")
        levelNumber = arguments?.getInt("levelNumber")
        reward = arguments?.getInt("reward")!!

        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //帶入關卡資訊到layout
        information?.let { info ->
            view.findViewById<TextView>(R.id.center).text = info.levelNumber.toString()
            view.findViewById<TextView>(R.id.content).text = info.gameTitle
            view.findViewById<TextView>(R.id.award_contnet).text = info.reward.toString()
            //判斷難度，更改layout顯示畫面
            when (info.difficulty){
                2.toString() -> { view.findViewById<ImageView>(R.id.imageView7).setImageResource(R.drawable.star_full) }
                3.toString() -> {
                    view.findViewById<ImageView>(R.id.imageView7).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.star_full)
                }
                4.toString() -> {
                    view.findViewById<ImageView>(R.id.imageView7).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView10).setImageResource(R.drawable.star_full)
                }
                5.toString() -> {
                    view.findViewById<ImageView>(R.id.imageView7).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView9).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView10).setImageResource(R.drawable.star_full)
                    view.findViewById<ImageView>(R.id.imageView12).setImageResource(R.drawable.star_full)
                }
                else -> {
                    // 處理無效值
                    Log.e("DifficultyError", "Invalid difficulty: ${info.difficulty}")
                }
            }
        }

        //取得第一關level.1的題目
        val start = view.findViewById<Button>(R.id.start)
        Log.d("ViewType", "start is of type: ${start::class.java.name}")
        start.setOnClickListener {
            fetchQuestions(levelId!!)
        }
    }

    override fun onResume() { //放需要數據刷新的東西
        super.onResume()
        (activity as? MenuActivity)?.hideToolbar()
    }

    private fun fetchQuestions(levelId: String) {
        Api.levelService.getQuestionsByLevel(levelId).enqueue(object : Callback<List<Question>> {
                override fun onResponse(
                    call: Call<List<Question>>,
                    response: Response<List<Question>>
                ) {
                    if (response.isSuccessful) {
                        val questions = response.body()
                        // 將問題列表封裝到 Bundle 中，key是"questions"
                        if (questions != null) {
                            val bundle = Bundle().apply {
                                putParcelableArrayList("questions", ArrayList(questions))
                                putInt("levelNumber",levelNumber!!)
                            }
                            // 導航到 LevelFragment 並傳遞數據
                            val levelFragment = LevelFragment(levelId,reward).apply {
                                arguments = bundle
                            }
                            val menuActivity = activity as? MenuActivity
                            menuActivity?.hideBottomNavigation()
                            //替換成關卡的頁面
                            menuActivity?.replaceFragment(levelFragment)
                            Log.d("API Success", "Received ${questions.size} questions")
                        } else {
                            // 如果 questions 為 null，顯示錯誤訊息
                            Log.e("API Error", "No questions found in response")
                        }
                    } else {
                        // 處理失敗回應，檢查狀態碼並轉換錯誤訊息為字串
                        val errorCode = response.code()
                        val errorBody = response.errorBody()?.string() // 轉為字串
                        Toast.makeText(requireContext(), "發生未預期的錯誤", Toast.LENGTH_SHORT)
                            .show()
                        Log.d("API Error", "Error code: $errorCode, Error body: $errorBody")
                    }
                }

                override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                    // 處理請求錯誤的情況
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong $t",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("API Failure", t.toString())
                }
            })
    }
}

