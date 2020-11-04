package com.example.kotlin_audioanalyzer.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.FileUtils
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.VoiceRecord
import com.example.kotlin_audioanalyzer.utils.*
import com.example.kotlin_audioanalyzer.utils.WavFile.WavFile
import kotlinx.android.synthetic.main.fragment_voice_etalon.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Math.floor

class VoiceEtalonFragment : Fragment(R.layout.fragment_voice_etalon) {

    private var Voicerecorder: VoiceRecord? = null
    private var mEtalonList = ArrayList<FloatArray>()
    private lateinit var dataWavOne:FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Voicerecorder = VoiceRecord(samplingRate)
        Voicerecorder?.prepare(fftResolution)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        initButton()
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    private fun initButton() {
        btn_voice_etalon.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice_blue)
                etalonRun = true
                mEtalonList.clear()
                Voicerecorder?.start() { }
            } else if (event.action == MotionEvent.ACTION_UP) {
                btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice)
                etalonRun = false
                Voicerecorder?.stop()
                mEtalonList.addAll(shortArrayListToFloatArrayList(Voicerecorder!!.etalonList))

                Voicerecorder!!.etalonList.clear()
                replaceFragment(InfoBufferFragment(mEtalonList))
            }
            true
        }
        /////////////////////////////////////////////////////////////////////////////
        voice_etalon_btn_cash_etalon.setOnClickListener {
            val inputStream: InputStream = APP_ACTIVITY.getResources()
                .openRawResource(R.raw.mifasol)
            mFile = File(APP_ACTIVITY.filesDir, "etalon.wav")
            mFile.createNewFile()
            val outputStream = FileOutputStream(mFile)
            FileUtils.copy(inputStream, outputStream)
            val wavFile = WavFile.openWavFile(mFile)
            val numFrames = wavFile.numFrames.toInt()
            val sampleBuffer = DoubleArray(numFrames * wavFile.numChannels)
            wavFile.readFrames(sampleBuffer, numFrames)
            if (wavFile.numChannels==2) {
                 dataWavOne = splitDataWavChannelsToFloat(sampleBuffer)[1]
            } else {
                 dataWavOne=doubleToFloat(sampleBuffer)
            }
            mEtalonList = floatArraySplitSample(dataWavOne, fftResolution)
            replaceFragment(InfoBufferFragment(mEtalonList))
        }
    }

    private fun doubleToFloat(sampleBuffer: DoubleArray): FloatArray {
        val outArray=FloatArray(sampleBuffer.size)
        for (i in 0 until sampleBuffer.size){
            outArray[i]=sampleBuffer[i].toFloat()
        }
        return outArray
    }

    private fun floatArraySplitSample(dataSplit: FloatArray, bufferSize: Int, ): ArrayList<FloatArray> {
        val outList = ArrayList<FloatArray>()

        val n = floor(dataSplit.size / bufferSize.toDouble()).toInt()
        for (i in 0 until n) {
            val tempArray = FloatArray(bufferSize)
            System.arraycopy(dataSplit, i * bufferSize, tempArray, 0, bufferSize)
            outList.add(tempArray)
        }
        return outList
    }


}