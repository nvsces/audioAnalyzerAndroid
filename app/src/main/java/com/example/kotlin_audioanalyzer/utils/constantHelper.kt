package com.example.kotlin_audioanalyzer.utils

import com.example.kotlin_audioanalyzer.MainActivity

val MFCC="mfcc"
val BASIC_TONE="tone"


lateinit var APP_ACTIVITY: MainActivity
var samplingRate=44100
var fftResolution=2048
var numFilters=13


var mfcc_realtime=false
var frameSizeEdit=1f


var currentRun=false
var etalonRun=false

var streamRealTime=true

var realtimebolean=false