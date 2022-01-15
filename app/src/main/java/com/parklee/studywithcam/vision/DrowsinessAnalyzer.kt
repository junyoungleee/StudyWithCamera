package com.parklee.studywithcam.vision

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.os.ParcelFileDescriptor.open
import android.system.Os.open
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageProxy
import com.google.firebase.installations.Utils
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.FileChannel.open
import java.nio.channels.Pipe.open

class DrowsinessAnalyzer {

    private var context: Context
    lateinit var interpreter: Interpreter
    var ASSOCIATED_AXIS_LABELS: String = "labels.txt"
    lateinit var associatedAxisLabels: List<String>
    var imageProcessing = ImageProcessing()

    constructor(context: Context) {
        this.context = context

        try {
            associatedAxisLabels = FileUtil.loadLabels(context, ASSOCIATED_AXIS_LABELS)
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
        }

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()
        FirebaseModelDownloader.getInstance()
            .getModel("Drowsiness-Detector", DownloadType.LOCAL_MODEL, conditions)
            .addOnCompleteListener {
            }
            .addOnSuccessListener { model ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                }
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    public fun classifyFace(image: ImageProxy): String {
        var img: Image = image.image!!
        var bitmap: Bitmap = imageProcessing.toBitmap(img)
        var width = bitmap.width
        var height = bitmap.height
        Log.d("비트맵", "w:${width}, h:${height}")

        var size = if (height > width) width else height
        var inputSize = 224  //model input size
        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(size, size))
            .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        var resultBuffer = TensorBuffer.createFixedSize(intArrayOf(4), DataType.FLOAT32)


        if (interpreter != null) {
            interpreter.run(tensorImage.buffer, resultBuffer.buffer)
        }

        var resultProcessor: TensorProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()

        var result = ""
        if( associatedAxisLabels != null) {
            var labels = TensorLabel(associatedAxisLabels, resultProcessor.process(resultBuffer))
            var floatMap: Map<String, Float> = labels.mapWithFloatValue

            result = imageProcessing.makeResult(floatMap)
         }
        return result
    }

}