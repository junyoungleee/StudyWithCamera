package com.parklee.studywithcam.vision

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import com.google.mlkit.vision.face.Face
import java.nio.ByteBuffer
import kotlin.math.abs

/**
 * Analyze face landmark and extract information
 *
 */

object FaceProcessing {

    private var imageProcessing = ImageProcessing()

    fun getLongerEye(face: Face): ArrayList<Int> {
        val contour = face.allContours
        val leftEye = contour[6].points
        val rightEye = contour[5].points
        val face = contour[0].points
        val nose = contour[2].points

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