package com.example.kotlin_audioanalyzer.Spectrogramm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.kotlin_audioanalyzer.utils.numFilters

class mfccView: View {


    private var paint=Paint()
    private lateinit var mfccArray:ArrayList<FloatArray>
//    private val colorRainbow =
//        intArrayOf(-0x1, -0xff01, -0x10000, -0x100, -0xff0100, -0xff0001, -0xffff01, -0x1000000)
    private val colorRainbow =
        intArrayOf(Color.YELLOW, Color.MAGENTA, Color.DKGRAY, Color.CYAN, Color.BLUE,Color.BLACK, Color.RED, Color.GREEN)


    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    fun setWave(arrayList: ArrayList<FloatArray>)
    {
        mfccArray=arrayList
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height

        var k=height/ numFilters
        var scaleWidth=width/mfccArray.size


        paint.color=Color.GREEN



        for ( i in 0 until mfccArray.size){
            val mas=mfccArray[i]
            for (j in 0 until mas.size){
                val myRect=Rect()
                var aLeft:Int=(i)*width/mfccArray.size.toInt()
                var aTop:Int=height-(mas.size-(j))*k
                val aRight:Int=(i+1)*width/mfccArray.size.toInt()
                val aBottom:Int=height-(mas.size-(j+1))
                myRect.set(aLeft,aTop,aRight,aBottom)
                paint.color=defineColor(colorRainbow,mas[j])
                canvas.drawRect(myRect,paint)
            }
        }


      /*  for (i in 0 until mfccArray.size){
            val mas=mfccArray[i]
                while (scaleWidth>0) {
                    var wTemp=width-(mfccArray.size-i)*scaleWidth.toFloat()
                    scaleWidth--
                    for (j in 0 until mas.size){
                        while (k > 0) {
                            val hTemp = height - (mas.size - (j)) * k.toFloat()
                            canvas.drawPoint(wTemp, hTemp, paint)
                            k--
                    }
                    k = height / numFilters
                }
            }
            scaleWidth=width/mfccArray.size
        }*/
    }

    private fun defineColor(colorRainbow: IntArray,Amplitude:Float): Int {
        if (Amplitude>0){
            if(Amplitude>0.5){
                if (Amplitude>1){
                    if (Amplitude>2)    return colorRainbow[0]
                    else return colorRainbow[1]
                }else return colorRainbow[2]
            }else return colorRainbow[3]
        } else{
            if(Amplitude<-0.5){
                if (Amplitude<-1){
                    if (Amplitude<-2)    return colorRainbow[4]
                    else return colorRainbow[5]
                }else return colorRainbow[6]
            }else return colorRainbow[7]
        }
    }
}