package com.example.dalleralpha1_0_0.person

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.dalleralpha1_0_0.R

class PiggyBankView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var totalScore = 0f // 當前分數
    private val maxScore = 2000f // 滿分基準
    private val paint = Paint().apply {
        isAntiAlias = true
    }

    /**
     * 設置分數，直接設定分數，而非累加
     */
    fun setScore(score: Int) {
        this.totalScore = score.toFloat() // 直接設定分數
        if (this.totalScore > maxScore) {
            this.totalScore = maxScore // 確保不超過最大分數
        }
        invalidate() // 重新繪製
    }

    /**
     * 計算捐贈金額
     */
    fun getDonationAmount(): Float {
        return totalScore * 0.02f // 以分數的 0.02 倍計算捐贈金額
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 繪製小豬撲滿背景
        val piggyBank = ResourcesCompat.getDrawable(resources, R.drawable.piggybank, null)
        piggyBank?.setBounds(0, 0, width, height)
        piggyBank?.draw(canvas)

        // 根據分數計算金幣高度
        val goldDrawable = ResourcesCompat.getDrawable(resources, R.drawable.gold, null)
        val goldHeight = ((totalScore / maxScore) * height).toInt() // 計算金幣高度
        goldDrawable?.setBounds(
            0,
            height - goldHeight.coerceAtLeast(0), // 確保最小值為 0
            width,
            height
        )
        goldDrawable?.draw(canvas)
    }
}
