package com.parklee.studywithcam.view.ui

import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.ActivityMainBinding
import com.parklee.studywithcam.view.ui.main.GroupFragment
import com.parklee.studywithcam.view.ui.main.HomeFragment
import com.parklee.studywithcam.view.ui.main.StatisticFragment
import com.parklee.studywithcam.viewmodel.ServerViewModel
import org.tensorflow.lite.Interpreter
import java.util.*
import kotlin.collections.HashMap
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var serverVM: ServerViewModel  // Test


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationBar()
        initStartButton()

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val width = size.x
        val height = size.y

        SWCapplication.pref.setWidth("width", width)
        SWCapplication.pref.setHeight("height", height)

        modelDownLoad()
    }

    // 시작 버튼 초기화
    private fun initStartButton() {
        binding.timerButton.setOnClickListener {

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

    private fun modelDownLoad() {
        binding.loadingAnimation.visibility = View.VISIBLE
        binding.timerButton.isClickable = false

        var drowsiness = false
        var understanding = false

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        // 졸음 측정 AI 모델 다운로드 & 업데이트
        FirebaseModelDownloader.getInstance()
            .getModel("Drowsiness-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnCompleteListener {
                drowsiness = true
                if (understanding) {
                    Log.d("지금 상황1", "$drowsiness & $understanding")
                    binding.loadingAnimation.visibility = View.GONE
                    binding.timerButton.isClickable = true
                }
            }
            .addOnSuccessListener { model ->
                drowsiness = true
                val modelFile = model?.file
                if (modelFile != null) {
                    Log.d("model path", model!!.file!!.path.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "졸음 측정 AI 업그레이드 실패 : $it", Toast.LENGTH_SHORT).show()
            }

        // 이해도 측정 AI 모델 다운로드 & 업데이트
        FirebaseModelDownloader.getInstance()
            .getModel("Understanding-Detector", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
            .addOnCompleteListener {
                understanding = true
                if (drowsiness) {
                    Log.d("지금 상황2", "$drowsiness & $understanding")
                    binding.loadingAnimation.visibility = View.GONE
                    binding.timerButton.isClickable = true
                }
            }
            .addOnSuccessListener { model ->
                understanding = true
                val modelFile = model?.file
                if (modelFile != null) {
                    Log.d("model path", model!!.file!!.path.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "이해도 측정 AI 업그레이드 실패 : $it", Toast.LENGTH_SHORT).show()
            }
    }

}