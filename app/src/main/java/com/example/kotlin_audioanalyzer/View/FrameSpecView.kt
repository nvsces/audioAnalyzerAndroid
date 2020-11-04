package com.example.kotlin_audioanalyzer.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.samplingRate
import com.example.kotlin_audioanalyzer.utils.searchMax
import com.example.kotlin_audioanalyzer.utils.searchMaxAbs

class FrameSpecView : View {

    private lateinit var fftData: FloatArray
    private var paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setWave(array: FloatArray) {
        fftData = array
        val maxWave = searchMax(fftData)
        for (j in 0 until fftData.size) {
            fftData[j] = (fftData[j] / maxWave)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height

        val fpixel = 1000 * width / (samplingRate / 8)
        paint.color = Color.BLACK

        val tempDataFFT = FloatArray(fftData.size / 4)
        System.arraycopy(fftData, 0, tempDataFFT, 0, fftData.size / 4)
        val a = 1
        val scaleData = tempDataFFT.size / a

        var x1: Float = 0f
        var y1: Float = ((height - 40) * tempDataFFT[0])

        for (i in 1 until scaleData) {
            val y2 = ((height - 40) * tempDataFFT[i])
            val x2 = width * i / scaleData.toFloat()
            canvas.drawLine(x1, height - 40 - y1, x2, height - 40 - y2, paint)
            x1 = x2
            y1 = y2
        }
        val shadowPaint = Paint()
        shadowPaint.setTextSize(35.0f)
        shadowPaint.color = Color.BLACK
        for (i in 0 until 10) {
            canvas.drawText("${i * 0.5}",
                (i * 0.5 * fpixel - 20).toFloat(),
                height.toFloat(),
                shadowPaint)
        }
        canvas.drawText("kHz", (width - 80).toFloat(), height.toFloat(), shadowPaint)
    }


}