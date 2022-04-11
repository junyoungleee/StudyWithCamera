package com.parklee.studywithcam.vision

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * MLkit FaceDetection
 */
class FaceDetectAnalyzer(lifecycle: Lifecycle) {

    private lateinit var nowImageProxy : ImageProxy
    private var result = arrayListOf<Int>()

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
//        .enableTracking() //disable when contour is enable https://developers.google.com/ml-kit/vision/face-detection/android
        .build()

    private val detector = FaceDetection.getClient(options)

    init {
        lifecycle.addObserver(detector)
    }

    fun analyze(image: ImageProxy): ArrayList<Int> {
//        overlay.setPreviewSize(Size(image.width,image.height))
        detectFaces(image)
        return result
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectFaces(imageProxy: ImageProxy){
        val image = InputImage.fromMediaImage(imageProxy.image as Image, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener { faces ->
//                overlay.setFaces(faces)
                result = FaceProcessing.getLongerEye(faces[0])
                Log.d("face_result", "$result")

            }
            .addOnFailureListener(failureListener)
            .addOnCompleteListener{
                imageProxy.close()
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private val successListener = OnSuccessListener<List<Face>> { faces ->
        Log.d(TAG, "Number of face detected: " + faces.size)
    }

    private val failureListener = OnFailureListener { e ->
        Log.e(TAG, "Face analysis failure.", e)
    }

    companion object {
        private const val TAG = "FaceAnalyzer"
    }
}

