package com.parklee.studywithcam.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.parklee.studywithcam.R
import com.parklee.studywithcam.databinding.ActivityLoginBinding
import com.parklee.studywithcam.databinding.ActivityStudyBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}