package com.example.dalleralpha1_0_0.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R
import com.example.dalleralpha1_0_0.api.Question


class LevelFragment(private var levelid:String,private var reward:Int) : Fragment() {

    private var questions: ArrayList<Question>? = null
    private var currentQuestionIndex = 0
    private  var right_Answer = 0
    private  var wrong_Answer = 0
    private var levelNumber = 1

    lateinit var A1Button:Button
    lateinit var A2Button:Button
    lateinit var A3Button:Button
    lateinit var A4Button:Button
    lateinit var answer:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        questions = arguments?.getParcelableArrayList("questions")
        levelNumber = arguments?.getInt("levelNumber")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_level, container, false)
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化按鈕
        A1Button = view.findViewById(R.id.A1)
        A2Button = view.findViewById(R.id.A2)
        A3Button = view.findViewById(R.id.A3)
        A4Button = view.findViewById(R.id.A4)

        //顯示第一題
        displayQuestion()
        //判斷選擇的答案是對是錯
        setupAnswerButtons()

        // 點擊按鈕返回到 HomeFragment
        val back = view.findViewById<ImageButton>(R.id.back)
        back.setOnClickListener {
            val menuActivity = activity as? MenuActivity
            menuActivity?.replaceFragment(HomeFragment())
            menuActivity?.showBottomNavigation()
            menuActivity?.showToolbar()
        }
    }

    override fun onResume() { //放需要數據刷新的東西
        super.onResume()
        (activity as? MenuActivity)?.hideToolbar()
    }

    //顯示題目內容
    private fun displayQuestion() {
        questions?.let { questionsList ->
            if (currentQuestionIndex < questionsList.size) {
                val question = questionsList[currentQuestionIndex]
                view?.findViewById<TextView>(R.id.title_content)?.text = question.questionsnumber
                view?.findViewById<TextView>(R.id.content)?.text = question.questionText
                A1Button.text = question.options1
                A2Button.text = question.options2
                A3Button.text = question.options3
                A4Button.text = question.options4
                answer = question.correctAnswer
            }
        }
    }

    // 設定按鈕的點擊監聽器
    fun setupAnswerButtons() {
        val buttons = listOf(A1Button, A2Button, A3Button, A4Button)

        buttons.forEach { button ->
            button.setOnClickListener {
                val isCorrect = button == findButtonByAnswer(answer)

                if (isCorrect) {
                    val goodDialog = GoodFragment.newInstance(::loadNextQuestion)
                    goodDialog.show(childFragmentManager, "GoodDialog") // 顯示GoodDialog
                    button.setBackgroundResource(R.drawable.correct_button_background) // 將正確答案按鈕變綠
                    right_Answer ++
                } else {
                    val badDialog = BadFragment.newInstance(::loadNextQuestion)
                    badDialog.show(childFragmentManager, "BadDialog") // 顯示BadDialog
                    button.setBackgroundResource(R.drawable.wrong_button_background) // 點錯選項的按鈕變紅
                    findButtonByAnswer(answer).setBackgroundResource(R.drawable.correct_button_background) // 正確答案按鈕變綠
                    wrong_Answer ++
                }

                // Disable all buttons except the one that was clicked
                buttons.forEach { it.isEnabled = false }
                button.isEnabled = true // Re-enable only the selected button
            }
        }
    }

    // 根據答案的名稱找到對應的按鈕
    fun findButtonByAnswer(answer: String): Button {
        return when (answer) {
            A1Button.text.toString() -> A1Button
            A2Button.text.toString() -> A2Button
            A3Button.text.toString() -> A3Button
            A4Button.text.toString() -> A4Button
            else -> throw IllegalArgumentException("Invalid answer")
        }
    }

    // 載入下一題
    fun loadNextQuestion() {
        currentQuestionIndex++
        if (currentQuestionIndex < (questions?.size ?: 0)) {
            displayQuestion()
            resetButtonColors()  // 重置按鈕顏色
        } else {
            //作答結束
            navigateToResultPage()
        }
    }

    // 重置按鈕顏色
    @SuppressLint("PrivateResource")
    private fun resetButtonColors() {
        val buttons = listOf(A1Button, A2Button, A3Button, A4Button)
        buttons.forEach { button ->
            button.isEnabled = true  // 重新啟用按鈕
            button.setBackgroundResource(R.drawable.login_textinput_4)
        }
    }

    // 跳轉到結算頁面
    private fun navigateToResultPage() {
        val menuActivity = activity as? MenuActivity
        if (right_Answer > (questions?.size ?: 0)/2) {
            menuActivity?.replaceFragment(SuccessFragment(right_Answer,levelid,levelNumber,reward))
        }else{
            menuActivity?.replaceFragment(FailFragment(wrong_Answer))
        }
    }
}



