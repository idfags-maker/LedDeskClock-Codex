# LED 탁상시계

Android 13(API 33) 이상에서 사용하는 전체화면 탁상시계입니다. 검은 배경 위에 빨간색 7세그먼트 LED 숫자로 현재 시각을 크게 표시합니다. 가로·세로 화면에 자동으로 맞춰집니다.

## 사용 방법

- 앱을 실행하면 기기의 현재 시각과 날짜가 표시됩니다.
- 화면을 탭하면 24시간/12시간 표시, 초 표시, 야간 모드를 설정할 수 있습니다. 설정은 앱을 다시 실행해도 유지됩니다.
- 앱을 보고 있는 동안 화면이 자동으로 꺼지지 않습니다. 홈 화면으로 나가면 일반 화면 꺼짐 정책이 적용됩니다.
- 화면 가장자리에서 스와이프하면 시스템 탐색 버튼을 일시적으로 표시할 수 있습니다. 설정의 ‘시계 종료’로도 종료할 수 있습니다.
- 시간 및 시간대는 기기 설정을 따릅니다. 오프라인 기기에서는 Android 설정에서 시간을 맞춰 주세요.

광고, 분석 SDK, 인터넷 권한, 외부 런타임 라이브러리가 없습니다. 설치 후 모든 기능은 인터넷 없이 작동합니다. 장시간 켜 둘 때는 야간 모드나 낮은 기기 밝기를 권장합니다.

## APK 빌드: Android Studio

1. 이 폴더를 압축 해제한 후 Android Studio에서 `LedDeskClock` 폴더를 엽니다.
2. Gradle JDK를 **JDK 17**로 설정합니다.
3. SDK Manager에서 **Android SDK Platform 35**와 **Android SDK Build-Tools 34.0.0**을 설치하고 SDK 라이선스에 동의합니다.
4. Gradle 동기화를 완료한 뒤 터미널에서 아래 명령을 실행합니다.

Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

macOS / Linux:

```sh
chmod +x gradlew
./gradlew assembleDebug
```

결과 APK: `app/build/outputs/apk/debug/app-debug.apk`

Debug APK는 개발용 키로 자동 서명되며 기기에 바로 설치할 수 있습니다. USB 디버깅 및 Android SDK Platform-Tools가 있으면 `adb install -r app/build/outputs/apk/debug/app-debug.apk`로 설치합니다. 파일을 기기로 복사해 설치할 수도 있습니다.

## 명령줄 환경 설정

JDK 17의 위치를 `JAVA_HOME`에, Android SDK 위치를 `ANDROID_HOME`에 설정합니다. 또는 프로젝트 루트의 `local.properties`에 SDK 경로를 지정합니다. Windows 예시:

```properties
sdk.dir=C:/Users/YOUR_NAME/AppData/Local/Android/Sdk
```

공식 Gradle Wrapper(JAR 및 Windows/Unix 실행 스크립트)가 포함되어 있어 별도 Gradle 설치는 필요하지 않습니다. 최초 빌드는 Gradle, Android 빌드 플러그인 및 SDK 다운로드에 인터넷이 필요합니다. **앱 실행 시 인터넷은 필요하지 않습니다.**

배포용 APK가 필요하면 Android Studio의 Generate Signed App Bundle / APK에서 APK를 선택하고 본인의 키 저장소로 서명하세요. 서명 키와 암호는 소스에 포함하지 마세요.

## 구성

- Java 17 / Android 기본 View + Canvas
- Android Gradle Plugin 8.7.3 / Gradle 8.9
- compileSdk 35 / targetSdk 33 / minSdk 33
- `MainActivity.java`: 전체화면, 화면 유지, 수명 주기, 설정 저장
- `ClockView.java`: 반응형 LED 숫자, 시각 및 날짜 표시
- 1초마다 기기 시각을 다시 읽고, 백그라운드에서는 갱신 작업을 중지합니다.

## 검증 범위

프로젝트 XML 구문, 인터넷 권한 부재, Gradle Wrapper 공식 SHA-256 체크섬을 확인했습니다. 제작 환경에는 JDK와 Android SDK가 없어 APK 컴파일 및 Android 13 기기 실행 검증은 수행하지 못했습니다. APK 파일은 이 패키지에 포함되어 있지 않습니다.

빌드 후 기기에서 확인할 항목:

1. 비행기 모드에서 시각과 날짜가 계속 갱신되는지 확인합니다.
2. 가로·세로 회전, 초 표시 전환, 12시간제의 자정/정오 표시를 확인합니다.
3. 설정 저장, 야간 모드 및 종료 후 밝기 복원을 확인합니다.
4. 앱을 보고 있을 때 화면이 유지되고, 홈으로 나간 뒤에는 정상적으로 꺼지는지 확인합니다.
5. 기기 시각/시간대 변경 후 앱으로 돌아와 새 시각이 표시되는지 확인합니다.

## 참고 문서

- [Android 전체화면 처리](https://developer.android.com/develop/ui/views/layout/immersive)
- [앱 화면 유지](https://developer.android.com/develop/background-work/background-tasks/awake/screen-on)
- [Android Gradle Plugin 8.7 호환성](https://developer.android.com/build/releases/agp-8-7-0-release-notes)
