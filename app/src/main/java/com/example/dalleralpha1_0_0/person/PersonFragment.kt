package com.example.dalleralpha1_0_0.person

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dalleralpha1_0_0.R
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.Info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class PersonFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 獲取 PiggyBankView 和 DonationTextView 的參考
        val piggyBankView = view.findViewById<PiggyBankView>(R.id.piggy_bank_view)
        val donationTextView = view.findViewById<TextView>(R.id.donation_amount)

        // 獲取 ImageButton 並設置點擊事件
        val editButton = view.findViewById<ImageButton>(R.id.imageButton2)
        editButton.setOnClickListener {
            showEditDialog(view)
        }

        // 初始分數設定
        piggyBankView.setScore(0) // 初始分數

        // 使用 API 請求來動態更新分數與捐贈金額
        Api.infoService.getInfo().enqueue(object : Callback<Info> {
            override fun onResponse(call: Call<Info>, response: Response<Info>) {
                if (response.isSuccessful && response.body() != null) {
                    val info = response.body()!!

                    // 更新用戶名稱
                    val userName = view.findViewById<TextView>(R.id.user_name)
                    userName?.text = info.username

                    // 更新 PiggyBankView 的分數
                    val score = info.score // 從 API 獲取分數
                    piggyBankView.setScore(score)

                    // 計算捐贈金額並更新文字
                    updateDonationAmount(piggyBankView, donationTextView)
                    Log.d("API Response", "Username: ${info.username}")
                } else {
                    Log.e(
                        "fetchReward",
                        "API 回應不正確: ${response.code()} - ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<Info>, t: Throwable) {
                Log.e("fetchReward", "請求失敗: $t")
            }
        })
    }

    // 根據 PiggyBankView 的分數更新捐贈金額文字
    private fun updateDonationAmount(piggyBankView: PiggyBankView, donationTextView: TextView) {
        val donationAmount = piggyBankView.getDonationAmount() // 計算捐贈金額
        donationTextView.text = String.format(
            Locale.getDefault(),
            "已捐贈：%.2f 新台幣",
            donationAmount
        )
    }

    // 顯示編輯對話框的方法
    private fun showEditDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)

        // 獲取對話框中的元件
        val nameEditText = dialogView.findViewById<EditText>(R.id.edit_user_name)
        val roleGrid = dialogView.findViewById<GridLayout>(R.id.role_image_grid)

        // 預設填入目前的使用者名稱
        val currentNameTextView = view.findViewById<TextView>(R.id.user_name)
        val currentUserName = currentNameTextView.text.toString()
        nameEditText.setText(currentUserName)

        var selectedRoleId: Int? = null // 用於記錄選擇的角色 ID

        // Toolbar 內的右上角小圖示
        val cornerImageView = activity?.findViewById<ImageView>(R.id.corner_image)

        // 引用上方大圖片
        val topImageView = view.findViewById<ImageView>(R.id.top_image)

        // 設置每個圖片的點擊事件
        for (i in 0 until roleGrid.childCount) {
            val roleImage = roleGrid.getChildAt(i) as ImageView

            // 保存原始的 padding 和 margin
            val originalPadding = roleImage.paddingTop
            val originalLayoutParams = roleImage.layoutParams as ViewGroup.MarginLayoutParams

            roleImage.setOnClickListener {
                // 清除所有圖片的選中背景並恢復原始大小
                for (j in 0 until roleGrid.childCount) {
                    val img = roleGrid.getChildAt(j) as ImageView
                    img.background = null
                    img.setPadding(originalPadding, originalPadding, originalPadding, originalPadding)
                    val params = img.layoutParams as ViewGroup.MarginLayoutParams
                    params.setMargins(
                        originalLayoutParams.leftMargin,
                        originalLayoutParams.topMargin,
                        originalLayoutParams.rightMargin,
                        originalLayoutParams.bottomMargin
                    )
                    img.layoutParams = params
                }

                // 設置當前選中圖片的背景
                roleImage.setBackgroundResource(R.drawable.login_textinput)
                selectedRoleId = roleImage.id
            }
        }

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
            .setTitle("編輯使用者資訊")
            .setPositiveButton("確認") { dialog, _ ->
                // 獲取使用者輸入的名稱，若為空則保留原本名稱
                val newName = nameEditText.text.toString().takeIf { it.isNotBlank() } ?: currentUserName

                // 根據選擇的角色 ID 獲取角色名稱和圖片
                val (selectedRole, selectedImageRes) = when (selectedRoleId) {
                    R.id.role_1 -> "偵探小達" to R.drawable.daller
                    R.id.role_2 -> "紳士嘶嘶" to R.drawable.snake
                    R.id.role_3 -> "博士芙芙" to R.drawable.bat
                    R.id.role_4 -> "特務阿來" to R.drawable.frog
                    R.id.role_5 -> "間蝶Q摸" to R.drawable.butterfly
                    else -> view.findViewById<TextView>(R.id.user_role).text.toString() to null
                }

                // 更新 UI
                currentNameTextView.text = newName
                val userRoleTextView = view.findViewById<TextView>(R.id.user_role)
                userRoleTextView.text = selectedRole

                // 更新圖片
                selectedImageRes?.let {
                    topImageView.setImageResource(it) // 上方大圖片
                    cornerImageView?.setImageResource(it) // Toolbar 右上角小圖示
                }

                dialog.dismiss()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



}
