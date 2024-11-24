package com.example.dalleralpha1_0_0

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dalleralpha1_0_0.home.HomeFragment
import com.example.dalleralpha1_0_0.person.PersonFragment
import com.example.dalleralpha1_0_0.practice.PracticeFragment
import com.example.dalleralpha1_0_0.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // 設置初始的 Fragment
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fragmentContainerHome, HomeFragment()).commit()

        val nav=findViewById<BottomNavigationView>(R.id.navigation)
        nav.setOnNavigationItemSelectedListener(listener)
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
                R.id.practice -> {
                    val manager = supportFragmentManager
                    val transaction = manager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerHome, PracticeFragment()).commit()
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
}

