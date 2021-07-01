_<p align="center">Deepsense Confidential</p>_
![logo](https://user-images.githubusercontent.com/14275989/73751192-e1433f00-475e-11ea-853d-50952fb27862.png)

# Deepsense React SDK

> An android SDK for deepsense analyze API. 

This project contains an UI fragment that can handle all the analyze pipeline from the video capture, to the video analysis using deepsense analyze API.

It can be used for fast and easy integration of our services.

1. [Installation](#1-installation)
2. [Getting Started](#2-getting-started)
3. [Reference](#3-reference)
4. [About](#4-about)

# 1. Installation

Start by importing our [Github repository](https://github.com/features/packages) in your project's `build.gradle` using your Github username and [personnal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token). Keep in mind that your access token must have at least read access to repositories.
```groovy
// <project>/build.gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/thedeepsense/sdk-android")
        credentials {
            username = project.findProperty("gpr.user") as String ?: System.getenv("<GITHUB_USERNAME_ENV_VARIABLE>") // Github username
            password = project.findProperty("gpr.key") as String ?: System.getenv("<GITHUB_PAT_ENV_VARIABLE>") // Github personal access token
        }
    }
}
```

Next import our package:
```groovy
// <project>/build.gradle
dependencies {
    // ...
    implementation "com.deepsense:dssdk:<version>"
}
```

Now your all set 🔥 ! 

# 2. Getting Started

## Project Requierements
This package has some project level requierements:
- The following keys needs to be added to the application theme(s)
    - [overlayTextColor](#Styling)
    - [overlayColor](#Styling)
    - [overlayLogoBackgroundTint](#Styling)
- This project use [androidX](https://developer.android.com/jetpack/androidx)

### Basic usage
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.deepsense.dssdk.DsCameraFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:tag="DSCameraFragmentHolder">

</androidx.fragment.app.FragmentContainerView>
<!-- ... -->
```
```kotlin
// <project>/main/java/<package>/MainActivity.kt
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ...
        
        val sdkFragment = supportFragmentManager.findFragmentByTag(SDK_FRAGMENT_TAG) as DsCameraFragment?
            ?: throw Error("Fragment with tag '$SDK_FRAGMENT_TAG' should exists")

        sdkFragment.apply {
            apiKey = YOUR_API_KEY
            doesUseAPI = true
            gdprConsent = true

            setOnAnalysisResultListener { results, err ->
                // handle analysis results
            }
        }

        // ...
    }
```

### Only take video
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.deepsense.dssdk.DsCameraFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:tag="DSCameraFragmentHolder">

</androidx.fragment.app.FragmentContainerView>
<!-- ... -->
```
```kotlin
// <project>/main/java/<package>/MainActivity.kt
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ...
        
        // Create a stream to write the video in
        val out = ByteArrayOutputStream()

        val sdkFragment = supportFragmentManager.findFragmentByTag(SDK_FRAGMENT_TAG) as DsCameraFragment?
            ?: throw Error("Fragment with tag '$SDK_FRAGMENT_TAG' should exists")

        sdkFragment.apply {
            doesUseAPI = false
            
            setOutputStream(out)
            setOnRecordEndedListener {
                // The video has been written in the given output stream.

                // handle recorded video
                // ...
                out.close()
            }
        }

        // ...
    }
```

### Do analysis with a reference picture (face comparison)
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.deepsense.dssdk.DsCameraFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:tag="DSCameraFragmentHolder">

</androidx.fragment.app.FragmentContainerView>
<!-- ... -->
```
```kotlin
// <project>/main/java/<package>/MainActivity.kt
 override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ...
        val pictureBytes = getReferencePictureByteArrayFromSomewhere()

        val sdkFragment = supportFragmentManager.findFragmentByTag(SDK_FRAGMENT_TAG) as DsCameraFragment?
            ?: throw Error("Fragment with tag '$SDK_FRAGMENT_TAG' should exists")

        sdkFragment.apply {
            apiKey = YOUR_API_KEY
            doesUseAPI = true
            gdprConsent = true

            setReferencePicture(pictureBytes, "originalPictureName.jpg", "image/jpeg")

            setOnAnalysisResultListener { results, err ->
                // handle analysis results
            }
        }

        // ...
    }
```

# 3. Reference

## Variables and attributes
| Name                                 | Type                | Description                                                                               | default                                            |
|--------------------------------------|---------------------|-------------------------------------------------------------------------------------------|----------------------------------------------------|
| DSCameraFragment::isReady            | Boolean (readonly)  | Indicate if the fragment is ready                                                         |                                                    |
| DSCameraFragment::isFaceMatchEnabled | Boolean (readonly)  | Indicate if the analyze will do face comparison or not                                    |                                                    |
| DSCameraFragment::doesUseAPI         | Boolean             | Indicate if the fragment will send or not the video to deeepsense analyze API for analyze | false                                              |
| DsCameraFragment::gdprConsent        | Boolean             | Indicate if the user has given consent for use of its personnal data. (cf. [GDPR](https://gdpr-info.eu/))      | false                                              |
| DSCameraFragment::apiKey             | String              | Api Key to use when requesting deepsense's analyze API                                    | ""                                                 |
| DSCameraFragment::analyseBaseUrl     | String              | Base URI to use when requesting deepsense's analyze API                                   | <deepsense analyze api url based on environnement> |
| DSCameraFragment::environment        | DeepensEnvironments | Deepsense API environment to use for request to analyze API                               | DeepensEnvironments::STAGING                       |
| DSCameraFragment::retriedSessionID   | String?             | Session ID of the retried session                                                         | null                                               |

## Functions
| Name                                                                                      | Arguments                                                                                           | Returns | Descriptions                                                                                                                                                |
|-------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DSCameraFragment::setReferencePicture(image:ByteArray, name: String, mimeType: String) | image: reference picture for face comparison - name: original file name - mimeType: picture mime type | Unit    | Enable face comparison using the provided image as reference picture                                                                                          |
| DSCameraFragment::setReferencePicture(image:ByteArray)                                 | image: reference picture for face comparison                                                          | Unit    | Enable face comparison using the provided image as reference picture. The image will be assumed to be a JPEG file.                                            |
| DSCameraFragment::clearReferencePicture()                                                 |                                                                                                     | Unit    | Disable face comparison.                                                                                                                                      |
| DSCameraFragment::setOnAnalysisResultListener(listener: AnalysisResultListener)           | listener: callback called when the analysis is done.                                                | Unit    | The given callback will be called when the sdk finished analysis with api. Note: this callback will not be called if `doesUseAPI` is false                  |
| DSCameraFragment::setOnRecordEndedListener(listener: RecordEndedListener)                 | listener: callback called when the video recording is done.                                         | Unit    | The given callback will be called when the sdk finished to record the video. Note that it will only be called if a output stream as been provided           |
| DSCameraFragment::setOutputStream(out: OutputStream)                                      | out: Stream where the video will be written.                                                        | Unit    | The function allows to provide an output stream to retrieve the video recorded by the sdk. Once a stream is provided the RecordEndedCallback will be called |

## Types
- AnalysisResultListener
  - Function 
  - `(results: AnalyseResults, err: Throwable?) -> Unit`
  - Contains analyze results or errors
- RecordEndedListener
  - Function
  - `(outputStream: OutputStream) -> Unit`
  - Contains the provided output stream to retrieve the video
- DeepensEnvironments
  - enum
  - values:
    - TEST
    - DEV
    - STAGING
    - PRODUCTION

## Styling
Some style attribute will modify the look ok the SDK:
- colorPrimary (primary color (buttons, progress bar...))
- overlayTextColor (color of the text on the overlay)
- overlayLogoBackgroundTint (color of deepsense logo on the overlay)
- overlayColor (color of the overlay)

# 4. About

## Versions

| Version       | Date       | Description            |
| ------------- | ---------- | ---------------------- |
| 0.1.0-alpha   | 2021-05-12 | initial release        |


## Support

`tech@thedeepsense.co`

## License / Copyright

This SDK is distributed under Deepsense license agreement