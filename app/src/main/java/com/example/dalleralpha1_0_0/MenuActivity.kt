package com.example.dalleralpha1_0_0

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.dalleralpha1_0_0.api.Api
import com.example.dalleralpha1_0_0.api.Info
import com.example.dalleralpha1_0_0.home.HomeFragment
import com.example.dalleralpha1_0_0.person.PersonFragment
import com.example.dalleralpha1_0_0.practice.PracticeFragment
import com.example.dalleralpha1_0_0.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MenuActivity : AppCompatActivity() {

    private lateinit var rewardTextView: TextView
    private lateinit var levelTextView: TextView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE) // 確保不顯示標題
        setContentView(R.layout.activity_menu)

        // 初始化 Toolbar 的 TextView
        toolbar = findViewById<Toolbar>(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        rewardTextView = toolbar.findViewById(R.id.reward)
        levelTextView = toolbar.findViewById(R.id.level)

        // 可選：如果需要隱藏標題
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // 如果需要，啟動時更新數據
        fetchReward()

        // 設置初始的 Fragment
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentContainerHome, HomeFragment()).commit()

        val nav=findViewById<BottomNavigationView>(R.id.navigation)
        nav.setOnNavigationItemSelectedListener(listener)

        Log.d("MenuActivity", "ActionBar: ${supportActionBar}")
    }
    // 更新 Toolbar 上的 Reward
    fun updateToolbarReward(reward: Int,level:String) {
        rewardTextView.text = reward.toString()
        levelTextView.text = level
    }

    fun fetchReward() {
        Api.infoService.getInfo().enqueue(object : Callback<Info> {
            override fun onResponse(call: Call<Info>, response: Response<Info>) {
                if (response.isSuccessful && response.body() != null) {
                    val info = response.body()!!
                    updateToolbarReward(info.score,info.level) // 更新 Toolbar
                } else {
                    Log.e("MenuActivity", "API 回應不正確: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Info>, t: Throwable) {
                Log.e("MenuActivity", "請求失敗: $t")
            }
        })
    }
    private var listener = object :BottomNavigationView.OnNavigationItemSelectedListener {
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.home -> {
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerHome, HomeFragment()).commit()
                    return true
                }
                R.id.person -> {
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerHome, PersonFragment()).commit()
                    return true
                }

                R.id.search -> {
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerHome, SearchFragment()).commit()
                    return true
                }
            }
        return true
        }
    }

    //定義一個方法用來替換 Fragment
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerHome, fragment) // 替換 Fragment
            .addToBackStack(null) // 添加到回退堆疊，允許返回上一個 Fragment
            .commit()
    }

    // 顯示 BottomNavigationView
    fun showBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.navigation).visibility = View.VISIBLE
    }

    // 隱藏 BottomNavigationView
    fun hideBottomNavigation() {
        findViewById<BottomNavigationView>(R.id.navigation).visibility = View.GONE
    }

    // 方法：隱藏 Toolbar
    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    // 方法：顯示 Toolbar
    fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }
}

