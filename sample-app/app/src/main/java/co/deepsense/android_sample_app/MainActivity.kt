package co.deepsense.android_sample_app

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.unissey.sdk.AnalyseResults
import com.unissey.sdk.DsCameraFragment
import com.unissey.sdk.api.DeepsenseEnvironment
import java.io.ByteArrayOutputStream
import java.lang.Error

class MainActivity : AppCompatActivity() {
    companion object {
        const val SDK_FRAGMENT_TAG = "DSCameraFragmentHolder"
        const val API_KEY = "<your api key>"
        const val IS_API_ENABLED = true
        const val IS_GDPR_CONSENT_GIVEN = true
        val DS_ENV = DeepsenseEnvironment.TEST
    }

    private lateinit var sdkFragment: DsCameraFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sdkFragment = supportFragmentManager.findFragmentByTag(SDK_FRAGMENT_TAG) as DsCameraFragment?
            ?: throw Error("Fragment with tag '$SDK_FRAGMENT_TAG' should exists")

        val takeReferencePictureContract = prepareTakeReferencePictureContract()

        findViewById<ImageButton>(R.id.reference_picture_button).setOnClickListener {
            takeReferencePictureContract.launch(null)
        }

        sdkFragment.apply {
            apiKey = API_KEY
            environment = DS_ENV
            doesUseAPI = IS_API_ENABLED
            gdprConsent = IS_GDPR_CONSENT_GIVEN

            setOnAnalysisResultListener { results, err ->
                val resultMessageID = selectAnalysisResultMessageID(results, err)
                resetFaceMatch()
                Snackbar.make(requireView(), resultMessageID, Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun selectAnalysisResultMessageID(result: AnalyseResults, err: Throwable?): Int {
        if (err != null) {
            Log.e("MainActivity", "Error during analysis: $err", err)
            return R.string.activity_main_analysis_error
        }

        // If null and no error, no facematch / liveness has been ordered so it's ok.
        val isFaceMatchOK = result.faceMatching?.isMatch ?: true
        val isLivenessOK = result.liveness?.isGenuine ?: true

        if (!isFaceMatchOK) return R.string.activity_main_analysis_bad_facematching
        if (!isLivenessOK) return  R.string.activity_main_analysis_bad_liveness
        return R.string.activity_main_analysis_successful
    }

    private fun prepareTakeReferencePictureContract(): ActivityResultLauncher<Void> {
        return registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            kotlin.runCatching {
                val pictureBytes = compressBitmapToJPEGBytes(it)
                sdkFragment.setReferencePicture(pictureBytes)
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