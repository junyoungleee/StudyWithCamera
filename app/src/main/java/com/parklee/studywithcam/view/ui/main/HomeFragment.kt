package com.parklee.studywithcam.view.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.ui.MenuActivity

class HomeFragment : Fragment() {

    lateinit var nTimeTextView: TextView
    lateinit var cTimeTextView: TextView
    private var clockFormat = ClockFormat()

    lateinit var auth: FirebaseAuth
    lateinit var menuButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        nTimeTextView = view.findViewById(R.id.home_now_tv)
        cTimeTextView = view.findViewById(R.id.home_cumul_tv)

        menuButton = view.findViewById(R.id.menu_button)
        menuButton.setOnClickListener {
            startActivity(Intent(requireContext(), MenuActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()

        return view
    }

    override fun onResume() {
        super.onResume()

        var nSec = SWCapplication.pref.getPrefTime("nTime")
        var cSec = SWCapplication.pref.getPrefTime("cTime")

        nTimeTextView.text = clockFormat.calSecToString(nSec)
        cTimeTextView.text = clockFormat.calSecToString(cSec)
    }

}