package sample.unissey.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import sample.unissey.app.R

class VideoPlay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)

        val videoPath = intent?.extras?.getString("video_path").toString()


        val player = findViewById<VideoView>(R.id.videoPlayer)
        player.setVideoPath(videoPath)

        player.start()

        val replayButton = findViewById<Button>(R.id.replay_button)
        replayButton.setOnClickListener {
            player.start()
        }

        val retryButton = findViewById<Button>(R.id.retry_button)
        retryButton.setOnClickListener {
            val intent = Intent(it.context, MainActivity::class.java)
            it.context.startActivity(intent)
        }
    }
}