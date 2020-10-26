package com.example.kotlin_audioanalyzer.Spectrogramm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.mfcc_realtime
import com.example.kotlin_audioanalyzer.utils.numFilters
import kotlin.math.absoluteValue

class mfccView: View {

    private var paintFill = Paint()
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

        paintFill.style = Paint.Style.FILL
        paintFill.color = Color.GREEN
        paintStroke.style = Paint.Style.STROKE
        paintStroke.color = Color.BLACK

        mfccDrawEtalon(canvas,width,height,k)

        if (mfcc_realtime){
            mfccDrawCurrent(canvas,width,height,k)
        }

    }

    private fun mfccDrawCurrent(canvas: Canvas, width: Int, height: Int, k: Int) {
        val alpha=0.7
        for (i in 0 until realTimeData.size) {
            val mas = realTimeData[i]
            val masEtalon=mfccArray[i]
            for (j in 0 until mas.size) {

                val aLeft: Int = (i) * width / mfccArray.size.toInt()
                val aTop: Int = height-(mas.size - (j)) * k
                val aRight: Int = (i + 1) * width / mfccArray.size.toInt()
                paintFillCircle.color = colorDefine(colorParula, mas[j])
                paintStroke.color=Color.BLACK
                val x=(aRight-aLeft).toFloat()
                var radius=0f
                radius=x/2
//                radius = if (x>y) y
//                else x
                val xCircle=(x/2)+aLeft
                val yCircle=(aTop+(k/2)).toFloat()
                canvas.drawCircle(xCircle, yCircle, radius, paintFillCircle)
                canvas.drawCircle(xCircle, yCircle, radius, paintStroke)
            }
        }
    }

    private fun mfccDrawEtalon(canvas: Canvas, width: Int, height: Int, k: Int) {
        for (i in 0 until mfccArray.size) {
            val mas = mfccArray[i]
            for (j in 0 until mas.size) {
                val myRect = Rect()
                val aLeft: Int = (i) * width / mfccArray.size.toInt()
                val aTop: Int = height - (mas.size - (j)) * k
                val aRight: Int = (i + 1) * width / mfccArray.size.toInt()
                val aBottom: Int = height - (mas.size - (j + 1))
                myRect.set(aLeft, aTop, aRight, aBottom)
                paintFill.color = colorDefine(colorParula, mas[j])
                canvas.drawRect(myRect, paintFill)
                canvas.drawRect(myRect, paintStroke)
            }
        }
    }


    private fun colorDefine(color: IntArray, Amplitude: Float): Int {
        val gradation = color.size

        var highLimit = 15
        var lowLimit = -15

        var numGradation = (highLimit - lowLimit) / gradation
        var valueGradation = IntArray(gradation)

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

