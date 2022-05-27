package com.parklee.studywithcam.vision

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * MLkit FaceDetection
 */
class FaceDetectAnalyzer(lifecycle: Lifecycle, context: Context) {

    private var context: Context
    private var result = arrayListOf<Int>()
    private var blinked: Int = 0

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
        .build()

    private val detector = FaceDetection.getClient(options)

    init {
        lifecycle.addObserver(detector)
        this.context = context
    }

    fun analyze(image: ImageProxy): ArrayList<Int> {
        detectFaces(image)
        return result
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectFaces(imageProxy: ImageProxy){
        val image = InputImage.fromMediaImage(imageProxy.image as Image, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    // 얼굴이 있을 때
                    result = FaceProcessing.getLongerEye(faces[0])
                    val headPosition = FaceProcessing.getFaceDirection(faces[0])

                    if (faces[0].leftEyeOpenProbability != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                        val leftOpenProb = faces[0].leftEyeOpenProbability
                        val rightOpenProb = faces[0].rightEyeOpenProbability
                        Log.d("face_open_prob", "$leftOpenProb, $rightOpenProb")
                        blinked = if (leftOpenProb < 0.1 && rightOpenProb < 0.1) 1 else 0
                    }

                    result.add(blinked)
                    result.add(headPosition)
                    Log.d("face_result", "$result")
                } else {
                    result = arrayListOf<Int>()
                    Toast.makeText(context, "얼굴이 보이지 않아요", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener(failureListener)
            .addOnCompleteListener{
                imageProxy.close()
            }
    }

    private val failureListener = OnFailureListener { e ->
        Log.e(TAG, "Face analysis failure.", e)
    }

    companion object {
        private const val TAG = "FaceAnalyzer"
    }
}

