package com.parklee.studywithcam.vision

import android.annotation.SuppressLint
import android.graphics.*
import android.media.Image
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.Lifecycle
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceImageAnalyzer(lifecycle: Lifecycle, private val overlay: VisionOverlay) : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "FaceAnalyzer"
    }

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setMinFaceSize(0.15f)
//            .enableTracking()
        .build()

    val detector = FaceDetection.getClient(options)

    init {
        lifecycle.addObserver(detector)
    }

    override fun analyze(imageProxy: ImageProxy) {
        overlay.setPreviewSize(Size(imageProxy.width, imageProxy.height))
        detectFace(imageProxy)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun detectFace(imageProxy: ImageProxy) {
        val image = InputImage.fromMediaImage(imageProxy.image as Image, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    private val successListener = OnSuccessListener<List<Face>> { faces ->
        Log.d(TAG, "Number of face detected: " + faces.size)
        overlay.setFaces(faces)
    }
    private val failureListener = OnFailureListener { e ->
        Log.e(TAG, "Face analysis failure.", e)
    }


}