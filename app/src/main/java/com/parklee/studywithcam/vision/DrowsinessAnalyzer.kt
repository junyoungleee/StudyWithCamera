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
                // Download complete. Depending on your app, you could enable the ML
                // feature, or switch from the local model to the remote model, etc.
                Toast.makeText(context, "Model download complete", Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener { model ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                    Log.d("model path", model!!.file!!.path.toString())
                }
            }

    }

    @SuppressLint("UnsafeOptInUsageError")
    public fun classifyFace(image: ImageProxy): String {
        var img: Image = image.image!!
        var bitmap: Bitmap = imageProcessing.toBitmap(img)
        var rotation = imageProcessing.getImageRotation(image)
        var width = bitmap.width
        var height = bitmap.height

        var size = if (height > width) width else height
        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeWithCropOrPadOp(size, size))
            .add(ResizeOp(400, 400, ResizeOp.ResizeMethod.BILINEAR))
            .add(Rot90Op(rotation))
            .build()

//        var tensorImage = TensorImage(DataType.UINT8)
//        tensorImage.load(bitmap)
//        tensorImage = imageProcessor.process(tensorImage)

        var imageBuffer = imageProcessing.toByteBuffer(bitmap)

//        var resultBuffer = TensorBuffer.createFixedSize(IntArray(4), DataType.FLOAT32)
        val bufferSize = 4 * java.lang.Float.SIZE / java.lang.Byte.SIZE
        val modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder())

        if (interpreter != null) {
//            interpreter.run(tensorImage.buffer, resultBuffer.buffer)
            interpreter.run(imageBuffer, modelOutput)
        }

        var resultProcessor: TensorProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()
        modelOutput.rewind()
        val probabilites = modelOutput.asFloatBuffer()

        var result = ""
        var max = -1f
        var maxIdx = 0
        if( associatedAxisLabels != null) {
//            var labels = TensorLabel(associatedAxisLabels, resultProcessor.process(resultBuffer))
//            var floatMap: Map<String, Float> = labels.mapWithFloatValue
//            result = "${floatMap.keys}, ${floatMap.values}"
//            Log.d("프라버빌리티", probabilites.capacity().toString())
//            Log.d("어쏘시에이티드", associatedAxisLabels.size.toString())

            for (i in 0..(probabilites.capacity()-1)) {
                val probability = probabilites.get(i)
                val label = associatedAxisLabels[i]
                if (probability > max) {
                    max = probability
                    maxIdx = i
                }
                result += "$label : $probability\n"

            }

            result += "\n결과 : ${associatedAxisLabels[maxIdx]}"
         }
        return result
    }

}