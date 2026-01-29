package com.unissey.samplejavaapp.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.color.MaterialColors;

public class OvalOverlayView extends View {

    private RectF faceArea = new RectF();

    private final int progressColor;

    private float recordingProgress = 0;

    private final Paint overlayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final Path facePath = new Path();

    public OvalOverlayView(Context context) {
        this(context, null);
    }

    public OvalOverlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        progressColor = MaterialColors.getColor(
                this,
                android.R.attr.colorPrimary
        );

        overlayPaint.setStyle(Paint.Style.FILL);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(12);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(12);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setFaceArea(RectF value) {
        faceArea = value;
        invalidate();
    }

    public void setRecordingProgress(float recordingProgress) {
        this.recordingProgress = recordingProgress;
        invalidate();
    }

    /* -------------------- Drawing -------------------- */

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // Create oval path
        facePath.reset();
        facePath.addOval(faceArea, Path.Direction.CW);

        int save = canvas.save();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(facePath);
        } else {
            canvas.clipPath(facePath, Region.Op.DIFFERENCE);
        }

        int overlayColor = 0x88000000;
        overlayPaint.setColor(overlayColor);
        canvas.drawRect(
                0,
                0,
                getWidth(),
                getHeight(),
                overlayPaint
        );
        canvas.restoreToCount(save);

        // Draw oval border
        canvas.drawOval(faceArea, borderPaint);

        // Draw progress arc
        progressPaint.setColor(progressColor);
        canvas.drawArc(
                faceArea,
                -90,
                recordingProgress * 360,
                false,
                progressPaint
        );
    }
}
