package com.parklee.studywithcam.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.parklee.studywithcam.R
import com.parklee.studywithcam.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    lateinit var binding: ActivityMenuBinding
    lateinit var auth: FirebaseAuth
    lateinit var user: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        user = auth?.currentUser!!

        initAccountLinear()
    }

    private fun initAccountLinear() {
        binding.accountIdTv.text = user!!.email.toString()
//        binding.nicknameButton.text = user!!.uid.toString()

        // 로그아웃
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, R.string.logout_toast, Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // 탈퇴
        binding.withdrawalButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.withdrawal_title)
            builder.setMessage(R.string.withdrawal_context)
            builder.setPositiveButton("탈퇴할래요!") { dialogInterface, i ->
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.withdrawal_toast, Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                }
            }
            builder.setNegativeButton("아니요!") { dialogInterface, i ->

            }
            builder.show()
        }
    }
}