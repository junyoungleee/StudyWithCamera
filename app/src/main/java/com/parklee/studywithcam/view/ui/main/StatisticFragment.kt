package com.parklee.studywithcam.view.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.graph.TimeAxisValueFormat
import com.parklee.studywithcam.view.graph.LineMarkerView
import com.parklee.studywithcam.view.ui.statistic.StatisticDayFragment
import com.parklee.studywithcam.view.ui.statistic.StatisticMonthFragment
import com.parklee.studywithcam.view.ui.statistic.StatisticWeekFragment
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    lateinit var statisticFrameLayout: FrameLayout
    lateinit var dayButton: TextView
    lateinit var weekButton: TextView
    lateinit var monthButton: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view =  inflater.inflate(R.layout.fragment_statistic, container, false)

        statisticFrameLayout = view.findViewById(R.id.statistic_frame_layout)
        dayButton = view.findViewById(R.id.statistic_day_btn_tv)
        weekButton = view.findViewById(R.id.statistic_week_btn_tv)
        monthButton = view.findViewById(R.id.statistic_month_btn_tv)
        initFrameLayout()

        return view
    }

    private fun initFrameLayout() {
        dayButton.isSelected = true
        weekButton.isSelected = false
        monthButton.isSelected = false
        changeStatisticFragment(StatisticDayFragment())

        dayButton.setOnClickListener {
            dayButton.isSelected = true
            weekButton.isSelected = false
            monthButton.isSelected = false
            changeStatisticFragment(StatisticDayFragment())
        }
        weekButton.setOnClickListener {
            dayButton.isSelected = false
            weekButton.isSelected = true
            monthButton.isSelected = false
            changeStatisticFragment(StatisticWeekFragment())
        }
        monthButton.setOnClickListener {
            dayButton.isSelected = false
            weekButton.isSelected = false
            monthButton.isSelected = true
            changeStatisticFragment(StatisticMonthFragment())
        }
    }

    private fun changeStatisticFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.statistic_frame_layout, fragment).commit()
    }





}