package com.parklee.studywithcam.vision

import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageProcessing {

    fun getImageRotation(image: ImageProxy): Int {
        var rotation = image.imageInfo.rotationDegrees
        return rotation/90
    }

    fun toBitmap(image: Image): Bitmap {
        var planes: Array<Image.Plane> = image.planes
        var yBuffer: ByteBuffer = planes[0].buffer
        var uBuffer: ByteBuffer = planes[1].buffer
        var vBuffer: ByteBuffer = planes[2].buffer

        var ySize: Int = yBuffer.remaining()
        var uSize: Int = uBuffer.remaining()
        var vSize: Int = vBuffer.remaining()

        var nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize+vSize, uSize)

        var yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        var out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)

        var imageByte: ByteArray = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
    }

    fun makeResult(results: Map<String, Float>): String {
        val resultList = results.toList().sortedByDescending { it.second }

        var result = ""
        result += "결과 : ${resultList[0].first}\n\n"

        val resultMap = resultList.toMap().toMutableMap()
        resultMap.forEach { (label, value) ->
            result += "${label} : ${value}\n"
        }

        return result
    }

}

