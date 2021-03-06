package com.parklee.studywithcam.view.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.FragmentStatisticBinding
import com.parklee.studywithcam.model.entity.DailyStudy
import com.parklee.studywithcam.repository.DatabaseRepository
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.view.graph.ColorCalendar
import com.parklee.studywithcam.view.graph.LineMarkerView
import com.parklee.studywithcam.view.graph.TimeAxisValueFormat
import com.parklee.studywithcam.viewmodel.GraphViewModel
import com.parklee.studywithcam.viewmodel.GraphViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatisticFragment : Fragment() {

    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!

    private val graphVM: GraphViewModel by viewModels {
        GraphViewModelFactory((requireActivity().application as SWCapplication).dbRepository)
    }

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
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        var view = binding.root

        setCalendarBackground()

        uid = SWCapplication.pref.getUid("uid")

        graphVM.setLinearGraphData()
        graphVM.setPieChartData()

        initTimeChart()
        initPercentageChart()

        return view
    }


    // 시간 통계 --------------------------------------------------------------------------------
    private fun timeStatistic(unit: Int) {
        var thisStudy = 0  // 오늘, 이번 주, 이번 달 공부시간
        var lastStudy = 0  // 어제, 지난 주, 지난 달 공부시간

        thisStudy = SWCapplication.pref.getPrefTime("cTime")
        lastStudy = 0 // DB에서 가져와야 할 값

        with(binding) {
            timeTitle.text = getString(R.string.statistic_day_title1)
            timeSubTitle.text = getString(R.string.statistic_day_title2)
            timeComparedMe.text = getString(R.string.statistic_day_me)
        }

        binding.timeNow.text = ClockFormat.calSecToString(thisStudy)
        binding.timeBefore.text = ClockFormat.calSecToString(lastStudy)

        setTimeStatisticGapText(thisStudy, lastStudy)
    }

    private fun setTimeStatisticGapText(now: Int, before: Int) {
        var gap = now - before
        if (gap > 0) {
            // 현재 누적 공부시간이 더 많은 경우
            binding.timeCompared.text = ClockFormat.calSecToKorean(gap)
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
            binding.timeCompared.text = ClockFormat.calSecToKorean(gap)
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

    // ----------------------------------------------------------------------------------------

    // 차트 데이터 초기화 메서드
    private fun initTimeChartData() {
        graphVM.getAllLinearResult.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                studyData = graphVM.getTotalGraphData()
                drowsinessData = graphVM.getDrowsinessGraphData()
                spaceOutData = graphVM.getSpaceOutGraphData()

                Log.d("graph_data_1", "$studyData")
                Log.d("graph_data_2", "$drowsinessData")
                Log.d("graph_data_3", "$spaceOutData")

                val studyDataSet = LineDataSet(studyData, "onStudy")
                val drowsinessDataSet = LineDataSet(drowsinessData, "drowsiness")
                val spaceOutDataSet = LineDataSet(spaceOutData, "spaceOut")

                studyDataSet.apply {
                    lineWidth = 2F
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    fillColor = ContextCompat.getColor(requireContext(), R.color.mainOrangeTrans)
                    color = ContextCompat.getColor(requireContext(), R.color.mainOrange)
                    mode = LineDataSet.Mode.STEPPED
                }

                spaceOutDataSet.apply {
                    lineWidth = 2F
                    setDrawValues(false)
                    setDrawCircles(false)
                    setDrawFilled(true)
                    fillColor = ContextCompat.getColor(requireContext(), R.color.purple200Trans)
                    color = ContextCompat.getColor(requireContext(), R.color.purple_200)
                    mode = LineDataSet.Mode.STEPPED
                }

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
                binding.layoutGraph.studyChart.data = lineData
                binding.layoutGraph.studyChart.invalidate()  // 다시 그리기
            }
        }

    }

    // 차트 초기화 메서드
    private fun initTimeChart() {
        binding.layoutGraph.studyChart.run {
            setDrawGridBackground(false)
            setBackgroundColor(Color.WHITE)
            legend.isEnabled = false
            isDragYEnabled = false
        }

        with(binding.layoutGraph.studyChart.xAxis) {
            setDrawLabels(true)  // Label 표시 여부
            axisMaximum = 900f  // 60min * 24hour
            axisMinimum = -540f
            labelCount = 5
            valueFormatter = TimeAxisValueFormat()

            textColor = android.graphics.Color.BLACK
            position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM  // x축 라벨 위치
            setDrawLabels(true)  // Grid-line 표시
            setDrawAxisLine(true)  // Axis-Line 표시
        }

        // 왼쪽 y축 값
        with(binding.layoutGraph.studyChart.axisLeft) {
            axisMaximum = 2.2f   // y축 최대값(고정)
            axisMinimum = 0.1f  // y축 최소값(고정)

            // 왼쪽 y축 도메인 변경
            val yAxisVals = ArrayList<String>(listOf("-", "X", "O"))
            valueFormatter = IndexAxisValueFormatter(yAxisVals)
            granularity = 1f
        }

        // 오른쪽 y축 값
        with(binding.layoutGraph.studyChart.axisRight) {
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        val marker = LineMarkerView(requireContext(), R.layout.graph_marker)
        marker.chartView = binding.layoutGraph.studyChart
        binding.layoutGraph.studyChart.marker = marker
        binding.layoutGraph.studyChart.description.isEnabled = false  // 설명

        initTimeChartData()
    }

    private fun initPercentageChartData() {
        graphVM.getAllPieResult.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                timePieData = graphVM.getPieGraphData()
                Log.d("graph_pie_data", "$timePieData")

                pieDataSet = PieDataSet(timePieData, "Time Percentage")
                val pieColor = arrayListOf<Int>(
                    ContextCompat.getColor(requireActivity(), R.color.mainOrange),
                    ContextCompat.getColor(requireActivity(), R.color.teal_200),
                    ContextCompat.getColor(requireActivity(), R.color.purple_200))

                pieDataSet.apply {
                    sliceSpace = 3f
                    colors = pieColor
                }

                pieData = PieData(pieDataSet)
                pieData.apply {
                    setValueTextSize(11f)
                    setValueTextColor(Color.WHITE)
                }

                binding.layoutGraph.studyTimeChart.data = pieData
                binding.layoutGraph.studyTimeChart.invalidate()

                initPercentageChartView(graphVM.getRealStudyTime(), graphVM.getDrowsinessTime(), graphVM.getSpaceOutTime())
            }
        }
    }

    private fun initPercentageChart() {
        binding.layoutGraph.studyTimeChart.run {
            transparentCircleRadius = 95f
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)

            isDrawHoleEnabled = true
            setHoleColor(Color.WHITE)

            legend.isEnabled = false

            initPercentageChartData()
        }
    }

    private fun initPercentageChartView(study: Int, drowsiness: Int, spaceOut: Int) {
        with(binding.layoutGraph.layoutTimeStudy) {
            tvTimeTitle.text = getString(com.parklee.studywithcam.R.string.state_study)
            tvTime.text = ClockFormat.calSecToString(study)
        }
        with(binding.layoutGraph.layoutTimeDrowsiness) {
            tvTimeTitle.text = getString(com.parklee.studywithcam.R.string.state_drowsiness)
            tvTime.text = ClockFormat.calSecToString(drowsiness)
        }
        with(binding.layoutGraph.layoutTimeSpaceOut) {
            tvTimeTitle.text = getString(com.parklee.studywithcam.R.string.state_space_out)
            tvTime.text = ClockFormat.calSecToString(spaceOut)
        }
    }


    private fun getTodayDate(): String {
        var date = Date(System.currentTimeMillis())
        var todayDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return todayDateFormat.format(date)
    }

}