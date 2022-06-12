# StudyWithCamera
[종합설계 1] 학습자 행동분석을 통한 학습 모니터링 서비스 

## Introduction
*This is an Android Application for measuring, improving, and managing self-directed learnes' learning status.*<br>
자기 주도 학습자의 학습 상태를 측정, 개선 및 관리하기 위한 Android 애플리케이션<br>


+ 학습자의 상태를 <code>공부 상태</code>, <code>졸음 상태</code>, <code>멍때림 상태</code>로 분류하여 학습자 모니터링을 서비스를 제공함
    - <code>졸음 상태</code> : 10초 동안 눈을 감고 있다고 판단되는 경우
    - <code>멍때림 상태</code> : 10초 동안 고개를 들고 있거나 10초 동안 눈동자가 움직이지 않는다고 판단되는 경우
    - 학습자가 모니터 상에 5초 동안 없는 경우, 학습을 종료한 것으로 판단함
+ 오늘의 학습자의 3가지 상태는 그래프를 통하여 통계 자료로 제공함
    - '오늘의 공부' 그래프 : 공부 상태, 졸음 상태, 멍때림 상태를 시간에 따라 선형 그래프로 나타냄
    - '내가 정말 공부한 시간은?' 그래프 : 공부 상태, 졸음 상태, 멍때림 상태의 시간 비율을 파이 차트로 나타냄
    
#### version 1.0 (2021.10 ~ 2021.11.8)
+ Analysis of learners' real-time concentration (with yawning, blinking)
+ Statistical data : Daily learning time, weekly average learning time, concentration graph
#### version 1.1 (2021.11 ~ 2021.12)
+ Analysis of learners' real-time concentration (model update)
#### version 2.0 (2022.03 ~ 2022.05)
+ Analysis of learners' real-time concentration (with blinking, movement of eyes, movement of head)
+ Statistical date : Daily learning time, daily study graph

## Development Environment
+ Android Studio 4.2.2 - Build #AI-202.7660.26.42.7486908, built on June 24, 2021
+ MongoDB
+ Tensorflow-lite, ML kit Face detection

<img src="https://user-images.githubusercontent.com/55984242/140636896-929e0c3b-fb69-4da2-ad8c-b9fb5f2ae994.jpg" height="300px"/>

## Application Version
~~~
android {
    defaultConfig {
        minSdkVersion 21
        targetSdkVersion 30
    }
~~~

## How it works
:triangular_flag_on_post: you can watch how it works [HERE in YOUTUBE](https://www.youtube.com/watch?v=tVrpZoe7DSg)
