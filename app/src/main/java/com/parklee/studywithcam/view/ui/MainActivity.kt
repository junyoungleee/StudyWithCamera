package com.parklee.studywithcam.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.parklee.studywithcam.R
import com.parklee.studywithcam.view.ui.StudyActivity
import com.parklee.studywithcam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
        initStartButton()

    }

    private fun initStartButton() {
        binding.timerButton.setOnClickListener {
            val studyIntent = Intent(applicationContext, StudyActivity::class.java)
            startActivity(studyIntent)
        }
    }

    // 네비게이션바 세팅
    private fun initNavigationBar() {
        binding.bottomNavigationBar.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.group_study -> {
                        changeFragment(GroupFragment())
                        binding.timerButton.visibility = View.GONE
                    }
                    R.id.study_home -> {
                        changeFragment(HomeFragment())
                        binding.timerButton.visibility = View.VISIBLE
                    }
                    R.id.statistic -> {
                        changeFragment(StatisticFragment())
                        binding.timerButton.visibility = View.GONE
                    }
                }
                true
            }
            selectedItemId = R.id.study_home
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(binding.container.id, fragment).commit()
    }
}