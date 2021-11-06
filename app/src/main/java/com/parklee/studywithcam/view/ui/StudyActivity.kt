package com.parklee.studywithcam.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.ActivityStudyBinding
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.viewmodel.ServerViewModel
import com.parklee.studywithcam.viewmodel.TimerViewModel
import com.parklee.studywithcam.vision.DrowsinessAnalyzer
import com.parklee.studywithcam.vision.FaceImageAnalyzer
import com.parklee.studywithcam.vision.VisionOverlay
import org.tensorflow.lite.Interpreter
import java.util.concurrent.Executors

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var timerVM: TimerViewModel
    private lateinit var serverVM: ServerViewModel

    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var overlay: VisionOverlay
    private var clockFormat = ClockFormat()

    private lateinit var drowsinessAnalyzer: DrowsinessAnalyzer

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "DETECT RESULT : "
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        serverVM = ViewModelProvider(this).get(ServerViewModel::class.java)  // Test

        // 타이머
        var init = SWCapplication.pref.getPrefTime("cTime")

        timerVM = ViewModelProvider(this).get(TimerViewModel::class.java)
        timerVM.startTimer(init)  // 현재 타이머: 0, 누적 타이머: init

        timerVM.nTime.observe(this, Observer { time ->
            binding.studyNowTv.text = clockFormat.calSecToString(time)
        })

        timerVM.cTime.observe(this, Observer { time ->
            binding.studyCumulTv.text = clockFormat.calSecToString(time)
        })

        drowsinessAnalyzer = DrowsinessAnalyzer(this)

        // 카메라
//        overlay = VisionOverlay(this)
//        val layoutOverlay = ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        this.addContentView(overlay, layoutOverlay)

        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // 돌아가기
        stopTimerButtonAction()
    }

    //----------------------------------------------------------------------------------
    // 카메라 권한
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "카메라를 사용할 수 있는 권한이 없습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 실행
    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()  // lifecycle & 카메라 bind
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // default : 정면 카메라
            // CameraX preview(미리보기)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.studyPreview.surfaceProvider)
            }

            val analysisUseCase = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                            var result = drowsinessAnalyzer.classifyFace(imageProxy)
                            runOnUiThread {
                                binding.studyResultTv.text = result
                            }
                            imageProxy.close()
                        }
                    )
                }


            try {
                cameraProvider.unbindAll()  // rebinding 전 모든 케이스 unbind
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, analysisUseCase)  // bind use case to camera
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }





    //----------------------------------------------------------------------------------
    // 타이머 멈춤 & 저장
    private fun stopTimerButtonAction() {
        binding.studyTimerButton.setOnClickListener {
            timerVM.stopTimer()
//            serverVM.getDummy() // Test

            SWCapplication.pref.setPrefTime("cTime", timerVM.cTime.value!!.toInt())
            SWCapplication.pref.setPrefTime("nTime", timerVM.nTime.value!!.toInt())

            finish()
        }
    }



}


