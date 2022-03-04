![logo](https://user-images.githubusercontent.com/2079561/134871554-4682d336-60a0-48d1-9dd3-6e8330e6e013.png)

# Unissey Android SDK

> An android SDK for Unissey's analyze API.

`unissey-sdk` is the android library that replicates Unissey's core javascript SDK to be used in android applications. The library can handle all data capture necessary to run Unissey's biometric algorithms and returns the result to the parent application.

1. [Installation](#1-installation)
2. [Getting Started](#2-getting-started)
3. [Reference](#3-reference)
4. [About](#4-about)

# 1. Installation

You must have access to `sdk-android` repository and have generated a [personal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token) with repo and read:packages permissions.

Start by importing our [Github repository](https://github.com/features/packages) in your project's `build.gradle` using your Github username and [personnal access token](https://docs.github.com/en/github/authenticating-to-github/creating-a-personal-access-token).

```groovy
// <project>/build.gradle
repositories {
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/unissey/sdk-android")
        credentials {
          username = project.findProperty("gpr.user") as String ?: System.getenv("<GITHUB_USERNAME_ENV_VARIABLE>") // Github username
          password = project.findProperty("gpr.key") as String ?: System.getenv("<GITHUB_PERSONAL_ACCESS_TOKEN_ENV_VARIABLE>") // Github personal access token
        }
    }
}
```

Next import our package:
```groovy
// <project>/build.gradle
dependencies {
    // ...
    implementation "com.unissey:unissey-sdk:<version>"
}
```

Now your all set 🔥 ! 

# 2. Getting Started

## Requirements 
This package has some project level requierements:
- This project uses [androidX](https://developer.android.com/jetpack/androidx)
- The host project must use [API 21](https://developer.android.com/studio/releases/platforms#5.0) and later.
- [View binding](https://developer.android.com/topic/libraries/view-binding) must be enabled.
- Using [androidx.activity](https://developer.android.com/jetpack/androidx/releases/activity) and [androidx.fragment](https://developer.android.com/jetpack/androidx/releases/fragment) is advised. 

## Common installations
There are 3 main uses cases to our SDK described below. One where the video is captured and sent back to you for your own processing and the others where the video is directly sent to our API.
### 2.1 Video Capture Use Case
If you do not wish to send the video to the Unissey's analyze API right away, you must simply define the `setOnAnalysisResultListener` callback to receive the video captured by the SDK, `doesUseAPI` to `false`, and an `outputStream` be provided to record the video in. In this case other api related parameters are unnecessary.
> Note: the ouput stream is **not** closed by the sdk. 
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.unissey.sdk.DsCameraFragment"
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
### 2.2 Api Call Use Case
The SDK can make the call to Unissey's analyze API automatically after capturing the video if you specify the appropriate parameters.
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.unissey.sdk.DsCameraFragment"
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

### 2.3 Api call with face comparison use case
The SDK can also join a reference picture to the api call to enable face comparison.
```xml
<!-- <project>/src/main/res/layout/mainActivity.xml -->
<!-- ... -->
<androidx.fragment.app.FragmentContainerView
        android:id="@+id/fragment_holder"
        android:name="com.unissey.sdk.DsCameraFragment"
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
| DSCameraFragment::doesUseAPI         | Boolean             | Indicate if the fragment will automatically call Unissey's analyze API or not. (cf. [2.2 Api Call Use Case](#2.2-Api-Call-Use-Case)) | false                                              |
| DsCameraFragment::gdprConsent        | Boolean             | Indicate if the user has given consent for use and storage of its personal data. (cf. [GDPR](https://gdpr.eu/))      | false                                              |
| DSCameraFragment::apiKey             | String              | Api Key to use when requesting Unissey's analyze API                                    | ""                                                 |
| DSCameraFragment::analyseBaseUrl     | String              | Base URL to use when requesting Unissey's analyze API                                   | <unissey analyze api url based on environnement> |
| DSCameraFragment::environment        | DeepsenseEnvironments | Unissey API environment to use for request to Unissey's analyze API                              | DeepsenseEnvironments::STAGING                       |
| DSCameraFragment::retriedSessionID   | String?             | Session ID of the retried session                                                         | null                                               |

## Functions
| Name                                                                                      | Arguments                                                                                           | Returns | Descriptions                                                                                                                                                |
|-------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|---------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| DSCameraFragment::setReferencePicture(image:ByteArray, name: String, mimeType: String) | image: reference picture for face comparison - name: original file name - mimeType: picture mime type | Unit    | Enable face comparison using the provided image as reference picture                                                                                          |
| DSCameraFragment::setReferencePicture(image:ByteArray)                                 | image: reference picture for face comparison                                                          | Unit    | Enable face comparison using the provided image as reference picture. The image will be assumed to be a JPEG file.                                            |
| DSCameraFragment::clearReferencePicture()                                                 |                                                                                                     | Unit    | Disable face comparison.                                                                                                                                      |
| DSCameraFragment::setOnAnalysisResultListener(listener: AnalysisResultListener)           | listener: callback called when the analysis is done.                                                | Unit    | The given callback will be called when the sdk finished analysis with api. Note: this callback will not be called if `doesUseAPI` is false                  |
| DSCameraFragment::setOnRecordEndedListener(listener: RecordEndedListener)                 | listener: callback called when the video recording is done.                                         | Unit    | The given callback will be called when the sdk has finished to record the video. Note: it will only be called if an output stream has been provided.          |
| DSCameraFragment::setOutputStream(out: OutputStream)                                      | out: stream where the video will be written.                                                        | Unit    | The function allows to provide an output stream to retrieve the video recorded by the sdk. Once a stream is provided the RecordEndedCallback will be called |

## Types
- AnalysisResultListener
  - Function 
  - `(results: AnalyseResults, err: Throwable?) -> Unit`
  - Contains analyze results or errors
- RecordEndedListener
  - Function
  - `(outputStream: OutputStream) -> Unit`
  - Contains the provided output stream to retrieve the video
- DeepsenseEnvironments
  - enum
  - values:
    - TEST
    - DEV
    - STAGING
    - PRODUCTION

## Styling
Some style attributes will modify the look ok the SDK:
- colorPrimary (primary color (buttons, progress bar...))
- overlayTextColor (color of the text on the overlay)
- overlayLogoBackgroundTint (color of Unissey logo on the overlay)
- overlayColor (color of the overlay)

# 4. About

## Support

`tech@unissey.com`

## License / Copyright

This SDK is distributed under Unissey license agreement
