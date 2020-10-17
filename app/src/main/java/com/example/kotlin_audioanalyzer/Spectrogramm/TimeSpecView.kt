package com.example.kotlin_audioanalyzer.Spectrogramm

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.kotlin_audioanalyzer.Spectrogramm.Misc.activity
import com.example.kotlin_audioanalyzer.utils.etalonRun
import com.example.kotlin_audioanalyzer.utils.realtimebolean
import kotlin.math.abs

class TimeSpecView: View {

    private lateinit var fWave:FloatArray
    private var paint = Paint()
    private lateinit var wave: ShortArray

    private var realTimeData=ArrayList<Float>()

    private lateinit var frequencyArray: FloatArray
    constructor(context: Context) : super(context) {
        activity = Misc.getAttribute("activity") as Activity
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        activity = Misc.getAttribute("activity") as Activity?
    }



    fun setWaveFrequency(wList: FloatArray){
        frequencyArray= FloatArray(wList.size)
        val maxWave: Float= 512f
        val a=44100/(2*512)
        for (j in 0 until wList.size){
            frequencyArray[j]= (wList[j]/ maxWave)
        }
    }

    private fun getValueFromRelativePosition(
        position: Float,
        minValue: Float,
        maxValue: Float
    ): Float {
        return minValue + position * (maxValue - minValue)
    }

    fun setRealTimeWave(input:ArrayList<Float>){
        realTimeData.clear()
        realTimeData.addAll(input)
        val maxWave: Float= 512f
        for (j in 0 until input.size){
            realTimeData[j]= (realTimeData[j]/ maxWave)
        }
    }

    fun setWave(wList: ArrayList<ShortArray>) {
        val sizeWave=wList.size*1024
        wave=wList[4]
        for (i in 4 until 5) {
            wave = wave + wList[i]
        }

        fWave= FloatArray(wave.size)

        var absfWave=FloatArray(fWave.size)

        for( i in 0 until fWave.size){
            if (wave[i]<0) absfWave[i]=-wave[i].toFloat()
            else absfWave[i]=wave[i].toFloat()
        }



        val maxWave: Float= absfWave.maxByOrNull { it }!!
        for (j in 0 until wave.size){
            fWave[j]= (wave[j]/ maxWave!!).toFloat()
        }
    }


    fun searchMax(array: FloatArray):Float{
        var max=0f
        for (i in 4 until array.size){
            if (array[i]>max)
                max=array[i]
        }
        return max
    }

    fun searchMin(array: FloatArray):Float{
        var min=array[4]
        for (i in 5 until array.size){
            if (array[i]<min)
                min=array[i]
        }
        return min
    }

    fun drawEtalon(canvas: Canvas,width:Int,height:Int,a:Float =1f){
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

    fun drawRealTime(canvas: Canvas,width:Int,height:Int,a:Float =1f){
        var rtx1: Float = 0f
        var rty1: Float = (height * realTimeData[4]) * a

        paint.color=Color.YELLOW
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
        var samplingRate = 44100
        val width = canvas.width
        val height = canvas.height

        val wColor = 10
        val wFrequency = 40
        val rWidth = width - wColor - wFrequency

        paint.strokeWidth = 1f
        paint.color = Color.WHITE
        //Frequency

        val max=searchMax(frequencyArray)
        val min=searchMin(frequencyArray)
        val currentHz=max*samplingRate/2
        val minHz=min*samplingRate/2
        Log.d("TAG",max.toString())
        Log.d("TAG", minHz.toString())
        Log.d("TAG",currentHz.toString())

        val a = 1000/samplingRate
        val t=1/max
        var hDraw=1/max-1
        if (max>0.9) hDraw=1/max


        drawEtalon(canvas,width, height,hDraw)

        if (realtimebolean && realTimeData.size>4){
        drawRealTime(canvas, width, height,hDraw)
    }

        canvas.drawText(""+currentHz,rWidth + wColor.toFloat(),height-max*hDraw*height+20,paint)
        canvas.drawText(""+minHz,rWidth + wColor.toFloat(),height-min*hDraw*height,paint)

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

}