package com.example.dalleralpha1_0_0.person

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.dalleralpha1_0_0.MainActivity
import com.example.dalleralpha1_0_0.R
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.Info
import com.example.dalleralpha1_0_0.api.RegisterRequest
import com.example.dalleralpha1_0_0.api.RegisterResponse
import com.example.dalleralpha1_0_0.api.UpdateNameRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class PersonFragment : Fragment() {

    private  lateinit var cornerImageView :ImageView
    private  lateinit var topImageView :ImageView
    private  lateinit var userRoleTextView :TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Toolbar 內的右上角小圖示
        cornerImageView = activity?.findViewById(R.id.corner_image) ?: throw IllegalStateException("Activity is null")
        // 引用上方大圖片
        topImageView = view.findViewById(R.id.top_image)
        //角色名稱
        userRoleTextView = view.findViewById(R.id.user_role)

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

                    //更新角色名稱(小字)
                    userRoleTextView.text=info.headphoto

                    // 更新大圖片
                    when (info.headphoto) {
                        "偵探小達" -> topImageView.setImageResource(R.drawable.daller)
                        "紳士嘶嘶" -> topImageView.setImageResource(R.drawable.snake)
                        "特務阿來" -> topImageView.setImageResource(R.drawable.frog)
                        "博士芙芙" -> topImageView.setImageResource(R.drawable.bat)
                        "間蝶Q摸" -> topImageView.setImageResource(R.drawable.butterfly)
                    }

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
        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener{
            logout()
        }
    }
    private fun logout() {
        // 清除 SharedPreferences 中的用戶數據
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // 啟動 MainActivity，並清空返回棧
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
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
                userRoleTextView.text = selectedRole

                // 更新圖片
                selectedImageRes?.let {
                    topImageView.setImageResource(it) // 上方大圖片
                    cornerImageView.setImageResource(it) // Toolbar 右上角小圖示
                }

                dialog.dismiss()

                Api.updateNameService.updateNamePhoto(UpdateNameRequest(newName,selectedRole)).enqueue(object:retrofit2.Callback<Void>{
                    override fun onResponse(call: Call<Void>, response: Response<Void>)
                    {
                        if (response.isSuccessful){
                            Toast.makeText(context,"更新成功",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Toast.makeText(context,"更新失敗",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(context,"Api更新失敗",Toast.LENGTH_SHORT).show()
                        Log.e("fail",t.toString())
                    }

                })
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }



}
