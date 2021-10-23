package com.parklee.studywithcam.view.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication

class HomeFragment : Fragment() {

    lateinit var nTimeTextView: TextView
    lateinit var cTimeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        nTimeTextView = view.findViewById(R.id.home_now_tv)
        cTimeTextView = view.findViewById(R.id.home_cumul_tv)

        return view
    }

    override fun onResume() {
        super.onResume()

        var nSec = SWCapplication.pref.getPrefTime("nTime")
        var cSec = SWCapplication.pref.getPrefTime("cTime")

        nTimeTextView.text = calSecToString(nSec)
        cTimeTextView.text = calSecToString(cSec)
    }

    private fun calSecToString(time: Int): String {
        var hour = time / 3600
        var min = (time - (hour*3600)) / 60
        var sec = (time - (hour*3600)) % 60
        return "${makeText(hour)} : ${makeText(min)} : ${makeText(sec)}"
    }

    private fun makeText(t: Int): String {
        if (t < 10) return "0${t}" else return "$t"
    }


}