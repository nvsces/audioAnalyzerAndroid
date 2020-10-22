package com.example.kotlin_audioanalyzer.utils

import android.app.Activity
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.MainActivity
import com.example.kotlin_audioanalyzer.R
import java.util.*

lateinit var APP_ACTIVITY: MainActivity
var samplingRate=44100
var fftResolution=1024

var frameSizeEdit=1f

var checkqq=false
var countVoice:Long=0
var etalonRun=false

var stream=true

var realtimebolean=false

var map = HashMap<String, Any>()
fun getAttribute(s: String): Any? {
    return map[s]
}

fun setAttribute(s: String, o: Any) {
    map[s] = o
}

fun resetAttributes() {
    map = HashMap()
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