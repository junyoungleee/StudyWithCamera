package com.parklee.studywithcam.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.parklee.studywithcam.R
import com.parklee.studywithcam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var running : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
        initTimerButton()
    }

    fun initTimerButton() {
        binding.timerButton.setOnClickListener {
            if (!running) {
                running = true
                binding.timerButton.background = ContextCompat.getDrawable(applicationContext, R.drawable.timer_play_background)
            } else {
                running = false
                binding.timerButton.background = ContextCompat.getDrawable(applicationContext, R.drawable.timer_stop_background)
            }
        }
    }

    fun initNavigationBar() {
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
        supportFragmentManager.beginTransaction().replace(binding.frameLayout.id, fragment).commit()
    }
}