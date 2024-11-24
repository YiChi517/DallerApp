package com.example.dalleralpha1_0_0.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R

class FailFragment(val wrongAnswer:Int) : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.center).text = wrongAnswer.toString()

        // 點擊獲取獎勵返回到HomeFragment，ToolBar的鑽石會增加(這個還沒寫)
        val back = view.findViewById<Button>(R.id.back)
        back.setOnClickListener {
            val menuActivity = activity as? MenuActivity
            menuActivity?.replaceFragment(HomeFragment())
            menuActivity?.showBottomNavigation()
        }
    }

}