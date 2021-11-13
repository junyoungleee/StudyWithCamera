package com.parklee.studywithcam.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.parklee.studywithcam.R
import com.parklee.studywithcam.databinding.ActivityMainBinding
import com.parklee.studywithcam.view.ui.main.GroupFragment
import com.parklee.studywithcam.view.ui.main.HomeFragment
import com.parklee.studywithcam.view.ui.main.StatisticFragment
import com.parklee.studywithcam.viewmodel.ServerViewModel
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serverVM: ServerViewModel  // Test

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
        initStartButton()

        serverVM = ViewModelProvider(this).get(ServerViewModel::class.java)  // Test

    }

    // 시작 버튼 초기화
    private fun initStartButton() {
        binding.timerButton.setOnClickListener {
//            serverVM.getDummy() // Test

            val hash = HashMap<String, Any>()
            hash.put("name", "lee")
            hash.put("address", "seoul")
            hash.put("age", 23)
//            serverVM.postDummy(hash)

            val studyIntent = Intent(applicationContext, StudyActivity::class.java)
            startActivity(studyIntent)
        }
    }

    // 네비게이션바 초기화
    private fun initNavigationBar() {
        binding.bottomNavigationBar.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.group_study -> {
                        changeFragment(GroupFragment())
                        binding.timerButton.visibility = View.GONE
                        binding.mainToolbar.visibility = View.GONE
                    }
                    R.id.study_home -> {
                        changeFragment(HomeFragment())
                        binding.timerButton.visibility = View.VISIBLE
                        binding.mainToolbar.visibility = View.VISIBLE
                    }
                    R.id.statistic -> {
                        changeFragment(StatisticFragment())
                        binding.timerButton.visibility = View.GONE
                        binding.mainToolbar.visibility = View.GONE
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