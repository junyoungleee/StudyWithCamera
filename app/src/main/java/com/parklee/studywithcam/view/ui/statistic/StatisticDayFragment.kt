package com.parklee.studywithcam.view.ui.statistic

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.FragmentStatisticDayBinding
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.graph.LineMarkerView
import com.parklee.studywithcam.view.graph.TimeAxisValueFormat
import com.parklee.studywithcam.viewmodel.ServerViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticDayFragment : Fragment() {

    private var _binding: FragmentStatisticDayBinding? = null
    private val binding get() = _binding!!

    // 끈기 그래프
    private var studyData = ArrayList<Entry>()
    private var drowsinessData = ArrayList<Entry>()
    private var spaceOutData = ArrayList<Entry>()

    private var lineDataSet = ArrayList<ILineDataSet>()
    private lateinit var lineData: LineData

    // 시간 비율 그래프
    private var timePieData = ArrayList<PieEntry>()
    private lateinit var pieDataSet: PieDataSet
    private lateinit var pieData: PieData

    private lateinit var uid: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticDayBinding.inflate(inflater, container, false)

        uid = SWCapplication.pref.getUid("uid")

        initTimeChart()
        initPercentageChart()
        initPercentageChartView()

        return binding.root
    }


    // 차트 데이터 초기화 메서드
    private fun initTimeChartData() {
        studyData.add(Entry(-240f, 0f))
        studyData.add(Entry((6 * 60).toFloat(), 2f))
        studyData.add(Entry((6 * 60 + 20).toFloat(), 1f))
        studyData.add(Entry((6 * 60 + 40).toFloat(), 2f))
        studyData.add(Entry((6 * 60 + 100).toFloat(), 1f))
        studyData.add(Entry((6 * 60 + 110).toFloat(), 2f))
        studyData.add(Entry((6 * 60 + 120).toFloat(), 0f))
        studyData.add(Entry((1200).toFloat(), 0f))

        spaceOutData.add(Entry(-240f, 0f))
        spaceOutData.add(Entry((6 * 60 + 20).toFloat(), 1f))
        spaceOutData.add(Entry((6 * 60 + 40).toFloat(), 0f))

        drowsinessData.add(Entry(-240f, 0f))
        drowsinessData.add(Entry((6 * 60 + 100).toFloat(), 1f))
        drowsinessData.add(Entry((6 * 60 + 110).toFloat(), 0f))

        val studyDataSet = LineDataSet(studyData, "onStudy")
        studyDataSet.apply {
            lineWidth = 2F
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.mainOrangeTrans)
            color = ContextCompat.getColor(requireContext(), R.color.mainOrange)
            mode = LineDataSet.Mode.STEPPED
        }

        val spaceOutDataSet = LineDataSet(spaceOutData, "spaceOut")
        spaceOutDataSet.apply {
            lineWidth = 2F
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.purple200Trans)
            color = ContextCompat.getColor(requireContext(), R.color.purple_200)
            mode = LineDataSet.Mode.STEPPED
        }

        val drowsinessDataSet = LineDataSet(drowsinessData, "drowsiness")
        drowsinessDataSet.apply {
            lineWidth = 2F
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
            fillColor = ContextCompat.getColor(requireContext(), R.color.teal200Trans)
            color = ContextCompat.getColor(requireContext(), R.color.teal_200)
            mode = LineDataSet.Mode.STEPPED
        }

        lineDataSet.add(studyDataSet)
        lineDataSet.add(spaceOutDataSet)
        lineDataSet.add(drowsinessDataSet)
        lineData = LineData(lineDataSet)
    }

    // 차트 초기화 메서드
    private fun initTimeChart() {
        initTimeChartData()

        binding.studyChart.run {
            setDrawGridBackground(false)
            setBackgroundColor(Color.WHITE)
            legend.isEnabled = false
            isDragYEnabled = false
        }

        with(binding.studyChart.xAxis) {
            setDrawLabels(true)  // Label 표시 여부
            axisMaximum = 1200f  // 60min * 24hour
            axisMinimum = -240f
            labelCount = 5
            valueFormatter = TimeAxisValueFormat()

            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM  // x축 라벨 위치
            setDrawLabels(true)  // Grid-line 표시
            setDrawAxisLine(true)  // Axis-Line 표시
        }

        // 왼쪽 y축 값
        with(binding.studyChart.axisLeft) {
            axisMaximum = 2.2f   // y축 최대값(고정)
            axisMinimum = 0.1f  // y축 최소값(고정)

            // 왼쪽 y축 도메인 변경
            val yAxisVals = ArrayList<String>(listOf("-", "X", "O"))
            valueFormatter = IndexAxisValueFormatter(yAxisVals)
            granularity = 1f
        }

        // 오른쪽 y축 값
        with(binding.studyChart.axisRight) {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        val marker = LineMarkerView(requireContext(), R.layout.graph_marker)
        marker.chartView = binding.studyChart
        binding.studyChart.marker = marker

        binding.studyChart.description.isEnabled = false  // 설명
        binding.studyChart.data = lineData

        binding.studyChart.invalidate()  // 다시 그리기
    }

    private fun initPercentageChartData() {
        timePieData.add(PieEntry(80f, requireActivity().getString(R.string.state_study)))
        timePieData.add(PieEntry(15f, requireActivity().getString(R.string.state_drowsiness)))
        timePieData.add(PieEntry(5f, requireActivity().getString(R.string.state_space_out)))

        pieDataSet = PieDataSet(timePieData, "Time Percentage")
        val pieColor = arrayListOf<Int>(
            ContextCompat.getColor(requireActivity(), R.color.mainOrange),
            ContextCompat.getColor(requireActivity(), R.color.teal_200),
            ContextCompat.getColor(requireActivity(), R.color.purple_200))
        with(pieDataSet) {
            sliceSpace = 3f
            colors = pieColor
        }
        pieData = PieData(pieDataSet)
        with(pieData) {
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }
    }

    private fun initPercentageChart() {
        initPercentageChartData()

        binding.studyTimeChart.run {
            transparentCircleRadius = 95f
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)

            legend.isEnabled = false
            data = pieData
        }


    }

    private fun initPercentageChartView() {
        with(binding.layoutTimeStudy) {
            tvTimeTitle.text = getString(R.string.state_study)
            tvTime.text = "00 : 00 : 00"
        }
        with(binding.layoutTimeDrowsiness) {
            tvTimeTitle.text = getString(R.string.state_drowsiness)
            tvTime.text = "00 : 00 : 00"
        }
        with(binding.layoutTimeSpaceOut) {
            tvTimeTitle.text = getString(R.string.state_space_out)
            tvTime.text = "00 : 00 : 00"
        }
    }


    private fun getTodayDate(): String {
        var date = Date(System.currentTimeMillis())
        var todayDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return todayDateFormat.format(date)
    }

}