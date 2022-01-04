package com.parklee.studywithcam.view.ui.statistic

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.model.entity.DayStudys
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.graph.LineMarkerView
import com.parklee.studywithcam.view.graph.TimeAxisValueFormat
import com.parklee.studywithcam.viewmodel.ServerViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticDayFragment : Fragment() {

    // 오늘의 공부량
    private var clockFormat = ClockFormat()
    lateinit var todayTV: TextView
    lateinit var beforeTV: TextView
    lateinit var beforeGapTV: TextView
    lateinit var dayMiddleTV: TextView
    lateinit var dayGapConTV: TextView

    // 끈기 그래프
    private lateinit var graphData: DayStudys
    private var chartData = ArrayList<Entry>()
    private var lineDataSet = ArrayList<ILineDataSet>()
    private var lineData: LineData = LineData()
    lateinit var chart: LineChart

    private lateinit var uid: String
    private lateinit var serverVM: ServerViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_statistic_day, container, false)

        todayTV = view.findViewById(R.id.statistic_today_tv)
        beforeTV = view.findViewById(R.id.statistic_before_tv)
        beforeGapTV = view.findViewById(R.id.statistic_before_gap_tv)
        dayMiddleTV = view.findViewById(R.id.statistic_day_middle)
        dayGapConTV = view.findViewById(R.id.statistic_day_context_tv)
        setTodayStatistic()

        uid = SWCapplication.pref.getUid("uid")

//        serverVM = ViewModelProvider(this).get(ServerViewModel::class.java) // Test
//        serverVM.getDailyGraphData(uid, getTodayDate()) // Test

        chart = view.findViewById(R.id.statistic_chart)
        initChartData()
        initChart()

        return view
    }

    // 시간 세팅 메서드
    private fun setTodayStatistic() {
        var cSec = SWCapplication.pref.getPrefTime("cTime")
        todayTV.text = clockFormat.calSecToString(cSec)

        var avgSec = 0 // DB에서 가져와야 할 값
        beforeTV.text = clockFormat.calSecToString(avgSec)

        var gap = cSec - avgSec
        if (gap > 0) {
            // 현재 누적 공부시간이 더 많은 경우
            beforeGapTV.text = clockFormat.calSecToKorean(gap)
            dayMiddleTV.text = getString(R.string.statistic_graph_middle_moreless)
            dayGapConTV.text = getString(R.string.statistic_graph_more)
        } else if (gap == 0) {
            // 현재 누적 시간과 이달 평균 시간이 같은 경우
            beforeGapTV.text = ""
            dayMiddleTV.text = getString(R.string.statistic_graph_middle_same)
            dayGapConTV.text = getString(R.string.statistic_graph_same)
        } else {
            // 이달 평균 시간이 더 많은 경우
            gap = Math.abs(gap)
            beforeGapTV.text = clockFormat.calSecToKorean(gap)
            dayMiddleTV.text = getString(R.string.statistic_graph_middle_moreless)
            dayGapConTV.text = getString(R.string.statistic_graph_less)
        }
    }

    // 차트 데이터 초기화 메서드
    private fun initChartData() {
        chartData.add(Entry(-240f, 0f))
        chartData.add(Entry((6*60).toFloat(), 0f))
        chartData.add(Entry((6*60+20).toFloat(), 1f))
        chartData.add(Entry((6*60+40).toFloat(), 2f))
        chartData.add(Entry((6*60+60).toFloat(), 3f))
        chartData.add(Entry((6*60+80).toFloat(), 2f))
        chartData.add(Entry((6*60+100).toFloat(), 0f))
        chartData.add(Entry((1200).toFloat(), 0f))

        var set = LineDataSet(chartData, "set1")
        set.lineWidth = 2F
        set.setDrawValues(false)
        set.highLightColor = Color.TRANSPARENT
        set.mode = LineDataSet.Mode.STEPPED

        lineDataSet.add(set)
        lineData = LineData(lineDataSet)
    }

    // 차트 초기화 메서드
    private fun initChart() {
        chart.run {
            setDrawGridBackground(false)
            setBackgroundColor(Color.WHITE)
            legend.isEnabled = false
        }

        val xAxis = chart.xAxis
        xAxis.setDrawLabels(true)  // Label 표시 여부
        xAxis.axisMaximum = 1200f  // 60min * 24hour
        xAxis.axisMinimum = -240f
        xAxis.labelCount = 5
        xAxis.valueFormatter = TimeAxisValueFormat()

        xAxis.textColor = Color.BLACK
        xAxis.position = XAxis.XAxisPosition.BOTTOM  // x축 라벨 위치
        xAxis.setDrawLabels(true)  // Grid-line 표시
        xAxis.setDrawAxisLine(true)  // Axis-Line 표시

        // 왼쪽 y축 값
        val yLAxis = chart.axisLeft
        yLAxis.axisMaximum = 4.5f   // y축 최대값(고정)
        yLAxis.axisMinimum = -0.5f  // y축 최소값(고정)

        // 왼쪽 y축 도메인 변경
        val yAxisVals = ArrayList<String>(Arrays.asList("F", "C", "B", "A", "A+"))
        yLAxis.valueFormatter = IndexAxisValueFormatter(yAxisVals)
        yLAxis.granularity = 1f

        // 오른쪽 y축 값
        val yRAxis = chart.axisRight
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val marker = LineMarkerView(requireContext(), R.layout.graph_marker)
        marker.chartView = chart
        chart.marker = marker

        chart!!.description.isEnabled = false  // 설명
        chart!!.data = lineData

        chart!!.invalidate()  // 다시 그리기
    }

    private fun getTodayDate(): String {
        var date = Date(System.currentTimeMillis())
        var todayDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return todayDateFormat.format(date)
    }

}