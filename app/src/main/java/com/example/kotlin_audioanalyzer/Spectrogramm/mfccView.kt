package com.example.kotlin_audioanalyzer.Spectrogramm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.*
import kotlin.math.absoluteValue
import kotlin.math.floor

class mfccView: View {

    private var paintFillRect = Paint()
    private var paintStroke = Paint()
    private var paintFillCircle=Paint()
    private lateinit var mfccArray: ArrayList<FloatArray>
    private var realTimeData = ArrayList<FloatArray>()

    private val colorParula =
        intArrayOf(
            Color.parseColor("#f9fb0d"),
            Color.parseColor("#f5e422"),
            Color.parseColor("#fee435"),
            Color.parseColor("#ddbe24"),
            Color.parseColor("#c6c326"),
            Color.parseColor("#9eca41"),
            Color.parseColor("#73ce64"),
            Color.parseColor("#56cd7b"),
            Color.parseColor("#34c998"),
            Color.parseColor("#28c5a9"),
            Color.parseColor("#00b9cb"),
            Color.parseColor("#2e7dfc"),
            Color.parseColor("#4439df"),
            Color.parseColor("#3c21aa")
        )

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setWave(arrayList: ArrayList<FloatArray>) {
        mfccArray = arrayList
    }

    fun setRealTimeWave(bufferMfccRealTime: ArrayList<FloatArray>) {
        realTimeData.clear()
        realTimeData.addAll(bufferMfccRealTime)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val k = height / numFilters

        //
        val frameSize= floor(frameSizeEdit * samplingRate / fftResolution).toInt()

        var startIndex=0
        //

        paintFillRect.style = Paint.Style.FILL
        paintFillRect.color = Color.GREEN
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = Color.BLACK

        if (mfcc_realtime && realTimeData.size>frameSize) startIndex=realTimeData.size-frameSize

        mfccDrawEtalon(canvas,width,height,k,startIndex)

        if (mfcc_realtime){
            mfccDrawCurrent(canvas,width,height,k,startIndex)
        }

    }

    private fun mfccDrawCurrent(canvas: Canvas, width: Int, height: Int, k: Int, startIndex: Int) {
        var widthSize=mfccArray.size
        if (startIndex>0)  widthSize=realTimeData.size-startIndex

        for (i in startIndex until realTimeData.size) {
            val mas = realTimeData[i]
            val masEtalon=mfccArray[i]
            for (j in 0 until mas.size) {

                val aLeft: Int = (i-startIndex) * width / widthSize.toInt()
                val aTop: Int = height-(mas.size - (j)) * k
                val aRight: Int = (i-startIndex + 1) * width / widthSize.toInt()
                paintFillCircle.color = colorDefine(colorParula, mas[j])
                paintStroke.color=Color.BLACK
                val x=(aRight-aLeft).toFloat()
                var radius=0f
                radius = if (x>k) (k/2).toFloat()
                else x/2
                val xCircle=(x/2)+aLeft
                val yCircle=(aTop+(k/2)).toFloat()
                canvas.drawCircle(xCircle, yCircle, radius, paintFillCircle)
                canvas.drawCircle(xCircle, yCircle, radius, paintStroke)
            }
        }
    }

    private fun mfccDrawEtalon(canvas: Canvas, width: Int, height: Int, k: Int, startIndex: Int) {
        var widthSize=mfccArray.size
        if (startIndex>0)  widthSize=realTimeData.size-startIndex

        for (i in startIndex until mfccArray.size) {
            val mas = mfccArray[i]
            for (j in 0 until mas.size) {
                val myRect = Rect()
                val aLeft: Int = (i-startIndex) * width / widthSize.toInt()
                val aTop: Int = height - (mas.size - (j)) * k
                val aRight: Int = (i-startIndex + 1) * width / widthSize.toInt()
                val aBottom: Int = height - (mas.size - (j + 1))
                myRect.set(aLeft, aTop, aRight, aBottom)
                paintFillRect.color = colorDefine(colorParula, mas[j])
                canvas.drawRect(myRect, paintFillRect)
                canvas.drawRect(myRect, paintStroke)
            }
        }
    }

    private fun colorDefine(color: IntArray, Amplitude: Float): Int {
        val gradation = color.size

        val highLimit = 15
        val lowLimit = -15

        val numGradation = (highLimit - lowLimit) / gradation
        val valueGradation = IntArray(gradation)

        for (i in 0 until gradation) {
            valueGradation[i] = (highLimit - i * numGradation)
        }
        val aColor = amplitudeToColor(valueGradation, Amplitude)
        return color[aColor]
    }

    fun amplitudeToColor(valueGradation: IntArray, amplitude: Float): Int {

        var result = 0
        var tempDifference = (amplitude - valueGradation[0]).absoluteValue

        for (i in 1 until valueGradation.size) {
            val tempResult = (amplitude - valueGradation[i]).absoluteValue
            if (tempResult < tempDifference) {
                tempDifference = tempResult
                result = i
            }
        }
        return result
    }
}

