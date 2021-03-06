package com.parklee.studywithcam.vision

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException

class EyeAnalyzer {

    private val context: Context
    lateinit var modelName: String
    lateinit var interpreter: Interpreter

    private lateinit var inputImage: TensorImage
    private var modelInputChannel: Int = 0
    private var modelInputWidth: Int = 0
    private var modelInputHeight: Int = 0
    private lateinit var outputBuffer: TensorBuffer

    protected val delegate = GpuDelegate()
    protected var options = (Interpreter.Options()).addDelegate(delegate)

    var imageProcessing = ImageProcessing()

    var GAZE_LABELS: String = "gaze_labels.txt"
    lateinit var associatedAxisLabels: List<String>

    private var lastAnalyzedTimestamp = 0L

    constructor(context: Context) {
        this.context = context
        try {
            associatedAxisLabels = FileUtil.loadLabels(context, GAZE_LABELS)
            modelName = "Gaze-Detector"
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
                    interpreter = Interpreter(modelFile, options)   // delegate option ??????
                    initModelShape()
                }
            }
    }

    fun initModelShape() {
        val inputTensor = interpreter.getInputTensor(0)
        val inputShape = inputTensor.shape()
        modelInputChannel = inputShape[0]
        modelInputWidth = inputShape[1]
        modelInputHeight = inputShape[2]

        inputImage = TensorImage(inputTensor.dataType())

        val outputTensor = interpreter.getOutputTensor(0)
        outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), outputTensor.dataType())
    }

    fun loadImage(bitmap: Bitmap, rotateDegree: Int): TensorImage {
        inputImage.load(bitmap)

        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(modelInputWidth, modelInputHeight, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
            .add(Rot90Op(rotateDegree))
            .add(NormalizeOp(0f, 255f))
            .build()

        return imageProcessor.process(inputImage)
    }

    fun classifyEyeDirection(image: Bitmap, rotateDegree: Int): String {

        inputImage = loadImage(image, rotateDegree)
        interpreter.run(inputImage.buffer, outputBuffer.buffer.rewind())

        val output = TensorLabel(associatedAxisLabels, outputBuffer).mapWithFloatValue
        Log.d("eye_gaze_result ", "$output")

        var result = ""

            // ????????? ??????
            if( associatedAxisLabels != null) {
                result = imageProcessing.finalResult(output)  // ?????? ??????

            }

        return result
    }
}