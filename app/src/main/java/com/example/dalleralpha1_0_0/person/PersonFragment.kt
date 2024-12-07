package com.example.dalleralpha1_0_0.person

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        // 模擬分數變動
        simulateScoreChanges(piggyBankView, donationTextView)
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

    // 模擬分數變動的測試方法
    private fun simulateScoreChanges(piggyBankView: PiggyBankView, donationTextView: TextView) {
        // 模擬分數
        piggyBankView.setScore(1000)
        updateDonationAmount(piggyBankView, donationTextView)
    }
}
