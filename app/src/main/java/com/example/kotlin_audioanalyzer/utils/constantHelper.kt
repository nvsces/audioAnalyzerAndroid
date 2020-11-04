package com.example.kotlin_audioanalyzer.utils

import com.example.kotlin_audioanalyzer.MainActivity
import java.io.File

val MFCC="mfcc"
val BASIC_TONE="tone"

lateinit var mFile:File

lateinit var APP_ACTIVITY: MainActivity
var samplingRate=44100
var fftResolution=4096
var numFilters=13


var mfcc_realtime=false
var frameSizeEdit=1f


var currentRun=false
var etalonRun=false

var streamRealTime=true

var realtimebolean=false