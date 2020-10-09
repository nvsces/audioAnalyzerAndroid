package com.example.kotlin_audioanalyzer.Spectrogramm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View


class TimeView : View {
    // Attributes
    private var paint = Paint()
    private var detector: GestureDetector
    private var gain = 1.0f
    private var fftResolution = 0
    private lateinit var wave: FloatArray

    // Window
    constructor(context: Context?) : super(context) {
        detector = GestureDetector(getContext(), GestureListener())
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        detector = GestureDetector(getContext(), GestureListener())
    }

    /**
     * Touch event handling
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        detector.onTouchEvent(event)
        invalidate()
        return true
    }

    private inner class GestureListener : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            gain *= 1.0f + distanceY * 0.01f
            return true
        }
    }

    /**
     * Simple sets
     */
    fun setFFTResolution(res: Int) {
        fftResolution = res
        wave = FloatArray(res)
    }

    fun setWave(w: FloatArray) {
        System.arraycopy(w, 0, wave, 0, w.size)
    }

    /**
     * Called whenever a redraw is needed
     * Renders wave form as a series of lines
     */
    public override fun onDraw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val a = Misc.getAttribute("activity") as Activity
        val nightMode: Boolean = Misc.getPreference(a, "night_mode", true)

        // Draw axis
        paint.strokeWidth = 1f
        if (!nightMode) paint.color = Color.LTGRAY else paint.color = Color.DKGRAY
        canvas.drawLine(0f, height / 2.toFloat(), width.toFloat(), height / 2.toFloat(), paint)

        // Draw wave
        var k=Integer.valueOf(Misc.getPreference(a, "line_width", "1")).toFloat()
        paint.strokeWidth = k
        if (!nightMode) paint.color = Color.BLACK else paint.color = Color.WHITE
        var x1 = 0f
        var y1 = height * (0.5f + 0.5f * gain * wave[0])
        for (i in 1 until fftResolution) {
            val x2 = width * i / fftResolution.toFloat()
            val y2 = height * (0.5f + 0.5f * gain * wave[i])
            if (x1 > 0 && x1 < width && x2 > 0 && x2 < width)
                canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }
    }
}