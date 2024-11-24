package com.example.dalleralpha1_0_0.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.example.dalleralpha1_0_0.MenuActivity
import com.example.dalleralpha1_0_0.R

class BadFragment : DialogFragment() {

    var onNextClicked: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bad, container, false)

        view.findViewById<Button>(R.id.next).setOnClickListener {
            onNextClicked?.invoke()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.BOTTOM) // 從底部彈出
            setBackgroundDrawable(ColorDrawable(Color.RED)) //背景設為紅色
        }
    }

    companion object {
        fun newInstance(onNextClicked: () -> Unit): BadFragment {
            val fragment = BadFragment()
            fragment.onNextClicked = onNextClicked
            return fragment
        }
    }
}