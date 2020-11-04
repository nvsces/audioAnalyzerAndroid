package com.example.kotlin_audioanalyzer

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.kotlin_audioanalyzer.databinding.ActivityMainBinding
import com.example.kotlin_audioanalyzer.fragments.TitleFragment
import com.example.kotlin_audioanalyzer.fragments.VoiceEtalonFragment
import com.example.kotlin_audioanalyzer.utils.*
import com.example.kotlin_audioanalyzer.utils.WavFile.WavFile
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY=this
       // testRead()

        if (checkPermissions(RECORD_AUDIO)){
            replaceFragment(VoiceEtalonFragment())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun testRead(){

        val inputStream: InputStream = APP_ACTIVITY.getResources().openRawResource(com.example.kotlin_audioanalyzer.R.raw.mifasol)
        val mFile= File(APP_ACTIVITY.filesDir,"etalon.wav")
        mFile.createNewFile()
        val outputStream=FileOutputStream(mFile)

        FileUtils.copy(inputStream,outputStream)

        val wavFile=WavFile.openWavFile(mFile)


        val numFrames=wavFile.numFrames.toInt()
        val sampleBuffer=DoubleArray(numFrames*2)
        wavFile.readFrames(sampleBuffer,numFrames)
        wavFile.display()
        var dataWavOne=splitDataWavChannelsToFloat(sampleBuffer)[0]
        var dataWavTwo=splitDataWavChannelsToFloat(sampleBuffer)[1]

        Log.d("TEST","data one")

        Log.d("TEST",dataWavOne[71217-3].toString())
        Log.d("TEST",dataWavOne[71217-2].toString())
        Log.d("TEST",dataWavOne[71217-1].toString())
        Log.d("TEST",dataWavOne[71217].toString())
        Log.d("TEST",dataWavOne[71217+1].toString())
        Log.d("TEST",dataWavOne[71217+2].toString())
        Log.d("TEST",dataWavOne[71217+3].toString())

        Log.d("TEST","data two")

        Log.d("TEST",dataWavTwo[71217-3].toString())
        Log.d("TEST",dataWavTwo[71217-2].toString())
        Log.d("TEST",dataWavTwo[71217-1].toString())
        Log.d("TEST",dataWavTwo[71217].toString())
        Log.d("TEST",dataWavTwo[71217+1].toString())
        Log.d("TEST",dataWavTwo[71217+2].toString())
        Log.d("TEST",dataWavTwo[71217+3].toString())
    }




    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            replaceFragment(VoiceEtalonFragment())
        }else replaceFragment(TitleFragment())
    }
}