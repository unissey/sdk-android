![Unissey logo](images/unissey-logo.png)

<!-- Using HTML notation instead of MD to prevent the TOC generation from picking up this header --> 
<h1>Unissey Android SDK</h1>

[![GitHub release (with filter)](https://img.shields.io/github/v/release/unissey/sdk-android)](https://github.com/unissey/sdk-android/packages/1234777)

This Android Library provides an easy way to obtain a video selfie to be used with Unissey's SaaS
solution on an Android application. This SDK has been developed with Android Compose, allowing for
an easy integration on both Android Compose apps and traditional Android Views apps.

<!-- @formatter:off -->
<!-- TOC -->
  * [1. Installation & requirements](#1-installation--requirements)
    * [1.1 Requirements](#11-requirements)
    * [1.2 Installation](#12-installation)
      * [1.2.1 Get a GitHub personal access token](#121-get-a-github-personal-access-token)
      * [1.2.2 Configure your repositories to include Unissey's package repository](#122-configure-your-repositories-to-include-unisseys-package-repository)
      * [1.2.3 Add a dependency to Unissey's SDK in your application](#123-add-a-dependency-to-unisseys-sdk-in-your-application)
  * [2. Getting started](#2-getting-started)
    * [2.1 Overview](#21-overview)
    * [2.2 UnisseyViewModel](#22-unisseyviewmodel)
    * [2.3 UnisseyScreen](#23-unisseyscreen)
      * [2.3.1 Android Compose](#231-android-compose)
      * [2.3.2 Traditional Android Views](#232-traditional-android-views)
  * [3. Reference](#3-reference)
    * [3.1 AcquisitionPreset](#31-acquisitionpreset)
    * [3.2 OnRecordEndedListener](#32-onrecordendedlistener)
    * [3.3 OnStateChangedListener](#33-onstatechangedlistener)
    * [3.4 SessionConfig](#34-sessionconfig)
    * [3.5 UnisseyViewModel's public variables and functions](#35-unisseyviewmodels-public-variables-and-functions)
    * [3.6 String resources](#36-string-resources)
    * [3.7 Colors](#37-colors)
    * [3.8 Images](#38-images)
    * [3.9 Typography](#39-typography)
  * [4. Advanced usage](#4-advanced-usage)
    * [4.1 Specifying a SessionConfig](#41-specifying-a-sessionconfig)
    * [4.2 Customizing the texts and translations](#42-customizing-the-texts-and-translations)
    * [4.3 Customizing the colors](#43-customizing-the-colors)
      * [4.3.1 Android Compose](#431-android-compose)
      * [4.3.2 Traditional Android Views](#432-traditional-android-views)
    * [4.4 Customizing the images](#44-customizing-the-images)
    * [4.5 Customizing the types](#45-customizing-the-types)
    * [4.6 Auto-starting the video capture when the camera's ready](#46-auto-starting-the-video-capture-when-the-cameras-ready)
      * [4.6.1 Android Compose](#461-android-compose)
      * [4.6.2 Traditional Android Views](#462-traditional-android-views)
    * [4.7 Adapting the content padding](#47-adapting-the-content-padding)
      * [4.7.1 Android Compose](#471-android-compose)
      * [4.7.2 Traditional Android Views](#472-traditional-android-views)
    * [4.8 Enabling Injection Attack Detection (IAD)](#48-enabling-injection-attack-detection-iad)
      * [4.8.1 Obtain the IAD data from your Back-End](#481-obtain-the-iad-data-from-your-back-end)
      * [4.8.2 Create an IadConfig to add to the SessionConfig](#482-create-an-iadconfig-to-add-to-the-sessionconfig)
      * [4.8.3 Send the metadata along with the video to the /analyze endpoint](#483-send-the-metadata-along-with-the-video-to-the-analyze-endpoint)
  * [5. Common issues](#5-common-issues)
    * [5.1 Android Studio reporting string resources not translated in French](#51-android-studio-reporting-string-resources-not-translated-in-french)
<!-- TOC -->
<!-- @formatter:on -->

## 1. Installation & requirements

### 1.1 Requirements

The minimum API level of your app must be at least
21 ([Android 5.0](https://developer.android.com/tools/releases/platforms#5.0)) in order to use this
SDK.

### 1.2 Installation

If you already a GitHub personal access token with the proper permissions, you can skip to the
second step of the installation.

#### 1.2.1 Get a GitHub personal access token

First, you need to generate an access token by following the instructions
provided [here](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
by GitHub. Make sure to include at least the `repo` and `read:packages` permissions.

Then, to gain access to Unissey's SDK, you need to contact the Unissey team and provide your GitHub
account name.

#### 1.2.2 Configure your repositories to include Unissey's package repository

In your project's `settings.gradle`, add a repository entry pointing to our package repository using
your GitHub personal token like so:

```groovy
dependencyResolutionManagement {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/unissey/sdk-android")
            credentials {
                username = System.getenv("<GITHUB_USERNAME_ENV_VARIABLE>")
                password = System.getenv("<GITHUB_PERSONAL_ACCESS_TOKEN_ENV_VARIABLE>")
            }
        }
    }
}
```

You can of course choose to store your GitHub username and access token somewhere else than the
environment variables.

#### 1.2.3 Add a dependency to Unissey's SDK in your application

In your app's `build.gradle` this time, add a dependency to the SDK like so:

```groovy
dependencies {
    implementation 'com.unissey:unissey-sdk:3.1.3'
}
```

**NOTE:** Make sure to use the latest version that can be
found [here](https://github.com/unissey/sdk-android/packages/1234777).

## 2. Getting started

### 2.1 Overview

Unissey's SDK offers three screens, one of which is optional:

1. If you choose to, the user is greeted with a screen displaying instructions on what to do to
   ensure our algorithms are delivering the best results possible.
2. Then, if your application hasn't already asked for the user's permission to use the camera, a
   screen presenting a brief explanation on why the app requires the use of the camera is displayed.
   A button on this page will make the system dialog appear to request the user's consent to let the
   app use the camera. If the user happens to decline, subsequent clicks on this button will
   redirect to the settings page where the user will have to manually grant permission to the
   camera.
3. Finally, if the permission has been granted, the main screen is displayed featuring a live
   preview of the camera. This screen provides a button (or none if you choose to hide it) that
   starts the video capture process. This process is divided in two steps. The SDK will start by
   detecting the user's face and providing indications, based on the face's location, to help the
   user position their face in the center of the frame. Once it's done and the user stayed still for
   at least one second, the actual video recording will start for the duration you specified with
   the
   Acquisition Preset.

The sample apps are here to provide a basic implementation of this library that you can use as a
base for your integration inside your own Android application. Whether you're using Android Compose
or a traditional approach with Activities and Fragments, you will have to create
a `UnisseyViewModel` and a `UnisseyScreen` to interact with this SDK. The `UnisseyViewModel` is the
developer interface with which you can interact in your application's code, it's also the class that
you can configure to suit your needs. The `UnisseyScreen` on the other hand holds the user interface
and it's what your user will see and interact with.

### 2.2 UnisseyViewModel

The `UnisseyViewModel` class offers a `Factory` companion object that should be used to create an
instance of it. Only an `AcquisitionPreset` object and an `OnRecordEndedListener` are necessary to
start the SDK. However, it can be further customized with an `OnStateChangedListener` and
a `SessionConfig`. All of these parameters are detailed in the [Reference](#3-reference) section.

See the following code for simple usages where the default values are suitable for your use case:

```kotlin
// In Kotlin
val unisseyViewModel: UnisseyViewModel by viewModels {
    UnisseyViewModel.Factory.create(
        acquisitionPreset = SelfieFast
    ) { result ->
        // `result` contains the response or an error
        // SessionResult contains convenience functions to access the content of `result`.
        // You can use the function `getOrThrow()` or `getOrNull()` or a classic when(result)
        // or if(result is ...) and act according to its type
        val response = result.getOrNull()
        Log.d(
            "UnisseySdk",
            "Video record ended with file path: ${response?.videoFilePath ?: "null"}"
        )
    }
}
```

<!-- @formatter:off -->
```java
// In Java
UnisseyViewModel unisseyViewModel = new ViewModelProvider(
    this,
    UnisseyViewModel.Factory.create(SelfieFast.INSTANCE, result -> {
        // `result` contains the response or an error
        // Just like in Kotlin, you can use the convenience functions or a more classic approach
        SessionResponse response = result.getOrNull();
        Log.d(
            "UnisseySdk",
            "Video record ended with file path: " + response.getVideoFilePath()
        );
    })
).get(UnisseyViewModel.class);
```
<!-- @formatter:on -->

### 2.3 UnisseyScreen

Once you have an instance of `UnisseyViewModel`, you can create the `UnisseyScreen` with the
ViewModel as a parameter.

#### 2.3.1 Android Compose

In Compose, you can use the `UnisseyScreen` as you would use any other Composable in your
application:

```kotlin
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // See the code in the previous section for how to create a UnisseyViewModel
        val unisseyViewModel = ...

        setContent {
            MyAppTheme {
                UnisseyScreen(
                    unisseyViewModel = unisseyViewModel
                )
            }
        }
    }
}
```

#### 2.3.2 Traditional Android Views

If your application is built with the older Views system using Activities and Fragments, the most
recommended way to create a `UnisseyScreen` is by encapsulating it in a dedicated Fragment.

The SDK provides a helper function `getUnisseyView()` to easily build a `UnisseyScreen` View for
simpler use cases, using the Android's interoperability API. It spares you from adding a Compose UI
dependency if you don't need it in your application.

If this helper function isn't suited for your needs, you can always implement it yourself with the
help of
the [official documentation](https://developer.android.com/jetpack/compose/migrate/interoperability-apis/compose-in-views).
If you choose to go this route, you will need to add the Compose UI dependency. To do so, simply add
this line in your `build.gradle`'s `dependencies` section:

```groovy
implementation 'androidx.compose.ui:ui:1.4.3'
```

Don't forget to check the latest version of this
package [here](https://mvnrepository.com/artifact/androidx.compose.ui/ui) to benefit from the latest
updates.

⚠️ **NOTE:** An important thing to keep in mind with this approach is that, if you're interacting
with a class variable inside your `OnRecordEndedListener`, like saving the result's video URI to
another ViewModel from your application, the reference of said ViewModel would change upon a
configuration change. That means if your user rotates the screen of his phone for example, causing a
screen orientation change, the Fragment would get recreated and the reference to these variables
would change. This would break your callback's code if you're referencing any of these variables in
it.
To avoid this, you can reassign the callback after the `UnisseyViewModel` has been
created. It should be updated everytime the Fragment is recreated to keep the reference to the
context up to date.

```kotlin
// In Kotlin
class UnisseyFragment : Fragment() {
    // Example of a shared ViewModel where the video URI should be saved
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val onRecordEndedListener: OnRecordEndedListener = { result ->
        val data = result.getOrThrow()
        Log.d(
            "UnisseyDemo",
            "Video record ended with file path: '${data.videoFilePath}"
        )
        sharedViewModel.setVideoUri(data.videoFilePath)
        // Here you can navigate to another Fragment
    }

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        UnisseyViewModel.Factory.create(
            SelfieFast,
            onRecordEndedListener = onRecordEndedListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Update the OnRecordEndedListener since the reference to SharedViewModel has changed in
        // case of a Fragment recreation (it happens upon configuration changes such as a change
        // in the screen orientation)
        unisseyViewModel.onRecordEndedListener = onRecordEndedListener

        return getUnisseyView(requireContext(), unisseyViewModel)
    }
}
```

```java
// In Java
public class UnisseyFragment extends Fragment {
    // Example of a shared ViewModel where the video URI should be saved
    private SharedViewModel sharedViewModel;

    private final OnRecordEndedListener onRecordEndedListener = (SessionResult result) -> {
        SessionResponse response = result.getOrThrow();
        Log.d(
                "UnisseyDemo",
                "Video record ended with file path: " + response.getVideoFilePath()
        );
        sharedViewModel.setVideoUri(response.getVideoFilePath());
        // Here you can navigate to another Fragment
    };

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        UnisseyViewModel unisseyViewModel =
                new ViewModelProvider(this,
                        UnisseyViewModel.Factory.create(SelfieFast.INSTANCE,
                                onRecordEndedListener)).get(UnisseyViewModel.class);

        // Update the OnRecordEndedListener since the reference to SharedViewModel has changed in
        // case of a Fragment recreation (it happens upon configuration changes such as a change
        // in the screen orientation)
        unisseyViewModel.setOnRecordEndedListener(onRecordEndedListener);

        return UnisseyViewKt.getUnisseyView(requireContext(), unisseyViewModel);
    }
}
```

⚠️ **NOTE:** If your application is written in Java, a crash might happen when instantiating the
UnisseyView unless you add this plugin to your `build.gradle` file:

```groovy
plugins {
    id 'org.jetbrains.kotlin.android'
}
```

This problem started happening with the version `2024.01.00` of `androidx.compose:compose-bom` that
is used in Unissey's SDK to choose coherent versions of Compose.

## 3. Reference

### 3.1 AcquisitionPreset

The present SDK provides presets defining how the video is recorded. Even if you decide to override
its values, you must select a preset when creating an instance of `UnisseyViewModel`.
Here are the 2 current possible values of `AcquisitionPreset`:

| Preset name       | Recording duration | Video quality                                                      | Description                                                                                                                        |
|-------------------|--------------------|--------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|
| SelfieFast        | 1 second           | 720p resolution, with a "HigherQualityThan(HD)"  fallback strategy | The preset that most use cases rely on. It provides the minimal configuration needed for Unissey's AI models to work at their best |
| SelfieSubstantial | 3 seconds          | 720p resolution, with a "HigherQualityThan(HD)" fallback strategy  | The preset fit for use cases aiming for a PVID substantial compliance                                                              |

### 3.2 OnRecordEndedListener

This is the callback that triggers when the user is done recording a video.

This is a type alias for `(result: SessionResult) -> Unit`.

Since type aliases aren't interoperable with Java, a convenience interface is also available to
provide a more idiomatic way to declare this callback for Java developers:

```java
public interface OnRecordEndedListener {
    void onRecordEnded(SessionResult result);
}
```

A `SessionResult` is a sealed class that can either be a `Success` enclosing a `SessionResponse`
object or a `Failure` enclosing an `Exception`.

The `SessionResponse`:

| Parameter name | Type   | Description                                                                                          |
|----------------|--------|------------------------------------------------------------------------------------------------------|
| videoFilePath  | String | The path to the video file saved in the cache directory of your app                                  |
| metadata       | String | A String containing technical metadata useful to Unissey to be added to the request to Unissey's API |

### 3.3 OnStateChangedListener

This is a callback that triggers everytime there's a significant change of state while the user is
interacting with the SDK. This callback offers a possibility for your application to react to SDK
and user events while the SDK is being used. This allows for more advanced usages that you could
have, some of which are described in the [Advanced usages](#4-advanced-usage) section.

This is a type alias for `(unisseyEvent: UnisseyEvent) -> Unit`.

For the reason stated in the previous section, a Java interface is available as well:

```java
public interface OnStateChangedListener {
    void onStateChanged(UnisseyEvent unisseyEvent);
}
```

A `UnisseyEvent` represents an event happening inside the SDK, often initiated by the user. Here's
an exhaustive list of possible events:

| Event                     | Description                                                                                                                                                                                                                                                                              |
|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| InstructionsShown         | When the Instructions screen is displayed to the user                                                                                                                                                                                                                                    |
| CameraPermissionShown     | When the Camera Permission screen is displayed to the user. This screen provides a simple explanation as to why this permission is necessary                                                                                                                                             |
| CameraPermissionRequested | When the camera permission has been requested, meaning when the system alert is being displayed to the user                                                                                                                                                                              |
| CameraPermissionAccepted  | When the user has accepted the permission                                                                                                                                                                                                                                                |
| CameraPermissionDenied    | When the user has declined the permission                                                                                                                                                                                                                                                |
| CameraPreviewShown        | When the Video Capture screen is displayed, showing a live preview of the camera                                                                                                                                                                                                         |
| CameraReady               | There is an initialization delay before the camera is fully set up and available. This event fires when the camera is actually ready to record a video.                                                                                                                                  |
| VideoCaptureStarted       | When the video capture has started. The term "video capture" refers to the process that begins when the user hits the "Start" button. The recording doesn't start right away, there's a first stage where a face detection is happening to help the user position their face in the oval |
| VideoRecordStarted        | When the recording has started                                                                                                                                                                                                                                                           |
| VideoRecordProgress       | This event is the only one to feature a parameter. It fires multiple times during the recording and provides a `progress` parameter indicating the recording progress with a value contained between 0 and 1 (1 being 100%)                                                              |
| VideoRecordEnded          | When the recording is over. This event triggers along with the `OnRecordEndedListener` callback                                                                                                                                                                                          |

### 3.4 SessionConfig

If the default state of the SDK doesn't suit your needs, the `UnisseyViewModel` can take
a `SessionConfig` parameter with ways to customize the behavior of the library.

Here's what a `SessionConfig` is composed of:

| Parameter name  | Type            | Description                                                     |
|-----------------|-----------------|-----------------------------------------------------------------|
| recordingConfig | RecordingConfig | A nested configuration object meant for technical configuration |
| uiConfig        | UiConfig        | A nested configuration object meant for graphic configuration   |

The `RecordingConfig`:

| Parameter name      | Type                  | Default value | Description                                                                                                                                                                                                                                                                                           |
|---------------------|-----------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| recordingDurationMs | Int?                  | null          | The duration of the recording, overrides the value from the `AcquisitionPreset`                                                                                                                                                                                                                       |
| qualitySelector     | VideoQualitySelector? | null          | An object mimicking the [CameraX class](https://developer.android.com/reference/androidx/camera/video/QualitySelector) `QualitySelector` to avoid having to add the CameraX to your project if you're not already using it. Use this parameter to override the quality set in the `AcquisitionPreset` |

The `VideoQualitySelector`:

| Parameter name   | Type                    | Description                                                                                                                                                                                                                          |
|------------------|-------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| qualities        | VideoQuality            | An enum mapping the CameraX' `Quality` values (SD, HD, FHD, UHD, LOWEST, HIGHEST)                                                                                                                                                    |
| fallbackStrategy | QualityFallbackStrategy | A sealed class mapping the CameraX' `FallbackStrategy`. An instance of either `LowerQualityThan(quality)`, `HigherQualityThan(quality)`, `LowerQualityOrHigherThan(quality)` or `HigherQualityOrLowerThan(quality)` must be provided |

The `UiConfig`:

| Parameter name                        | Type     | Default value                       | Description                                                                                                                                                                                                                                                                                |
|---------------------------------------|----------|-------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| darkTheme                             | Boolean? | null                                | Specify whether to force the dark theme (if set to `true`), force a light theme (if set to `false`) or leave the decision to the user's system (if set to `null`)                                                                                                                          |
| showInstructions                      | Boolean  | true                                | Specify whether to show the first instructions screen or not                                                                                                                                                                                                                               |
| showVideoCaptureButton                | Boolean  | true                                | Specify whether to show the "Start" button on the video capture screen or not. This is mainly useful if you choose to enable auto-starting of the video capture, as explained in the [Auto-starting the video capture](#46-auto-starting-the-video-capture-when-the-cameras-ready) section |
| showWideWindowPreviewInputsToTheRight | Boolean  | true                                | Specify whether the preview inputs should be displayed to the right of the camera preview or to the left in wide window mode (typically on phones in landscape mode)                                                                                                                       | 
| buttonCornerRadius                    | Float?   | null (which leads to radius of 7dp) | Set the corner radius of the buttons present in the SDK's interfaces                                                                                                                                                                                                                       |

### 3.5 UnisseyViewModel's public variables and functions

A few variables and functions in the `UnisseyViewModel` are accessible in read or write mode from
the client's application. Here's the exhaustive list:

| Name              | Type                    | Description                                                                                                                                              |
|-------------------|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| currentPage       | UnisseyPage (read only) | Indicates which screen is being displayed to the user. It's an enum with values comprised in `INSTRUCTIONS`, `CAMERA_PERMISSION` and `VIDEO_CAPTURE`     |
| navigateUp        | Function                | Tells the `UnisseyViewModel` to navigate one screen up                                                                                                   |
| startVideoCapture | Function                | Tells the `UnisseyViewModel` to start the video capture process. Useful when you want the video capture to autostart when the screen appears for example |

### 3.6 String resources

Here's an exhaustive list of the String resources used in the SDK and that can be overridden (see
the [Customizing the texts and translations](#42-customizing-the-texts-and-translations) section to
know how to override them):

| Key                                          | English value                                                                                    | French value                                                                                              |
|----------------------------------------------|--------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------|
| unissey_instructions_title                   | Record a short video selfie                                                                      | Enregistrez un selfie vidéo                                                                               |
| unissey_instructions_subtitle                | Let's make sure no one is impersonating you.                                                     | Cette étape permet de vérifier que personne n'usurpe votre identité                                       |
| unissey_instruction_maintain_stable_position | Look straight at the camera, and keep your face clearly visible                                  | Maintenez votre visage droit et entièrement visible                                                       |
| unissey_instruction_plain_expression         | Have a plain expression                                                                          | Conservez une expression neutre                                                                           |
| unissey_instruction_well_lit_environment     | Stand in a well-lit environment                                                                  | Assurez-vous d’être suffisamment éclairé                                                                  |
| unissey_continue_label                       | Continue                                                                                         | Continuer                                                                                                 |
| unissey_camera_permission_explanation        | The camera permission is required for this feature to be available. Please grant the permission. | L'utilisation de la caméra est nécessaire à cette fonctionnalité. Merci d'autoriser l'usage de la caméra. |
| unissey_camera_permission_button             | Request permission                                                                               | Donner la permission                                                                                      |
| unissey_video_capture_title                  | The acquisition will last %d second(s).                                                          | L'acquisition durera %d seconde(s).                                                                       |
| unissey_start_label                          | Start                                                                                            | Commencer                                                                                                 |
| unissey_instruction_position_face_oval       | Position your face in the oval                                                                   | Placez-vous dans l'ovale                                                                                  |
| unissey_instruction_multiple_faces_detected  | Multiple faces detected                                                                          | Plusieurs visages détectés                                                                                |
| unissey_instruction_no_face_detected         | No face detected                                                                                 | Aucun visage détecté                                                                                      |
| unissey_instruction_get_closer               | Get closer                                                                                       | Rapprochez-vous                                                                                           |
| unissey_instruction_get_further_away         | Get further away                                                                                 | Reculez-vous                                                                                              |
| unissey_instruction_move_right               | Move your face to the right                                                                      | Plus vers la droite                                                                                       |
| unissey_instruction_move_left                | Move your face to the left                                                                       | Plus vers la gauche                                                                                       |
| unissey_instruction_move_up                  | Move your face up                                                                                | Plus vers le haut                                                                                         |
| unissey_instruction_move_down                | Move your face down                                                                              | Plus vers le bas                                                                                          |
| unissey_instruction_do_not_move              | Perfect, don't move                                                                              | Parfait, ne bougez plus                                                                                   |

### 3.7 Colors

This SDK relies on Material 3 for theming. It provides a set of colors that can be overridden (see
[Customizing the colors](#43-customizing-the-colors) section to know how to override them).

Here's the exhaustive list of colors used in the SDK along with their default values corresponding
to the Unissey color theme:

| Key                                        | Default color value     |
|--------------------------------------------|-------------------------|
| unissey_theme_color_light_primary          | `#0A175C`               |
| unissey_theme_color_light_onPrimary        | `#FFFFFF`               |
| unissey_theme_color_light_secondary        | `#3D59E8`               |
| unissey_theme_color_light_surface          | `#FFFBFE`               |
| unissey_theme_color_light_onSurface        | `#1C1B1F`               |
| unissey_theme_color_light_surfaceVariant   | `#F6F6F6`               |
| unissey_theme_color_light_onSurfaceVariant | `#49454F`               |
| unissey_theme_color_light_scrim            | `#FFFFFF` - 80% opacity |
| unissey_theme_color_dark_primary           | `#3D59E8`               |
| unissey_theme_color_dark_onPrimary         | `#FFFFFF`               |
| unissey_theme_color_dark_secondary         | `#3D59E8`               |
| unissey_theme_color_dark_surface           | `#1C1B1F`               |
| unissey_theme_color_dark_onSurface         | `#E6E1E5`               |
| unissey_theme_color_dark_surfaceVariant    | `#49454F`               |
| unissey_theme_color_dark_onSurfaceVariant  | `#CAC4D0`               |
| unissey_theme_color_dark_scrim             | `#000000` - 80% opacity |

### 3.8 Images

This SDK contains customizable images (see
[Customizing the images](#44-customizing-the-images) section
to know how to override them). Those images illustrate the three instructions on the first optional
screen.

Here's the list of images:

| Drawable name                     | Description                                                            |
|-----------------------------------|------------------------------------------------------------------------|
| unissey_face_position_picto.xml   | The first illustration showing how to position the user's face         |
| unissey_face_expression_picto.xml | The second illustration indicating to have a neutral expression        |
| unissey_face_light_picto.xml      | The third illustration that displays that a good lighting is necessary |

### 3.9 Typography

In the same spirit as the colors variables, the SDK provides a set of types used throughout the
different screens. They can be overridden in the same manner as the colors (
see [Customizing the types](#45-customizing-the-types) section to know how). Those types are divided
into three categories: Compact, Medium and Expanded. Those categories correspond to the Window Size
Classes defined by
the [official documentation](https://developer.android.com/guide/topics/large-screens/support-different-screen-sizes#window_size_classes)
and ensure a correct display on all form factors.

Here's the exhaustive list of types along with their default values:

**Compact**

| Key                                       | Default family | Default weight | Default size |
|-------------------------------------------|----------------|----------------|--------------|
| unissey_theme_type_compact_headlineMedium | System Default | Bold           | 24 sp        |
| unissey_theme_type_compact_titleSmall     | System Default | Bold           | 18 sp        |
| unissey_theme_type_compact_bodySmall      | System Default | Normal         | 14 sp        |
| unissey_theme_type_compact_bodyMedium     | System Default | Normal         | 16 sp        |
| unissey_theme_type_compact_labelMedium    | System Default | Medium         | 14 sp        |

**Medium**

| Key                                      | Default family | Default weight | Default size |
|------------------------------------------|----------------|----------------|--------------|
| unissey_theme_type_medium_headlineMedium | System Default | Bold           | 28 sp        |
| unissey_theme_type_medium_titleSmall     | System Default | Bold           | 22 sp        |
| unissey_theme_type_medium_bodySmall      | System Default | Normal         | 18 sp        |
| unissey_theme_type_medium_bodyMedium     | System Default | Normal         | 20 sp        |
| unissey_theme_type_medium_labelMedium    | System Default | Medium         | 16 sp        |

**Expanded**

| Key                                        | Default family | Default weight | Default size |
|--------------------------------------------|----------------|----------------|--------------|
| unissey_theme_type_expanded_headlineMedium | System Default | Bold           | 32 sp        |
| unissey_theme_type_expanded_titleSmall     | System Default | Bold           | 24 sp        |
| unissey_theme_type_expanded_bodySmall      | System Default | Normal         | 22 sp        |
| unissey_theme_type_expanded_bodyMedium     | System Default | Normal         | 24 sp        |
| unissey_theme_type_expanded_labelMedium    | System Default | Medium         | 18 sp        |

## 4. Advanced usage

### 4.1 Specifying a SessionConfig

To give you an idea of how you can customize the SDK to fit your needs using the `SessionConfig`,
here's an example use case. Say you're happy with the SDK but you'd rather have your own
instructions page, to make sure it fits your UI Design. It also happens that your application
doesn't support the dark mode yet, and you don't want your user to have access to a single page in
dark mode among pages in light mode. Finally, you want the video recording to last 2 seconds instead
of the default 1 second of the `SelfieFast` preset, for whatever reason.

You could leverage the `SessionConfig` to achieve this:

```kotlin
// In Kotlin
val recordingConfig = RecordingConfig(2000)
val uiConfig = UiConfig(darkTheme = false, showInstructions = false)
val sessionConfig = SessionConfig(recordingConfig, uiConfig)

val unisseyViewModel: UnisseyViewModel by viewModels {
    UnisseyViewModel.Factory.create(
        SelfieFast,
        sessionConfig,
    ) { result ->
        ...
    }
}
```

<!-- @formatter:off -->
```java
// In Java
RecordingConfig recordingConfig = new RecordingConfig(2000);
UiConfig uiConfig = new UiConfig(false, false);
SessionConfig sessionConfig = new SessionConfig(recordingConfig, uiConfig);

UnisseyViewModel unisseyViewModel =
        new ViewModelProvider(this,
                UnisseyViewModel.Factory.create(SelfieFast.INSTANCE,
                        sessionConfig,
                        result -> {
                        ...
                        })
        ).get(UnisseyViewModel.class);
```
<!-- @formatter:on -->

### 4.2 Customizing the texts and translations

The Unissey SDK provides various default English texts as well as a French translation that you can
choose to leave as they are. However you're free to override any or all of them through
the `strings.xml` resource file if they're not fit for your needs.

To do this, you just need to add a line in your `strings.xml` for each text you want to override. An
exhaustive list of the texts you can redefine is available in
the [String resources](#36-string-resources) section.
All of our strings' keys are prefixed with "unissey" to prevent any conflict with the naming of your
own string resources.

**Example:** Overriding the Instructions screen's title

<!-- @formatter:off -->
```xml
<resources>
    <string name="app_name">My awesome app</string>
    <string name="unissey_instructions_title">My custom title inside Unissey\'s SDK</string>
</resources>
```
<!-- @formatter:on -->

On a side note, here's how you can handle the pluralization for the text with the
key `unissey_video_capture_title`:

<!-- @formatter:off -->
```xml
<plurals name="unissey_video_capture_title">
    <item quantity="zero">The acquisition will last %d seconds.</item>
    <item quantity="one">The acquisition will last %d second.</item>
    <item quantity="other">The acquisition will last %d seconds.</item>
</plurals>
```
<!-- @formatter:on -->

### 4.3 Customizing the colors

This SDK is using the Material 3 library for theming. It exposes a number of variables that can be
overridden by the client's application, which are all listed in the [Colors](#37-colors) section. To
override a color, you just need to redefine the corresponding value anywhere in your application
before you create the UnisseyScreen.

#### 4.3.1 Android Compose

As an example, redefining the primary and secondary colors could be done this way in Android
Compose:

```kotlin
import com.unissey.sdk.ui.theme.unissey_theme_color_light_primary
import com.unissey.sdk.ui.theme.unissey_theme_color_light_secondary

...

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizeUnisseyColors()

        setContent {
            val unisseyViewModel: UnisseyViewModel by viewModels {
                UnisseyViewModel.Factory.create(
                    SelfieFast
                ) { result ->
                    ...
                }
            }

            MyAwesomeAppTheme {
                UnisseyScreen(
                    unisseyViewModel = unisseyViewModel
                )
            }
        }
    }

    private fun customizeUnisseyColors() {
        unissey_theme_color_light_primary = Color(0xFF4CAF50)
        unissey_theme_color_light_secondary = Color(0xFF1B6B1E)
    }
}
```

#### 4.3.2 Traditional Android Views

To achieve the same result as the Android Compose example above using traditional Android Views, you
could write:

```kotlin
import androidx.compose.ui.graphics.Color
import com.unissey.sdk.ui.theme.unissey_theme_color_light_primary
import com.unissey.sdk.ui.theme.unissey_theme_color_light_secondary

...

class UnisseyFragment : Fragment() {

    ...

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        UnisseyViewModel.Factory.create(
            ...
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        customizeUnisseyColors()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ...

        return getUnisseyView(requireContext(), unisseyViewModel)
    }

    private fun customizeUnisseyColors() {
        unissey_theme_color_light_primary = Color(0xFF4CAF50)
        unissey_theme_color_light_secondary = Color(0xFF1B6B1E)
    }

}
```

⚠️ **NOTE:** Since the SDK is developed with Android Compose, the `Color` type you see in the
code `unissey_theme_color_light_primary = Color(0xFF4CAF50)` refers
to `androidx.compose.ui.graphics.Color` and not `android.graphics.Color`. You need to add the
Compose UI dependency to get access to this type. To do so, simply add this line in
your `build.gradle`'s `dependencies` section:

```groovy
implementation 'androidx.compose.ui:ui:1.4.3'
```

Don't forget to check the latest version of this
package [here](https://mvnrepository.com/artifact/androidx.compose.ui/ui) to benefit from the latest
updates.

### 4.4 Customizing the images

This SDK exposes some images in its `drawable` directory. They can be freely
overridden by just providing your own images using the same names as the ones defined in
the SDK and detailed in the [Images](#38-images) sections.

### 4.5 Customizing the types

This SDK is using the Material 3 library for theming. Just like for the colors, it exposes a number
of variables that can be overridden by the client's application, which are all listed in
the [Typography](#39-typography) section. To override a type, you just need to redefine the
corresponding value anywhere in your application before you create the UnisseyScreen. You can have a
look at the [official documentation](https://developer.android.com/jetpack/compose/text/fonts) to
see how to handle custom types in Compose.

**Example:** Let's say you want to change the font family to "Fira Sans". Provided that you imported
the .ttf files in your `res/font` Android Resource Directory, you write this function and call it
where you would have edited the colors (see the section above):

```kotlin
fun customizeUnisseyTypography() {
    val firaSansFamily = FontFamily(
        Font(R.font.firasans_light, FontWeight.Light),
        Font(R.font.firasans_regular, FontWeight.Normal),
        Font(R.font.firasans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.firasans_medium, FontWeight.Medium),
        Font(R.font.firasans_bold, FontWeight.Bold)
    )

    unissey_theme_type_compact_headlineMedium = TextStyle(
        fontFamily = firaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
    unissey_theme_type_medium_headlineMedium = TextStyle(
        fontFamily = firaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    )
    unissey_theme_type_expanded_headlineMedium = TextStyle(
        fontFamily = firaSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    )
}
```

⚠️ **NOTE:** Same way, same warning, just like for the colors, if you're using the traditional
Android Views, be aware that the types used in the example are Android Compose types. Which means
you will need to add a dependency to `androidx.compose.ui:ui` to your project to be able to edit the
types.

### 4.6 Auto-starting the video capture when the camera's ready

This SDK provides an [OnStateChangedListener](#33-onstatechangedlistener) that you can leverage to
achieve more advanced behavior. One good example of advanced usage would be to use this listener,
along with the open function `startVideoCapture(context: Context)`, to auto-start the video capture
when the camera is ready, so that the user doesn't have to click on the "Start" button.

First of all, you're going to hide the "Start" button which doesn't make any sense if we want the
video to be captured right away:

```kotlin
val sessionConfig = SessionConfig(uiConfig = UiConfig(showVideoCaptureButton = false))
```

Then, you need to implement an `OnStateChangedListener` and trigger the `startVideoCapture()`
function when the SDK's state becomes `CameraReady`.

#### 4.6.1 Android Compose

Here's a way you could do that in Android Compose:

```kotlin
class MainActivity : ComponentActivity() {

    private lateinit var unisseyViewModel: UnisseyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val sessionConfig = SessionConfig(uiConfig = UiConfig(showVideoCaptureButton = false))

            unisseyViewModel = ViewModelProvider(this,
                UnisseyViewModel.Factory.create(
                    SelfieFast,
                    sessionConfig,
                    { state ->
                        if (state is CameraReady) {
                            unisseyViewModel.startVideoCapture(this)
                        }
                    }
                ) { result ->
                    ...
                })[UnisseyViewModel::class.java]

            MyAwesomeAppTheme {
                UnisseyScreen(
                    unisseyViewModel = unisseyViewModel
                )
            }
        }
    }
}
```

The trick is that you need a reference to the `UnisseyViewModel` before instantiating it
with the Factory, since you need the reference to call `startVideoCapture()`.

#### 4.6.2 Traditional Android Views

In a very similar manner as the Android Compose code above, here's how you could achieve the same
result using traditional Android Views:

```kotlin
class UnisseyFragment : Fragment() {

    private val onRecordEndedListener: OnRecordEndedListener = { result ->
        ...
    }

    private val onStateChangedListener: OnStateChangedListener = { state ->
        if (state is CameraReady) {
            unisseyViewModel.startVideoCapture(requireContext())
        }
    }

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        UnisseyViewModel.Factory.create(
            SelfieFast,
            SessionConfig(uiConfig = UiConfig(showVideoCaptureButton = false)),
            onStateChangedListener,
            onRecordEndedListener
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Update the OnRecordEndedListener and OnStateChangedListener since the reference to
        // SharedViewModel has changed in case of a Fragment recreation (it happens upon configuration
        // changes such as a change in the screen orientation)
        unisseyViewModel.onRecordEndedListener = onRecordEndedListener
        unisseyViewModel.onStateChangedListener = onStateChangedListener

        return getUnisseyView(requireContext(), unisseyViewModel)
    }
}
```

⚠️ **NOTE:** For the same reason you need to update `OnRecordEndedListener` upon configuration
change, as mentioned in the [UnisseyScreen](#232-traditional-android-views) section, you need to
update your `OnStageChangedListener` to avoid any crash after the user has rotated the screen or
done any other action that could trigger a configuration change.

### 4.7 Adapting the content padding

The SDK lets you define [Modifiers](https://developer.android.com/jetpack/compose/modifiers) for its
three pages. They allow you to further customize some aspects of the screens. One good example of
that would be adding padding to one or all screens. A typical use case for this is when you have a
toolbar in your application that you want to be displayed on the SDK's screens as well. You don't
necessarily need to specify a padding when adding a toolbar, but if you want some of the pages to
display in full screen, such as the video capture screen, you may adapt your layout to do so and
then provide padding to the pages that shouldn't be in full screen.

#### 4.7.1 Android Compose

As illustrated in the Sample Compose App, you could use the Material
3 [Scaffold](https://developer.android.com/jetpack/compose/layouts/material#scaffold) Composable
that provides padding values that should be applied to the content root to offset the top and bottom
bars if they exist.

```kotlin
Scaffold(
    topBar = {
        TopAppBar(
            title = { ... },
            navigationIcon = {
                IconButton(onClick = { ... }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = ...
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    },
) { contentPadding ->
    UnisseyScreen(
        instructionsModifier = Modifier.padding(contentPadding),
        cameraPermissionModifier = Modifier.padding(contentPadding),
        unisseyViewModel = unisseyViewModel
    )
}
```

By doing this, you're effectively keeping the video capture screens in full screen while applying a
padding corresponding to the top bar's height to the instructions and camera permission screens, so
that the top bar isn't overlapping the screen content.

#### 4.7.2 Traditional Android Views

As illustrated in the Sample Legacy App, you could have a transparent `Toolbar` widget constrained
so that it technically overlaps the screen content. By doing so, the SDK's screens would appear in
full screen. However, this means that the toolbar would overlap the title on the instructions
screen. To avoid this, you could apply a top padding to the instructions and camera permission
screens equal to the toolbar's height. To do that though, you will have to write a little bit of
Compose code in your Views app:

```kotlin
class UnisseyFragment : Fragment() {

    ...

    private val unisseyViewModel: UnisseyViewModel by viewModels {
        ...
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ...

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                UnisseyScreen(
                    instructionsModifier = Modifier.padding(top = 56.dp),
                    cameraPermissionModifier = Modifier.padding(top = 56.dp),
                    unisseyViewModel = unisseyViewModel
                )
            }
        }
    }

}
```

In this example, the padding has simply been set to 56 dp for simplicity, which is the default
Android's toolbar height in portrait mode. You can of course compute the exact height of your top
bar. If you don't already use Android Compose in your application, you will have to add a few lines
to your `build.gradle` file for this to compile.

You need to add these dependencies:

```groovy
dependencies {
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.foundation:foundation'
}
```

You also need to add these lines in the `android` block:

```groovy
android {
    buildFeatures {
        compose true
    }
    composeOptions {
        kotlinCompilerExtensionVersion '1.4.7'
    }
}
```

### 4.8 Enabling Injection Attack Detection (IAD)

Unissey’s Injection Attack Detection (IAD) is a solution based on random, entirely passive
measurements that assure the genuineness and authenticity of the used camera and of the captured
video flow.

#### 4.8.1 Obtain the IAD data from your Back-End

The first step is retrieving the string data to pass on to the SDK. To do so, call the
endpoint `/iad/prepare` as described in the API documentation **from your application's Back-End** (
this is important as you need to specify your API key which should never be made public in your
client's source code).

#### 4.8.2 Create an IadConfig to add to the SessionConfig

As explained in the [Specifying a SessionConfig](#41-specifying-a-sessionconfig) section, you can
customize the SDK's behavior by specifying a configuration object. In order to enable Injection
Attack Detection and secure the acquired video, you need to create a `SessionConfig` object
containing an `IadConfig` with the data retrieved from the call to `/iad/prepare`:

```kotlin
val iadConfig = IadConfig(iadData)
val sessionConfig = SessionConfig(iadConfig = iadConfig)

val unisseyViewModel: UnisseyViewModel by viewModels {
    UnisseyViewModel.Factory.create(
        SelfieFast,
        sessionConfig,
    ) { result ->
        ...
    }
}
```

Note that when IAD is enabled, the video takes a bit longer to appear to the user (up to 3.5s) since
the SDK is performing some computations to ensure the camera and video are genuine and authentic.

#### 4.8.3 Send the metadata along with the video to the /analyze endpoint

Last but not least, when transmitting the video to your application's Back-End, do not forget to
send the metadata returned by the SDK as well. From your Back-End, you should then send the video
and the metadata to Unissey's `/analyze` endpoint. This step is mandatory in order for the IAD to
work since these encrypted metadata now contain information processed on our Back-End to assess the
video's authenticity.

The result of the call to `/analyze` will then contain information related to the IAD.

## 5. Common issues

### 5.1 Android Studio reporting string resources not translated in French

This SDK includes a French translation for convenience for our French clients. When importing a
library into your project, all the library's translations are also included in your binary
regardless of the fact that your own application supports those languages or not. If your
application doesn't support French, Android Studio will complain that you didn't provide a French
translation for your own string resources. To avoid this, you need to explicitly specify your app's
supported languages through your `build.gradle` file, as described in
the [official documentation](https://developer.android.com/guide/topics/resources/multilingual-support#specify-the-languages-your-app-supports).
By doing this, the French translation won't be included in your binary, and the errors will
disappear.

**Example:** If your application only supports English by default:

```groovy
android {
    defaultConfig {
        ...
        resConfigs "en"
    }
}
```
