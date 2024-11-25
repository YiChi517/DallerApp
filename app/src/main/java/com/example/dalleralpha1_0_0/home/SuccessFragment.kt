package com.example.dalleralpha1_0_0.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R

class SuccessFragment(private val rightAnswer:Int, private var currentLevel: Int) : Fragment() {

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

        // 點擊獲取獎勵，ToolBar的鑽石會增加(這個還沒寫)
        val back = view.findViewById<Button>(R.id.back)
        back.setOnClickListener {
            // 解鎖下一關（例如：將下一關的解鎖狀態設為 true）
            val sharedPreferences = requireContext().getSharedPreferences("GameProgress", Context.MODE_PRIVATE)
            val nextLevel = currentLevel.plus(1)
            if (nextLevel <= 4) {
                sharedPreferences.edit().putBoolean("level$nextLevel" + "Unlocked", true).apply()
            }
            //返回HomeFragment
            val menuActivity = activity as? MenuActivity
            menuActivity?.replaceFragment(HomeFragment())
            menuActivity?.showBottomNavigation()
        }
    }
}