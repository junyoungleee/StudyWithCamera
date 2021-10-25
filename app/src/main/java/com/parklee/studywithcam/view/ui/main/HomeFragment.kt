package com.parklee.studywithcam.view.ui.main

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.view.format.ClockFormat

class HomeFragment : Fragment() {

    lateinit var nTimeTextView: TextView
    lateinit var cTimeTextView: TextView
    private var clockFormat = ClockFormat()

    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        nTimeTextView = view.findViewById(R.id.home_now_tv)
        cTimeTextView = view.findViewById(R.id.home_cumul_tv)

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