package sample.unissey.app

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.unissey.sdk.AnalyzeAPIResponse
import com.unissey.sdk.CameraState
import com.unissey.sdk.UnisseyFragment

import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.Error

class MainActivity : AppCompatActivity() {
    companion object {
        const val SDK_FRAGMENT_TAG = "DSCameraFragmentHolder"
        const val IS_API_ENABLED = false
    }

    private lateinit var sdkFragment: UnisseyFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sdkFragment = supportFragmentManager.findFragmentByTag(SDK_FRAGMENT_TAG) as UnisseyFragment?
            ?: throw Error("Fragment with tag '$SDK_FRAGMENT_TAG' should exists")

        val takeReferencePictureContract = prepareTakeReferencePictureContract()

        findViewById<ImageButton>(R.id.reference_picture_button).setOnClickListener {
            takeReferencePictureContract.launch(null)
        }

        val out = ByteArrayOutputStream()

        sdkFragment.apply {
            doesUseAPI = IS_API_ENABLED

            setOutputStream(out)

            onCameraStateChanged = {
                when(it){
                    CameraState.LOADING -> Log.d("CAMERA_LIFECYCLE", "Camera is loading")
                    CameraState.READY -> Log.d("CAMERA_LIFECYCLE", "Camera is ready, the user can start the capture")
                    CameraState.RELEASED -> Log.d("CAMERA_LIFECYCLE", "Camera resources are released")
                }
            }

            setOnRecordEndedListener {
                out.close()

                val mediaFile = File(activity?.filesDir, "uni_sample_plackback.mp4")
                mediaFile.writeBytes(out.toByteArray())
                val mediaUri = Uri.fromFile(mediaFile)

                val intent = Intent(activity, VideoPlay::class.java)
                intent.putExtra("video_path", mediaUri.toString())

                activity?.startActivity(intent)
            }
        }
    }

    private fun selectAnalysisResultMessageID(result: AnalyzeAPIResponse, err: Throwable?): Int {
        if (err != null) {
            Log.e("MainActivity", "Error during analysis: $err", err)
            return R.string.activity_main_analysis_error
        }

        // If null and no error, no facematch / liveness has been ordered so it's ok.
        val isFaceMatchOK = result?.isMatch ?: true
        val isLivenessOK = result?.isGenuine ?: true

        if (!isFaceMatchOK) return R.string.activity_main_analysis_bad_facematching
        if (!isLivenessOK) return  R.string.activity_main_analysis_bad_liveness
        return R.string.activity_main_analysis_successful
    }

    private fun prepareTakeReferencePictureContract(): ActivityResultLauncher<Void?> {
        return registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            kotlin.runCatching {
                val pictureBytes = it?.let { it1 -> compressBitmapToJPEGBytes(it1) }
                if (pictureBytes != null) {
                    sdkFragment.setReferencePicture(pictureBytes)
                }
                runOnUiThread { findViewById<ImageButton>(R.id.reference_picture_button).setImageBitmap(it) }
            }.onFailure { err ->
                Log.e("SelectImageSource", "Error: $err", err)
            }
        }
    }

    private fun compressBitmapToJPEGBytes(bitmap: Bitmap): ByteArray {
        val bOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bOutputStream)
        return bOutputStream.toByteArray()
    }

    private fun resetFaceMatch() {
        findViewById<ImageButton>(R.id.reference_picture_button).setImageResource(R.drawable.take_reference_picture_icon)
        sdkFragment.clearReferencePicture()
    }
}