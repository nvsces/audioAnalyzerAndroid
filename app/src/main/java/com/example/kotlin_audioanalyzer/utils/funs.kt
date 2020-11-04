package com.example.kotlin_audioanalyzer.utils

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.MainActivity
import com.example.kotlin_audioanalyzer.R
import kotlin.collections.ArrayList




fun overlapWindowDataBuffer(listBuffer: ArrayList<FloatArray>): ArrayList<FloatArray> {
    val bufferSize=listBuffer[0].size
    val outList=ArrayList<FloatArray>()
    outList.add(listBuffer[0])

    for (i in 1 until listBuffer.size-1){
        val tempArray=FloatArray(bufferSize)
        System.arraycopy(listBuffer[i],0,tempArray,0,bufferSize/2)
        System.arraycopy(listBuffer[i+1],0,tempArray,bufferSize/2,bufferSize/2)
        outList.add(tempArray)
    }
    outList.add(listBuffer[listBuffer.size-1])
    return outList
}

fun magnitudeSpec(re: FloatArray, im: FloatArray): FloatArray {
    val magnitude=FloatArray(re.size)
    for(i in 0 until re.size){
        magnitude[i]=re[i]*re[i]+im[i]*im[i]
    }
    return magnitude
}

fun shortArrayListToFloatArrayList(shortList: ArrayList<ShortArray>): ArrayList<FloatArray> {
    val floatList=ArrayList<FloatArray>()
    var floatArray=FloatArray(shortList[0].size)

    for (i in 0 until shortList.size){
        floatArray= short2FloatArray(shortList[i])
        floatList.add(floatArray)
    }
    return floatList
}

fun splitDataWavChannelsToFloat(sampleBuffer:DoubleArray):ArrayList<FloatArray>{
    val splitList=ArrayList<FloatArray>()
    val arrayOneChannels=FloatArray(sampleBuffer.size/2)
    val arrayTwoChannels=FloatArray(sampleBuffer.size/2)
    var onek=0
    var twok=0

    for (i in 0 until sampleBuffer.size){
        if (i%2!=0) {
            arrayOneChannels[onek]=sampleBuffer[i].toFloat()
            onek++
        }else{
            arrayTwoChannels[twok]=sampleBuffer[i].toFloat()
            twok++
        }
    }
    splitList.add(arrayTwoChannels)
    splitList.add(arrayOneChannels)


    return splitList
}

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
fun short2DoubleArray(shortArray: ShortArray):DoubleArray{
    val doubleArray=DoubleArray(shortArray.size)
    for (i in shortArray.indices){
        doubleArray[i]=shortArray[i].toDouble()
    }
    return doubleArray
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