package com.parklee.studywithcam.view.graph

import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study

class GraphData {
    companion object {
        fun dataToLinearStudySectionData(studys: List<Study>, disturbs1: List<Disturb>, disturbs2: List<Disturb>): ArrayList<Entry> {
            val result = arrayListOf<Entry>()

            result.add(Entry(-540f, 0f))
            result.add(Entry(900f, 0f))

            studys.map { study ->
                result.add(Entry(timeToGraphTime(study.startTime), 2f))
                result.add(Entry(timeToGraphTime(study.endTime), 0f))
            }
            disturbs1.map { disturb ->
                result.add(Entry(timeToGraphTime(disturb.startTime), 1f))
                result.add(Entry(timeToGraphTime(disturb.endTime), 2f))
            }
            disturbs2.map { disturb ->
                result.add(Entry(timeToGraphTime(disturb.startTime), 1f))
                result.add(Entry(timeToGraphTime(disturb.endTime), 2f))
            }

            result.sortBy { it.x }
            return result
        }

        fun dataToLinearDisturbData(disturbs: List<Disturb>): ArrayList<Entry>  {
            val result = arrayListOf<Entry>()
            result.add(Entry(-540f, 0f))
            disturbs.map { disturb ->
                result.add(Entry(timeToGraphTime(disturb.startTime), 1f))
                result.add(Entry(timeToGraphTime(disturb.endTime), 0f))
            }

            result.sortBy { it.x }
            return result
        }

        fun dataToPieData(study: Int, drowsiness: Int, spaceOut: Int): ArrayList<PieEntry> {
            val result = arrayListOf<PieEntry>()
            val total = (study + drowsiness + spaceOut).toFloat()

            Log.d("graph_pie_data_total", "${(study/total)*100}")
            result.add(PieEntry((study/total)*100.toFloat(), "공부"))
            result.add(PieEntry((drowsiness/total)*100.toFloat(), "졸음"))
            result.add(PieEntry((spaceOut/total)*100.toFloat(), "멍때림"))

            return result
        }

        private fun timeToGraphTime(time: String): Float {
            val t = time.split(":")
            return (-540 + t[0].toInt()*60 + t[1].toInt()).toFloat()
        }
    }
}