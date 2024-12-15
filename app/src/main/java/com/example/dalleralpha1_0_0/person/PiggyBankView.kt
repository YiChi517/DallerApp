package com.example.dalleralpha1_0_0.person

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.example.dalleralpha1_0_0.R

class PiggyBankView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var totalScore = 0f // 當前分數
    private val maxScore = 2000f // 將滿分基準改為 2000
    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.YELLOW // 設定為金幣顏色
        strokeWidth = 4f     // 設定線條寬度
    }
    private val path = Path() // 用於遮罩的小豬撲滿外框路徑

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
        return totalScore * 0.02f // 以分數的 1.0 倍計算捐贈金額
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 測試分數
//        setScore(100)

        // 繪製小豬撲滿背景
        val piggyBank = ResourcesCompat.getDrawable(resources, R.drawable.piggybank, null)
        piggyBank?.setBounds(0, 0, width, height)
        piggyBank?.draw(canvas)

        // 創建 Bitmap，並從小豬撲滿的 Drawable 中提取形狀
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(bitmap)
        piggyBank?.draw(tempCanvas)

        // 根據 Bitmap 的透明區域生成 Path
        path.reset()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (bitmap.getPixel(x, y) != 0) { // 檢查像素是否不透明
                    path.addCircle(x.toFloat(), y.toFloat(), 1f, Path.Direction.CW)
                }
            }
        }

        // 裁剪畫布，僅在遮罩內繪製內容
        canvas.save()
        canvas.clipPath(path)

        // 根據分數動態繪製黃色矩形
        if (totalScore > 0) {
            val heightPerScore = (height.toFloat() / maxScore).coerceAtLeast(1f) // 確保每分對應的高度至少為 1 像素
            val currentHeight = height - (totalScore * heightPerScore) // 計算黃色線條的最高位置

            if (currentHeight >= 0) { // 確保繪製範圍在畫布內
                canvas.drawRect(
                    0f,                // 左邊界
                    currentHeight,     // 上邊界
                    width.toFloat(),   // 右邊界
                    height.toFloat(),  // 下邊界
                    paint              // 畫筆
                )
            }
        }

        // 恢復畫布
        canvas.restore()
    }



}
