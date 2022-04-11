package com.parklee.studywithcam.vision

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageProxy
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
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class FaceAnalyzer {

    private var context: Context
    private var model: AiModel
    lateinit var modelName: String
    lateinit var interpreter: Interpreter
    var imageProcessing = ImageProcessing()

    var DROWSINESS_LABELS: String = "drowsiness_labels.txt"
    var UNDERSTADING_LABELS: String = "understanding_labels.txt"
    lateinit var associatedAxisLabels: List<String>

    private var lastAnalyzedTimestamp = 0L

    constructor(context: Context, model: AiModel) {
        this.context = context
        this.model = model

        try {
            when(model) {
                AiModel.DROWSINESS -> {
                    associatedAxisLabels = FileUtil.loadLabels(context, DROWSINESS_LABELS)
                    modelName = "Drowsiness-Detector"
                }
                AiModel.UNDERSTANDING -> {
                    associatedAxisLabels = FileUtil.loadLabels(context, UNDERSTADING_LABELS)
                    modelName = "Understanding-Detector"
                }
            }
        } catch (e: IOException) {
            Log.e("tfliteSupport", "Error reading label file", e)
        }

        val conditions = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        FirebaseModelDownloader.getInstance()
            .getModel(modelName, DownloadType.LOCAL_MODEL, conditions)
            .addOnSuccessListener { model ->
                val modelFile = model?.file
                if (modelFile != null) {
                    interpreter = Interpreter(modelFile)
                }
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun classifyFace(image: ImageProxy): String {
        var result = ""

        // 1초마다 실행
        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)) {
            Log.d("실행 : 현재", "$currentTimestamp")
            Log.d("실행 : 이전", "$lastAnalyzedTimestamp")

            var img: Image = image.image!!
            var bitmap: Bitmap = imageProcessing.toBitmap(img)
            var width = bitmap.width
            var height = bitmap.height
            Log.d("비트맵", "w:${width}, h:${height}")

            var size = if (height > width) width else height
            var inputSize = 48  //model input size
            var imageProcessor = ImageProcessor.Builder()
                .add(ResizeWithCropOrPadOp(size, size))
                .add(ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
                .build()

            var tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            tensorImage = imageProcessor.process(tensorImage)

            // 결과 버퍼
            var resultBufferSize = when(model){
                AiModel.DROWSINESS -> 4
                AiModel.UNDERSTANDING -> 3
            }
            var resultBuffer = TensorBuffer.createFixedSize(intArrayOf(resultBufferSize), DataType.FLOAT32)

            // 인터프리터 실행
            if (interpreter != null) {
                interpreter.run(tensorImage.buffer, resultBuffer.buffer)
            }

            var resultProcessor: TensorProcessor = TensorProcessor.Builder().add(NormalizeOp(0f, 255f)).build()

            // 결과값 생성
            if( associatedAxisLabels != null) {
                var labels = TensorLabel(associatedAxisLabels, resultProcessor.process(resultBuffer))
                var floatMap: Map<String, Float> = labels.mapWithFloatValue

                var allResult = imageProcessing.makeResult(floatMap)
                Log.d("전체결과 : ", "$allResult")
                result = imageProcessing.finalResult(floatMap)  // 실제 결과
            }
            lastAnalyzedTimestamp = currentTimestamp
        }

        return result
    }
}