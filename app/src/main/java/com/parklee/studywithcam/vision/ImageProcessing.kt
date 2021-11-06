package com.parklee.studywithcam.vision

import android.graphics.*
import android.media.Image
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageProcessing {

    public fun getImageRotation(image: ImageProxy): Int {
        var rotation = image.imageInfo.rotationDegrees
        return rotation/90
    }

    public fun toBitmap(image: Image): Bitmap {
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

    public fun toByteBuffer(bitmap: Bitmap): ByteBuffer {
        val bitmap = Bitmap.createScaledBitmap(bitmap, 145, 145, true)
        val input = ByteBuffer.allocateDirect(145*145*3*4).order(ByteOrder.nativeOrder())
        for (y in 0 until 145) {
            for (x in 0 until 145) {
                val px = bitmap.getPixel(x, y)

                // Get channel values from the pixel value.
                val r = Color.red(px)
                val g = Color.green(px)
                val b = Color.blue(px)

                // Normalize channel values to [-1.0, 1.0]. This requirement depends on the model.
                // For example, some models might require values to be normalized to the range
                // [0.0, 1.0] instead.
                val rf = (r - 127) / 255f
                val gf = (g - 127) / 255f
                val bf = (b - 127) / 255f

                input.putFloat(rf)
                input.putFloat(gf)
                input.putFloat(bf)
            }
        }
        return input
    }


}

