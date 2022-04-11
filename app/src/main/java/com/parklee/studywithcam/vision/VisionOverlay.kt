package com.parklee.studywithcam.vision

import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.View
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import com.parklee.studywithcam.SWCapplication
import kotlin.math.abs


class VisionOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var previewWidth: Int = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight: Int = 0
    private var heightScaleFactor = 1.0f

    private var faces = emptyArray<Face>()
    private var orientation = Configuration.ORIENTATION_LANDSCAPE
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = 1.0f
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawOverlay(canvas);
    }

    fun setOrientation(orientation: Int)
    {
        this.orientation = orientation
    }
    fun setPreviewSize(size: Size) {
        // Need to swap width and height when in portrait, since camera's natural orientation is landscape.
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            previewWidth = size.height
            previewHeight = size.width
        }
        else
        {
            previewWidth = size.width
            previewHeight = size.height
        }
    }
    fun setFaces(faceList: List<Face>)
    {
        faces = faceList.toTypedArray()
        postInvalidate()
    }

    private fun drawOverlay(canvas: Canvas) {

        val displayWidth = SWCapplication.pref.getWidth("width")
        val displayHeight = SWCapplication.pref.getHeight("height")

        // 상태바를 제외한 전체 화면 / 프리뷰 사이즈(480*640)으로 나눈 비율
        widthScaleFactor = displayWidth.toFloat() / previewWidth
        heightScaleFactor = displayHeight.toFloat() / previewHeight

//        Log.d("overlay_width", "${width} - ${previewWidth}")
//        Log.d("overlay_height", "${height} - ${previewHeight}")
        for(face in faces)
        {
            drawFaceContour(face,canvas)
            drawFaceLandmark(face,canvas)
        }
    }

    // draw a square shape around the face
    private fun drawFaceBorder(face: Face, canvas: Canvas)
    {
        val bounds = face.boundingBox
        val left = translateX(bounds.left.toFloat())  // Front Camera
        val top = translateY(bounds.top.toFloat())
        val right = translateX(bounds.right.toFloat())  // Front Camera
        val bottom = translateY(bounds.bottom.toFloat())
        canvas.drawRect(left, top, right, bottom, paint)
    }

    // Note: Contours are detected for only the most prominent face in an image.
    // draw a dotted contour on the most prominent detected face
    private fun drawFaceContour(face: Face, canvas: Canvas)
    {
        val contour = face.allContours
        var right_0 = 0f
        var right_8 = 0f
        var left_0 = 0f
        var left_8 = 0f

        for (index in 0 until contour.size) {
            if (index == 5 || index == 6) {
                val points = contour[index].points
                for (idx in 0 until points.size) {
                    if (idx == 0 || idx == 8) {
                        val rx = if(previewWidth - points[idx].x < 0) {
                            (previewWidth - points[idx].x)*-1
                        } else {
                            previewWidth - points[idx].x
                        }
                        val px = translateX(rx)
                        val py = translateY(points[idx].y)

                        if (index == 5 && idx == 0) right_0 = px
                        else if (index == 5 && idx == 8) right_8 = px
                        else if (index == 6 && idx == 0) left_0 = px
                        else if (index == 6 && idx == 8) left_8 = px
                        canvas.drawCircle(px, py, 5.0f, dotPaint)
                    }
                }
            }
        }
        // compare left, right width
        val left = left_0 - left_8
        val right = right_0 - right_8
        if (left > right) {
            Log.d("overlay_eye", "left - $left : $right")
        } else {
            Log.d("overlay_eye", "right - $left : $right")
        }
    }


    // draw dots or circle on the given face landmark
    private fun drawFaceLandmark(face: Face, canvas: Canvas)
    {
        val leftEye = face.getLandmark(FaceLandmark.LEFT_EYE)
        val nose = face.getLandmark(FaceLandmark.NOSE_BASE)
        val rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE)
        Log.d("overlay_left_eye", "x: ${leftEye.position.x}, y: ${leftEye.position.y}")
        Log.d("overlay_right_eye", "x: ${rightEye.position.x}, y: ${rightEye.position.y}")
        drawFaceLandmark(leftEye,canvas)
        drawFaceLandmark(rightEye,canvas)
        drawFaceLandmark(nose,canvas)
    }

    private fun drawFaceLandmark(faceLandmark: FaceLandmark?, canvas: Canvas)
    {
        if(faceLandmark == null)
            return
        val rx = if(previewWidth - faceLandmark.position.x < 0) {
            (previewWidth - faceLandmark.position.x)*-1
        } else {
            previewWidth - faceLandmark.position.x
        }
        canvas.drawCircle(translateX(rx),translateY(faceLandmark.position.y), 10.0f, dotPaint)
    }
    private fun translateX(x: Float): Float = x * widthScaleFactor
    private fun translateY(y: Float): Float = y * heightScaleFactor
}