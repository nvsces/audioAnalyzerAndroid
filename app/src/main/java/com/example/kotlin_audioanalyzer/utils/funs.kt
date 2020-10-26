package com.example.kotlin_audioanalyzer.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.MainActivity
import com.example.kotlin_audioanalyzer.R
import kotlin.collections.ArrayList

lateinit var APP_ACTIVITY: MainActivity
var samplingRate=44100
var fftResolution=2048
var mfcc_realtime=false
var streamMFCC=false
var frameSizeEdit=1f

var numFilters=13

var checkqq=false
var currentRun=false
var etalonRun=false

var streamRealTime=true

var realtimebolean=false

fun deleteFrameIsMFCC(mfcc: Array<FloatArray>): FloatArray {
    return mfcc[0]
}

fun deleteFrameListIsMFCC(listMFCC:ArrayList<Array<FloatArray>>):ArrayList<FloatArray>{
    val outArray=ArrayList<FloatArray>()
    for ( i in 0 until listMFCC.size){
        val array:FloatArray=listMFCC[i][0]
        outArray.add(array)
    }
    return outArray
}

fun short2FloatArray(shortArray: ShortArray):FloatArray{
    val floatArray=FloatArray(shortArray.size)
    for (i in shortArray.indices){
        floatArray[i]=shortArray[i].toFloat()
    }
    return floatArray
}

fun searchMaxDefIndex(array: ArrayList<Float>,Index:Int): Float {
    var max = 0f
    for (i in Index until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}

fun searchMaxDefIndex(array: FloatArray,Index:Int): Float {
    var max = 0f
    for (i in Index until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}

fun searchMaxAbs(array: FloatArray): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i]<0) array[i]=-array[i]
        if (array[i] > max)
            max = array[i]
    }
    return max
}


fun searchMax(array: FloatArray): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}
fun searchMax(array: ArrayList<Float>): Float {
    var max = 0f
    for (i in 4 until array.size) {
        if (array[i] > max)
            max = array[i]
    }
    return max
}
fun searchMax(array: ShortArray): Float {
    var max = 0f
    for (i in 0 until array.size) {
        if (array[i] > max)
            max = array[i].toFloat()
    }
    return max
}


fun replaceFragment(fragment: Fragment, addStack:Boolean=true){
    if (addStack) {
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }else{
        APP_ACTIVITY.supportFragmentManager.beginTransaction()
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }
}

fun showToast(message: String){
    Toast.makeText(APP_ACTIVITY,message, Toast.LENGTH_SHORT).show()
}