package com.example.dalleralpha1_0_0.home

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import com.example.dalleralpha1_0_0.R
class AdFragment : DialogFragment() {

    private lateinit var skipButton: Button
    private lateinit var videoView: VideoView
    private lateinit var time: TextView
    private var countdownTime = 10 // 倒计时初始值，单位：秒
    private val countdownHandler = Handler(Looper.getMainLooper())
    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (countdownTime > 0) {
                countdownTime--
                time.text = "$countdownTime" // 更新按钮文字
                countdownHandler.postDelayed(this, 1000) // 每秒更新
            } else {
                // 倒计时结束，显示按钮
                skipButton.visibility = View.VISIBLE
                countdownHandler.removeCallbacks(this) // 停止更新
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false // 防止点击外部关闭对话框
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fraagment_ad, container, false)

        videoView = view.findViewById(R.id.videoView)
        skipButton = view.findViewById(R.id.close)
        time = view.findViewById(R.id.time)

        // 设置 VideoView 播放资源
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/raw/sample_video")
//        val videoUri = Uri.parse("https://www.youtube.com/watch?v=DOxelTKlTcc&list=RDDOxelTKlTcc&start_radio=1")
        videoView.setVideoURI(videoUri)

        // 准备完成时播放视频
        videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true // 设置循环播放
            videoView.start()           // 开始播放
        }

        // 初始化按钮：隐藏并设置倒计时
        skipButton.visibility = View.GONE
        countdownHandler.post(countdownRunnable)

        // 按钮点击事件
        skipButton.setOnClickListener {
            dismiss() // 关闭对话框
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 清除 Handler 防止内存泄漏
        countdownHandler.removeCallbacks(countdownRunnable)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT,900)
            setBackgroundDrawable(ColorDrawable(Color.parseColor("#ABE87B"))) // 设置背景颜色
        }
    }
}
