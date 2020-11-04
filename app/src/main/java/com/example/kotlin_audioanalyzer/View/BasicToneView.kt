package com.example.kotlin_audioanalyzer.View

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.*
import kotlin.math.floor

class BasicToneView : View {

    private var paint = Paint()
    private var realTimeData = ArrayList<Float>()
    private lateinit var frequencyArray: FloatArray

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }


    fun setWaveFrequency(wList: FloatArray) {
        frequencyArray = FloatArray(wList.size)
        val maxWave = fftResolution / 2
        val a = samplingRate / (2 * fftResolution)
        for (j in wList.indices) {
            frequencyArray[j] = (wList[j] / maxWave)
        }
    }

    private fun getValueFromRelativePosition(
        position: Float,
        minValue: Float,
        maxValue: Float,
    ): Float {
        return minValue + position * (maxValue - minValue)
    }

    fun setRealTimeWave(input: ArrayList<Float>) {
        realTimeData.clear()
        realTimeData.addAll(input)
        val maxWave = fftResolution / 2
        for (j in 0 until input.size) {
            realTimeData[j] = (realTimeData[j] / maxWave)
        }
    }


    private fun searchMin(array: FloatArray): Float {
        var min = array[4]
        for (i in 5 until array.size) {
            if (array[i] < min)
                min = array[i]
        }
        return min
    }

    private fun drawEtalon(canvas: Canvas, width: Int, height: Int, a: Float = 1f) {
        paint.color = Color.WHITE
        var x1: Float = 0f
        var y1: Float = (height * frequencyArray[4]) * a

        for (i in 5 until frequencyArray.size) {
            val y2 = (height * frequencyArray[i]) * a
            val x2 = width * i / frequencyArray.size.toFloat()
            canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }
    }

    private fun drawRealTime(canvas: Canvas, width: Int, height: Int, a: Float = 1f) {
        var rtx1: Float = 0f
        var rty1: Float = (height * realTimeData[4]) * a
        paint.color = Color.YELLOW
        for (p in 5 until realTimeData.size) {
            val rty2 = (height * realTimeData[p]) * a
            val rtx2 = width * p / frequencyArray.size.toFloat()
            canvas.drawLine(rtx1, height - rty1, rtx2, height - rty2, paint)
            rtx1 = rtx2
            rty1 = rty2
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height

        val wColor = 10
        val wFrequency = 40
        val rWidth = width - wColor - wFrequency

        val frameSize = floor(frameSizeEdit * samplingRate / fftResolution).toInt()

        paint.strokeWidth = 1f
        paint.color = Color.WHITE
        //Frequency

        val max = searchMax(frequencyArray)
        val min = searchMin(frequencyArray)
        val currentHz = max * samplingRate / 2
        val minHz = min * samplingRate / 2
        var h = 0f
        val t = 1 / max
        var hDraw = 1 / max
        // if (max > 0.9) hDraw = 1 / max

        if (realtimebolean && realTimeData.size > 4) {
            val maxRealTime = searchMax(realTimeData)
            if (maxRealTime < max) hDraw = 1 / max
            else hDraw = 1 / maxRealTime

            if (realTimeData.size - 4 > frameSize) {
                val startIndex = realTimeData.size - frameSize
                val hrt = 1 / searchMaxDefIndex(realTimeData, startIndex)
                val hfa = 1 / searchMaxDefIndex(frequencyArray, startIndex)
                h = if (hrt < hfa) hrt
                else hfa
                darwRealTimeMovement(canvas, width, height, h, startIndex)
                darwEtalonMovement(canvas, width, height, h, startIndex)
            } else drawRealTime(canvas, width, height, hDraw)
        }

        if (realTimeData.size - 4 < frameSize) drawEtalon(canvas, width, height, hDraw)

        canvas.drawText("" + currentHz,
            rWidth + wColor.toFloat(),
            height - max * hDraw * height + 20,
            paint)
        canvas.drawText("" + minHz, rWidth + wColor.toFloat(), height - min * hDraw * height, paint)

//        canvas.drawText("kHz", rWidth + wColor.toFloat(), height-10.toFloat(),paint)
//        var i=0
//        while (i < (samplingRate - 500) / 8) {
//            canvas.drawText(" " + i / 1000,
//                rWidth + wColor.toFloat(),
//                height * (1f - i.toFloat() / (samplingRate / 8)),
//                paint)
//            i += 1000
//        }
    }

    private fun darwRealTimeMovement(
        canvas: Canvas,
        width: Int,
        height: Int,
        h: Float,
        startIndex: Int,
    ) {
        var rtx1: Float = 0f
        var rty1: Float = (height * realTimeData[startIndex]) * h
        paint.color = Color.YELLOW
        val widthSize = realTimeData.size - startIndex


        for (p in startIndex until realTimeData.size) {
            val rty2 = (height * realTimeData[p]) * h
            val rtx2 = width * (p - startIndex) / widthSize.toFloat()
            canvas.drawLine(rtx1, height - rty1, rtx2, height - rty2, paint)
            rtx1 = rtx2
            rty1 = rty2
        }
    }

    private fun darwEtalonMovement(
        canvas: Canvas,
        width: Int,
        height: Int,
        h: Float,
        startIndex: Int,
    ) {
        paint.color = Color.WHITE
        var x1: Float = 0f
        val widthSize = realTimeData.size - startIndex

        var y1: Float = (height * frequencyArray[4]) * h


        for (i in startIndex until realTimeData.size) {
            val y2 = (height * frequencyArray[i]) * h
            val x2 = width * (i - startIndex) / widthSize.toFloat()
            canvas.drawLine(x1, height - y1, x2, height - y2, paint)
            x1 = x2
            y1 = y2
        }
    }

}