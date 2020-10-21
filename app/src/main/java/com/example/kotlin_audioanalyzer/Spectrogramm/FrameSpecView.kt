package com.example.kotlin_audioanalyzer.Spectrogramm

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.searchMax

class FrameSpecView: View {

    private lateinit var fftData:FloatArray

    private var paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setWave(array:FloatArray){
        fftData=array
        val maxWave= searchMax(fftData)
        for (j in 0 until fftData.size) {
            fftData[j] = (fftData[j] / maxWave)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height

        paint.color= Color.BLACK

        val a=8
        val scaleData=fftData.size/a

        var x1: Float = 0f
        var y1: Float = (height * fftData[0])

        for (i in 1 until scaleData) {
            val y2 = (height * fftData[i])
            val x2 = width * i / scaleData.toFloat()
            canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }

    }


}