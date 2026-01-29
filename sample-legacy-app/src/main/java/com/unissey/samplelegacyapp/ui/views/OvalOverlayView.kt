package com.unissey.samplelegacyapp.ui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withSave
import com.google.android.material.color.MaterialColors

class OvalOverlayView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    var faceArea: RectF = RectF()
        set(value) {
            field = value
            invalidate()
        }

    var overlayColor: Int = 0x88000000.toInt()
    var progressColor: Int = MaterialColors.getColor(
        this,
        android.R.attr.colorPrimary
    )
    var recordingProgress: Float = 0f

    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 12f
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 12f
        strokeCap = Paint.Cap.ROUND
    }

    private val facePath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw a dimmed overlay with an oval hole
        facePath.reset()
        facePath.addOval(faceArea, Path.Direction.CW)

        canvas.withSave {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                clipOutPath(facePath)
            } else {
                @Suppress("DEPRECATION")
                clipPath(facePath, Region.Op.DIFFERENCE)
            }

            overlayPaint.color = overlayColor
            drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayPaint)
        }

        // Draw the oval border
        canvas.drawOval(faceArea, borderPaint)

        // Draw the progress arc
        progressPaint.color = progressColor
        canvas.drawArc(
            faceArea,
            -90f,
            recordingProgress * 360f,
            false,
            progressPaint
        )
    }
}