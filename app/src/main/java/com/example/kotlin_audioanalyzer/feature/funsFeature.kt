package com.example.kotlin_audioanalyzer.feature

import kotlin.math.cos

fun hammingWindow(input:FloatArray):FloatArray{
    val N=input.size
    val out=FloatArray(N)
    val pi=3.14
    val Wn=FloatArray(N)
    for (i in 0 until N){
        Wn[i]= (0.54- 0.46*cos(2*pi*i/N)).toFloat()
        out[i]=Wn[i]*input[i]
    }
    return out
}