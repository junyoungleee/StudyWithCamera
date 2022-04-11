package com.parklee.studywithcam.vision

import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageProcessing() {

    fun getImageRotation(image: ImageProxy): Int {
        val rotation = image.imageInfo.rotationDegrees
        return rotation/90
    }

    fun toBitmap(image: Image): Bitmap {
        val planes: Array<Image.Plane> = image.planes
        val yBuffer: ByteBuffer = planes[0].buffer
        val uBuffer: ByteBuffer = planes[1].buffer
        val vBuffer: ByteBuffer = planes[2].buffer

        val ySize: Int = yBuffer.remaining()
        val uSize: Int = uBuffer.remaining()
        val vSize: Int = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize+vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)

        val imageByte: ByteArray = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
    }

    fun makeResult(results: Map<String, Float>): String {
        val resultList = results.toList().sortedByDescending { it.second }

        var result = ""
        result += "${resultList[0].first}\n\n"

        val resultMap = resultList.toMap().toMutableMap()
        resultMap.forEach { (label, value) ->
            result += "${label} : ${value}\n"
        }

        return result
    }
    
    fun finalResult(results: Map<String, Float>): String {
        val resultList = results.toList().sortedByDescending { it.second }
        val result = resultList[0].first
        return result
    }

}

