package com.parklee.studywithcam.vision

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.media.Image
import android.util.Log
import com.google.mlkit.vision.face.Face
import java.nio.ByteBuffer
import kotlin.math.abs
import kotlin.math.hypot

/**
 * Analyze face landmark and extract information
 *
 */

object FaceProcessing {

    private var imageProcessing = ImageProcessing()

    private fun getDistance(p1: PointF, p2: PointF): Double {
        return hypot((p2.x - p1.x).toDouble(), (p2.y - p1.y).toDouble())
    }

//    fun getFaceDirection(face: Face): ArrayList<DIRECTION> {
//        // Horizontal, Vertical 방향 체크
//        // 얼굴 각도는 오른쪽인데 눈 위치는 왼쪽이면 멍 때림으로 체크할 수 있을 것
//        val contour = face.allContours
//        val p1 = contour[11].points[0]  // 코의 최상단 좌표
//        val p2 = contour[0].points[8]   // 얼굴의 가장 왼쪽 좌표
//        val p3 = contour[0].points[28]  // 얼굴의 가장 오른쪽 좌표
//        val p4 = contour[0].points[18]  // 얼굴의 최하단 좌표
//    }

    fun getLongerEye(face: Face): ArrayList<Int> {
        val contour = face.allContours
        val leftEye = contour[6].points
        val rightEye = contour[5].points

        val leftLength = abs(leftEye[0].x - leftEye[8].x)
        val rightLength = abs(rightEye[0].x - rightEye[8].x)

        var rectInfo = arrayListOf<Int>()
        if (leftLength >= rightLength) {
            val size = abs(leftEye[0].x - leftEye[8].x)+20 // 양 옆에 10씩 추가
            val core = abs(leftEye[8].y + leftEye[0].y)/2  // 가운데 좌표
            Log.d("face_eye_left", "0x: ${leftEye[0].x}, 0y: ${leftEye[0].y}, 8x: ${leftEye[8].x}, 8y: ${leftEye[8].y}")
//            Log.d("face_eye_left_face", "7x: ${face[7].x}, 7y: ${face[7].y}")
//            Log.d("face_eye_left_nose", "0x: ${nose[0].x}, 0y: ${nose[0].y}")
            rectInfo.apply {
                add((leftEye[0].x - 10).toInt())
                add((core - size/2).toInt())
                add(size.toInt())
            }

        } else {
            val size = abs(rightEye[0].x - rightEye[8].x)+20 // 양 옆에 5씩 추가
            val core = abs(rightEye[8].y + rightEye[0].y)/2  // 가운데 좌표
            Log.d("face_eye_right", "0x: ${rightEye[0].x}, 0y: ${rightEye[0].y}, 8x: ${rightEye[8].x}, 8y: ${rightEye[8].y}")
            rectInfo.apply {
                add((rightEye[0].x - 10).toInt())
                add((core - size/2).toInt())
                add(size.toInt())
            }
        }

        return rectInfo
    }

    fun cropImage(image: Image?, rotationDegree: Float, xOffset: Int, yOffset: Int, size: Int): Bitmap {
        // 1 - Convert image to Bitmap
        var bitmap = imageProcessing.toBitmap(image!!)

        // 2 - Rotate the Bitmap
        if (rotationDegree != 0f) {
            val rotationMatrix = Matrix()
            rotationMatrix.postRotate(rotationDegree)
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotationMatrix, true)
        }

        // 3 - Crop the Bitmap
        bitmap = Bitmap.createBitmap(bitmap, xOffset, yOffset, size, size)
        return bitmap
    }


}