package com.parklee.studywithcam.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.parklee.studywithcam.repository.DatabaseRepository
import com.parklee.studywithcam.view.graph.GraphData
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class GraphViewModel(private val dbRepo: DatabaseRepository) : ViewModel() {

    private var totalGraphData = arrayListOf<Entry>()
    private var drowsinessGraphData = arrayListOf<Entry>()
    private var spaceOutGraphData = arrayListOf<Entry>()

    fun getTotalGraphData(): ArrayList<Entry> {
        return totalGraphData
    }

    fun getDrowsinessGraphData(): ArrayList<Entry> {
        return drowsinessGraphData
    }

    fun getSpaceOutGraphData(): ArrayList<Entry> {
        return spaceOutGraphData
    }

    private val _getAllLinearResult = MutableLiveData<Boolean>(false)
    val getAllLinearResult: LiveData<Boolean> get() = _getAllLinearResult

    fun setLinearGraphData() {
        viewModelScope.launch {
            val job = viewModelScope.launch {
                val studySections = dbRepo.getTodayStudySections("2022-05-29")
                val drowsinessSections = dbRepo.getTodayDisturbSections("2022-05-29", "졸음")
                val spaceOutSections = dbRepo.getTodayDisturbSections("2022-05-29", "멍때림")

                totalGraphData = GraphData.dataToLinearStudySectionData(studySections, drowsinessSections, spaceOutSections)
                drowsinessGraphData = GraphData.dataToLinearDisturbData(drowsinessSections)
                spaceOutGraphData = GraphData.dataToLinearDisturbData(spaceOutSections)
                Log.d("graph_launch", "ok")
            }
            job.join()
            Log.d("graph_after_launch", "ok")
            _getAllLinearResult.value = true
        }
    }


    private val _getAllPieResult = MutableLiveData<Boolean>(false)
    val getAllPieResult: LiveData<Boolean> get() = _getAllPieResult

    private var pieGraphData = arrayListOf<PieEntry>()
    private var realStudyTime = 0
    private var drowsinessTime = 0
    private var spaceOutTime = 0

    fun getRealStudyTime(): Int {
        return realStudyTime
    }

    fun getDrowsinessTime(): Int {
        return drowsinessTime
    }

    fun getSpaceOutTime(): Int {
        return spaceOutTime
    }

    fun getPieGraphData(): ArrayList<PieEntry> {
        return pieGraphData
    }


    fun setPieChartData() {
        viewModelScope.launch {
            val job = viewModelScope.launch {
                val studyTime = dbRepo.getTodayRealStudyTime("2022-05-29")
                drowsinessTime = dbRepo.getTodayDisturbTypeTime("2022-05-29", "졸음")
                spaceOutTime = dbRepo.getTodayDisturbTypeTime("2022-05-29", "멍때림")
                realStudyTime = studyTime - drowsinessTime - spaceOutTime

                Log.d("graph_pie_data", "${realStudyTime}, ${spaceOutTime}, ${drowsinessTime}")
                pieGraphData = GraphData.dataToPieData(realStudyTime, drowsinessTime, spaceOutTime)
                Log.d("graph_pie_launch", "$pieGraphData")
            }
            job.join()
            Log.d("graph_pie_afterlaunch", "ok")
            _getAllPieResult.value = true
        }
    }


}

class GraphViewModelFactory(private val dbRepo: DatabaseRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GraphViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GraphViewModel(dbRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}