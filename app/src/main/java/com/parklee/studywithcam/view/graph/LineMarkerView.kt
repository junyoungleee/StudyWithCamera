package com.parklee.studywithcam.view.graph

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.parklee.studywithcam.R

class LineMarkerView : MarkerView {

    private var marker: TextView = findViewById<TextView>(R.id.marker)

    constructor(context: Context, layoutResource: Int) : super(context, layoutResource)

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        marker.text = TimeAxisValueFormat().getFormattedValue(e?.x!!.toFloat())

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width/2)).toFloat(), -height.toFloat())
    }

}