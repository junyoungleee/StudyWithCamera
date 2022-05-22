package com.parklee.studywithcam.view.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.parklee.studywithcam.R
import com.parklee.studywithcam.SWCapplication
import com.parklee.studywithcam.databinding.ActivityStudyBinding
import com.parklee.studywithcam.network.data.Disturb
import com.parklee.studywithcam.network.data.Study
import com.parklee.studywithcam.repository.NetworkRepository
import com.parklee.studywithcam.view.format.ClockFormat
import com.parklee.studywithcam.viewmodel.ServerViewModel
import com.parklee.studywithcam.viewmodel.ServerViewModelFactory
import com.parklee.studywithcam.viewmodel.StudyViewModel
import com.parklee.studywithcam.viewmodel.TimerViewModel
import com.parklee.studywithcam.vision.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StudyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private lateinit var timerVM: TimerViewModel
    private lateinit var serverVM: ServerViewModel
    private lateinit var studyVM: StudyViewModel

    // Camera + Model
    private var cameraExecutor = Executors.newSingleThreadExecutor()
    private var clockFormat = ClockFormat()

    private lateinit var faceDetectAnalyzer: FaceDetectAnalyzer
    private lateinit var gazeAnalyzer: EyeAnalyzer

    var isPreviewOn: Boolean = true  // 카메라 프리뷰
    private var lastAnalyzedTimestamp = 0L

    private lateinit var overlay: VisionOverlay

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        uid = SWCapplication.pref.getUid("uid") // uid 받아오기

        repository = NetworkRepository()
        viewModelFactory = ServerViewModelFactory(repository)
        serverVM = ViewModelProvider(this, viewModelFactory).get(ServerViewModel::class.java)
        studyVM = ViewModelProvider(this).get(StudyViewModel::class.java)

        // 타이머
        timerVM = ViewModelProvider(this).get(TimerViewModel::class.java)
        setTimer()
        beforeStartStudy()

        // 분석 모델
        faceDetectAnalyzer = FaceDetectAnalyzer(lifecycle, this)
        gazeAnalyzer = EyeAnalyzer(this)

        // -----------------------------------------------------------------------------------------
        // 카메라
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (allPermissionsGranted()) {
            startCamera(isPreviewOn)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        binding.cameraPreviewButton.isSelected = true
        setCameraPreviewButton()

        overlay = VisionOverlay(this)
        val layoutOverlay = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.addContentView(overlay, layoutOverlay)

        // 돌아가기
        stopTimerButtonAction()

        studyVM.notStudying.observe(this) { notStudying ->
            if (notStudying) {
                Toast.makeText(this, getString(R.string.study_empty), Toast.LENGTH_SHORT).show()
                // 서버에 데이터 전송 로직 추가
                finish()
            }
        }
    }


    //----------------------------------------------------------------------------------
    // 화면 조정 후 공부 시작
    private fun beforeStartStudy() {
        binding.layoutBeforeStart.startButton.setOnClickListener {
            with(binding) {
                layoutBeforeStart.linearBeforeStart.visibility = View.GONE
                layoutTimerCumul.llTimer.visibility = View.VISIBLE
                layoutTimerNow.llTimer.visibility = View.VISIBLE
                studyTimerButton.visibility = View.VISIBLE
                cameraPreviewButton.visibility = View.VISIBLE
                ivCropImage.visibility = View.VISIBLE
            }
            // 타이머 시작
            timerVM.startTimer()  // 현재 타이머: 0, 누적 타이머: init
        }
    }

    private fun setTimer() {
        timerVM.nTime.observe(
            this,
            Observer { time -> binding.layoutTimerNow.tvTimer.text = clockFormat.calSecToString(time) })
        timerVM.cTime.observe(
            this,
            Observer { time -> binding.layoutTimerCumul.tvTimer.text = clockFormat.calSecToString(time) })

        binding.layoutTimerNow.tvTimerText.text = getString(R.string.study_now_timer)
        binding.layoutTimerCumul.tvTimerText.text = getString(R.string.study_cumul_timer)
    }

    //----------------------------------------------------------------------------------
    // 카메라 권한
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(isPreviewOn)
            } else {
                Toast.makeText(this, "카메라를 사용할 수 있는 권한이 없습니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }


    // 카메라 실행
    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun startCamera(isPreviewOn: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider =
                cameraProviderFuture.get()  // lifecycle & 카메라 bind
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA // default : 정면 카메라

            // CameraX preview(미리보기)
            val preview = Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .build()
                .also {
                    it.setSurfaceProvider(binding.studyPreview.surfaceProvider)
                }

            var lastAnalyzedTimestamp = 0L
            // CameraX analyze(분석하기)
            val analysisUseCase = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->

                            val p = faceDetectAnalyzer.analyze(imageProxy)

                            val currentTimestamp = System.currentTimeMillis()
                            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(
                                    1
                                )
                            ) {

                                if (p.isNotEmpty()) {
                                    val blinkResult =
                                        if (p[3] == 1) BLINK_STATE.CLOSE else BLINK_STATE.OPEN
                                    val headDirection =
                                        if (p[4] == 1) HEAD_STATE.UP else HEAD_STATE.DOWN

                                    val rotate = imageProxy.imageInfo.rotationDegrees
                                    val bitmap = FaceProcessing.cropImage(
                                        imageProxy.image,
                                        imageProxy.imageInfo.rotationDegrees.toFloat(),
                                        p[0],
                                        p[1],
                                        p[2]
                                    )
                                    val gazeResult =
                                        gazeAnalyzer.classifyEyeDirection(bitmap, rotate)

                                    studyVM.analyzeDisturb(blinkResult, gazeResult, headDirection)
                                    Log.d("eye_gaze", "$gazeResult")

                                    // 테스트용
                                    runOnUiThread {
                                        binding.tvGazeResult.text = "$gazeResult / $blinkResult"
                                        binding.ivCropImage.setImageBitmap(bitmap)
                                    }
                                } else {
                                    studyVM.detectEmptyFace()
                                    runOnUiThread {
                                        Toast.makeText(this, "얼굴이 보이지 않아요 :(", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                }
                                lastAnalyzedTimestamp = currentTimestamp
                            }
                        }
                    )
                }

            try {
                cameraProvider.unbindAll()  // rebinding 전 모든 케이스 unbind
                // bind use case to camera
                when (isPreviewOn) {
                    true -> cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        analysisUseCase
                    )
                    false -> cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase)
                }
            } catch (exc: Exception) {
                Log.e("Camera", "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // 카메라 프리뷰 온오프 버튼 세팅
    private fun setCameraPreviewButton() {
        binding.cameraPreviewButton.setOnClickListener {
            if(isPreviewOn) {
                // 프리뷰 OFF
                isPreviewOn = false
                binding.cameraPreviewButton.isSelected = false
                binding.studyPreview.visibility = View.GONE

            } else {
                // 프리뷰 ON
                isPreviewOn = true
                binding.cameraPreviewButton.isSelected = true
                binding.studyPreview.visibility = View.VISIBLE
            }
            startCamera(isPreviewOn)
        }
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

            var saveIntent = Intent()
            val studyNowTime = timerVM.nTime.value!!.toInt()
            val studyCumTime = timerVM.cTime.value!!.toInt()

            saveIntent.putExtra("nTime", studyNowTime)
            saveIntent.putExtra("cTime", studyCumTime)

            setResult(RESULT_OK, saveIntent)
            finish()
        }

    }

}


