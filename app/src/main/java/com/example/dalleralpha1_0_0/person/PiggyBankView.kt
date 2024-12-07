package com.example.dalleralpha1_0_0.person

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.dalleralpha1_0_0.R

class PiggyBankView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint: Paint = Paint()
    private var totalScore = 0 // 當前分數
    private val maxScore = 2000 // 滿分基準
    private var lineCount = 0 // 水平線數量

    init {
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.YELLOW
        paint.strokeWidth = 5f
    }

    fun setScore(score: Int) {
        this.totalScore = score
        this.lineCount = (totalScore / (maxScore / 10)) // 更新水平線數量
        invalidate() // 重新繪製
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 繪製小豬撲滿（背景圖案）
        val piggyBank = resources.getDrawable(R.drawable.piggy_bank_icon, null)
        piggyBank.setBounds(100, 100, width - 100, height - 100)
        piggyBank.draw(canvas)

        // 動態繪製水平線
        val startY = height - 200 // 起始繪製位置
        val lineSpacing = 50 // 每條水平線的間距

        for (i in 0 until lineCount) {
            val y = startY - (i * lineSpacing)
            canvas.drawLine(150f, y.toFloat(), (width - 150).toFloat(), y.toFloat(), paint)
        }
    }
}
