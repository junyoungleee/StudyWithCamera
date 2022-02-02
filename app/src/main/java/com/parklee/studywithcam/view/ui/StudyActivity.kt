package com.parklee.studywithcam.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.ActivityStudyBinding
import com.parklee.studywithcam.model.entity.Disturb
import com.parklee.studywithcam.model.entity.Study
import com.parklee.studywithcam.repository.NetworkRepository
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.viewmodel.ServerViewModel
import com.parklee.studywithcam.viewmodel.ServerViewModelFactory
import com.parklee.studywithcam.viewmodel.TimerViewModel
import com.parklee.studywithcam.vision.DrowsinessAnalyzer
import com.parklee.studywithcam.vision.VisionOverlay
import java.util.concurrent.Executors

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var timerVM: TimerViewModel
    private lateinit var serverVM: ServerViewModel

    // Camera + Model
    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private lateinit var overlay: VisionOverlay
    private var clockFormat = ClockFormat()
    private lateinit var drowsinessAnalyzer: DrowsinessAnalyzer

    // preference
    private lateinit var uid: String

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val TAG = "DETECT RESULT : "
    }

    // retrofit_server
    lateinit var repository: NetworkRepository
    lateinit var viewModelFactory: ServerViewModelFactory

    // check StudyX
    private var notFocusCnt: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = SWCapplication.pref.getUid("uid") // uid 받아오기

        repository = NetworkRepository()
        viewModelFactory = ServerViewModelFactory(repository)
        serverVM = ViewModelProvider(this, viewModelFactory).get(ServerViewModel::class.java)

        // 타이머
        var init = SWCapplication.pref.getPrefTime("cTime")
        timerVM = ViewModelProvider(this).get(TimerViewModel::class.java)
        timerVM.startTimer(init)  // 현재 타이머: 0, 누적 타이머: init

        timerVM.nTime.observe(this, Observer { time ->
            binding.studyNowTv.text = clockFormat.calSecToString(time) })
        timerVM.cTime.observe(this, Observer { time ->
            binding.studyCumulTv.text = clockFormat.calSecToString(time) })

        drowsinessAnalyzer = DrowsinessAnalyzer(this)



//        serverVM.getCalendarData(uid, 1)
//        serverVM.cal_data.observe(this, Observer {
//            Log.d("Response_1", it.toString())
//        })
//
//        serverVM.getDailyGraphData(uid, "2021-11-21")
//        serverVM.graph_data.observe(this, Observer {
//            Log.d("Response_2", it.toString())
//        })


        // 카메라

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

    private fun startNotFocus() {

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
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
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

            // post dummy data
//            var dummyStudys: List<Study> = listOf(Study("123000", "125000"))
//            var dummyFocusX: List<Disturb> = listOf(Disturb("졺", "123500", "124000"), Disturb("졺", "124300", "124500"))

//            var dummyData = StopStudys(dummyStudys, dummyFocusX)
//            serverVM.postStudyData("dummydummy", dummyData) // 성공


            SWCapplication.pref.setPrefTime("cTime", timerVM.cTime.value!!.toInt())
            SWCapplication.pref.setPrefTime("nTime", timerVM.nTime.value!!.toInt())

            finish()
        }
    }



}


