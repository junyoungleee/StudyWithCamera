package com.parklee.studywithcam.view.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.ui.MenuActivity
import java.time.LocalDate

class HomeFragment : Fragment() {

    lateinit var nTimeTextView: TextView
    lateinit var cTimeTextView: TextView

    lateinit var auth: FirebaseAuth
    lateinit var menuButton: ImageButton

    private lateinit var getResult: ActivityResultLauncher<Intent>

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

        getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val nTime = it.data!!.getIntExtra("nTime", 0)
            val cTime = it.data!!.getIntExtra("cTime", 0)

            val todayStudy = DailyStudy(LocalDate.now().toString(), nTime)
            // DB 저장
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        var nSec = SWCapplication.pref.getPrefTime("nTime")
        var cSec = SWCapplication.pref.getPrefTime("cTime")

        nTimeTextView.text = ClockFormat.calSecToString(nSec)
        cTimeTextView.text = ClockFormat.calSecToString(cSec)
    }

}


