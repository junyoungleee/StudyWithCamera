package com.parklee.studywithcam.view.ui.main

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
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
import com.parklee.studywithcam.databinding.FragmentStatisticBinding
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.graph.ColorCalendar
import com.parklee.studywithcam.view.graph.TimeAxisValueFormat
import com.parklee.studywithcam.view.graph.LineMarkerView
import com.parklee.studywithcam.view.ui.statistic.StatisticDayFragment
import com.parklee.studywithcam.view.ui.statistic.StatisticMonthFragment
import com.parklee.studywithcam.view.ui.statistic.StatisticWeekFragment
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val clockFormat = ClockFormat()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        var view = binding.root

        initFrameLayout()
        setCalendarBackground()

        return view
    }

    private fun initFrameLayout() {
        with(binding) {
            dayButton.isSelected = true
            weekButton.isSelected = false
            monthButton.isSelected = false
        }
        timeStatistic(1)
        changeStatisticFragment(StatisticDayFragment())

        binding.dayButton.setOnClickListener {
            with(binding) {
                dayButton.isSelected = true
                weekButton.isSelected = false
                monthButton.isSelected = false
            }
            timeStatistic(1)
            changeStatisticFragment(StatisticDayFragment())
        }

        binding.weekButton.setOnClickListener {
            with(binding) {
                dayButton.isSelected = false
                weekButton.isSelected = true
                monthButton.isSelected = false
            }
            timeStatistic(2)
            changeStatisticFragment(StatisticWeekFragment())
        }

        binding.monthButton.setOnClickListener {
            with(binding) {
                dayButton.isSelected = false
                weekButton.isSelected = false
                monthButton.isSelected = true
            }
            timeStatistic(3)
            changeStatisticFragment(StatisticMonthFragment())
        }
    }

    private fun changeStatisticFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.statistic_frame_layout, fragment).commit()
    }

    // 시간 통계 --------------------------------------------------------------------------------
    private fun timeStatistic(unit: Int) {
        // 1 - 일 / 2 - 주 / 3 - 월

        var thisStudy = 0  // 오늘, 이번 주, 이번 달 공부시간
        var lastStudy = 0  // 어제, 지난 주, 지난 달 공부시간

        when(unit) {
            1 -> {
                thisStudy = SWCapplication.pref.getPrefTime("cTime")
                lastStudy = 0 // DB에서 가져와야 할 값

                with(binding) {
                    timeTitle.text = getString(R.string.statistic_day_title1)
                    timeSubTitle.text = getString(R.string.statistic_day_title2)
                    timeComparedMe.text = getString(R.string.statistic_day_me)
                }
            }
            2 -> {
                with(binding) {
                    thisStudy = 0
                    lastStudy = 0 // DB에서 가져와야 할 값

                    timeTitle.text = getString(R.string.statistic_week_title1)
                    timeSubTitle.text = getString(R.string.statistic_week_title2)
                    timeComparedMe.text = getString(R.string.statistic_week_me)
                }
            }
            3 -> {
                with(binding) {
                    thisStudy = 0
                    lastStudy = 0

                    timeTitle.text = getString(R.string.statistic_month_title1)
                    timeSubTitle.text = getString(R.string.statistic_month_title2)
                    timeComparedMe.text = getString(R.string.statistic_month_me)
                }
            }
            else -> {
                Toast.makeText(context, "선택 오류", Toast.LENGTH_SHORT).show()
            }
        }

        binding.timeNow.text = clockFormat.calSecToString(thisStudy)
        binding.timeBefore.text = clockFormat.calSecToString(lastStudy)

        setTimeStatisticGapText(thisStudy, lastStudy)
    }

    private fun setTimeStatisticGapText(now: Int, before: Int) {
        var gap = now - before
        if (gap > 0) {
            // 현재 누적 공부시간이 더 많은 경우
            binding.timeCompared.text = clockFormat.calSecToKorean(gap)
            binding.timeComparedText1.text = getString(R.string.statistic_graph_middle_moreless)
            binding.timeComparedText2.text = getString(R.string.statistic_graph_more)
        } else if (gap == 0) {
            // 현재 누적 시간과 이달 평균 시간이 같은 경우
            binding.timeCompared.text = getString(R.string.statistic_graph_same_time)
            binding.timeComparedText1.text = getString(R.string.statistic_graph_middle_same)
            binding.timeComparedText2.text = getString(R.string.statistic_graph_same)
        } else {
            // 이달 평균 시간이 더 많은 경우
            gap = Math.abs(gap)
            binding.timeCompared.text = clockFormat.calSecToKorean(gap)
            binding.timeComparedText1.text = getString(R.string.statistic_graph_middle_moreless)
            binding.timeComparedText2.text = getString(R.string.statistic_graph_less)
        }
    }

    // 달력 통계 --------------------------------------------------------------------------------
    private fun setCalendarBackground() {
        var dummyDate1 = DailyStudy("220201", 13000)

        val calList = ArrayList<DailyStudy>()
        calList.add(dummyDate1)
        for (day in calList) {
            binding.calendarView.addDecorators(ColorCalendar(this.requireActivity(), day))
        }

    }

}