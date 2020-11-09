package com.example.kotlin_audioanalyzer.fragments

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.VoiceRecord
import com.example.kotlin_audioanalyzer.feature.FFT_
import com.example.kotlin_audioanalyzer.feature.hammingWindow
import com.example.kotlin_audioanalyzer.feature.sonopy.FFT
import com.example.kotlin_audioanalyzer.feature.sonopy.Sonopy
import com.example.kotlin_audioanalyzer.utils.*
import kotlinx.android.synthetic.main.fragment_info_buffer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InfoBufferFragment(var listBuffer: ArrayList<FloatArray>) :
    Fragment(R.layout.fragment_info_buffer) {

    private var method = BASIC_TONE
    private lateinit var sonopy: Sonopy
    private var bufferBasicToneRealTime = ArrayList<Float>()
    private var bufferMfccRealTime = ArrayList<FloatArray>()
    private var bufferFFTDataEtalon = ArrayList<FloatArray>()
    private var bufferFFTDataCurrent = ArrayList<FloatArray>()
    private var listMFCC = ArrayList<Array<FloatArray>>()
    private var listMFCCOut = ArrayList<FloatArray>()
    private var voiceRecord: VoiceRecord? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.sittings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_spectr -> {
                showToast("Меню спектр")
                tv_info_text.text = "Основной тон"
                method = BASIC_TONE
                mfcc_view.visibility = View.GONE
                frq_view.visibility = View.VISIBLE
            }
            R.id.settings_menu_mfcc -> {
                method = MFCC
                tv_info_text.text = "MFCC"
                mfcc_view.visibility = View.VISIBLE
                frq_view.visibility = View.GONE
                //replaceFragment(MfccFragment(listBuffer))
            }
            R.id.settings_menu_sample -> {
                replaceFragment(SpecFragment(bufferFFTDataEtalon, bufferFFTDataCurrent))
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        voiceRecord = VoiceRecord(samplingRate)
        voiceRecord?.prepare(fftResolution)
        sonopy = Sonopy(samplingRate, listBuffer[0].size, 0, fftResolution / 2, numFilters)
    }

    override fun onResume() {
        super.onResume()
        frq_view.setBackgroundColor(Color.BLACK)
        bufferFFTDataEtalon.clear()
        bufferFFTDataCurrent.clear()
        initBasicTone()
        initMFCC()
        initBtn()
    }

    private fun initBtn() {
        info_start.setOnClickListener {
            frameSizeEdit = info_size_frame.text.toString().toFloat()
            (it as Button).text = "stop"
            currentRun = true

            when (method) {
                BASIC_TONE -> {
                    streamRealTime = true
                    realtimebolean = true
                    bufferBasicToneRealTime.clear()
                    voiceRecord?.startTime() { recordBuffer ->
                        basicToneProcessing(recordBuffer)
                    }
                    startCurrentVoiceBasicTone()
                }
                MFCC -> {
                    streamRealTime = true
                    bufferMfccRealTime.clear()
                    voiceRecord?.startTime() { recordBuffer ->
                        mfccRealTimeProcessing(recordBuffer)
                    }
                    startCurrentVoiceMfcc()
                }
            }
        }
        info_btn_sound.setOnClickListener {
            val player = MediaPlayer.create(APP_ACTIVITY, Uri.fromFile(mFile))
            player?.setVolume(1.0F, 1.0F)
            player?.start()
            //player?.release()
        }
    }

    private fun mfccRealTimeProcessing(recordBuffer: ShortArray) {
        val mfcc = sonopy.mfccSpec(short2FloatArray(recordBuffer), numFilters)
        bufferMfccRealTime.add(deleteFrameIsMFCC(mfcc))
        mfcc_realtime = true
        mfcc_view.setRealTimeWave(bufferMfccRealTime)
        CoroutineScope(Dispatchers.IO).launch {
            mfcc_view.invalidate()
        }
    }

    private fun startCurrentVoiceMfcc() {
        CoroutineScope(Dispatchers.Default).launch {
            while (streamRealTime) {
                if (bufferMfccRealTime.size == listBuffer.size) {
                    currentRun = false
                    streamRealTime = false
                    voiceRecord?.stop()
                    APP_ACTIVITY.runOnUiThread { info_start.text = "Start" }
                }
            }
        }
    }

    private fun startCurrentVoiceBasicTone() {
        CoroutineScope(Dispatchers.Default).launch {
            while (streamRealTime) {
                if (bufferBasicToneRealTime.size == listBuffer.size) {
                    currentRun = false
                    voiceRecord?.stop()
                    APP_ACTIVITY.runOnUiThread { info_start.text = "Start" }
                    startCurrentVoiceReset()
                }
            }
        }
    }

    private fun startCurrentVoiceReset() {
        realtimebolean = false
        streamRealTime = false
    }

    private fun basicToneProcessing(recordBuffer: ShortArray) {
        val fftData = FFT.rfft(short2FloatArray(recordBuffer), fftResolution)
        val tempRe = fftData[0]
        val tempIm = fftData[1]
        val absSpec = FloatArray(fftResolution / 2)
        val magnitude: FloatArray = magnitudeSpec(tempRe, tempIm)
        System.arraycopy(magnitude, 0, absSpec, 0, fftResolution / 2)
        bufferFFTDataCurrent.add(absSpec)
        val maxIdx = absSpec.indices.maxBy { absSpec[it] } ?: -1
        bufferBasicToneRealTime.add(maxIdx.toFloat())
        frq_view.setRealTimeWave(bufferBasicToneRealTime)
        CoroutineScope(Dispatchers.IO).launch {
            frq_view.invalidate()
        }
    }

    private fun initGraph() {
        listMFCCOut.addAll(deleteFrameListIsMFCC(listMFCC))
        mfcc_view.setWave(listMFCCOut)
        mfcc_view.invalidate()
    }

    private fun initMFCC() {
        for (i in 0 until listBuffer.size) {
            val mfcc = sonopy.mfccSpec(listBuffer[i], numFilters)
            listMFCC.add(mfcc)
        }
        initGraph()
    }

    private fun initBasicTone() {
        var overlapListBuffer = ArrayList<FloatArray>()
        overlapListBuffer = overlapWindowDataBuffer(listBuffer)

        val n = fftResolution
        val arrayTon = FloatArray(overlapListBuffer.size)

        val absSpec = FloatArray(n / 2)
        val samplWindow= hammingWindow(overlapListBuffer[0])
        val fftTemp = FFT.rfft(samplWindow, n)
        val re = fftTemp[0]
        val im = fftTemp[1]
        val magnitude: FloatArray = magnitudeSpec(re, im)
        System.arraycopy(magnitude, 0, absSpec, 0, re.size / 2)

        bufferFFTDataEtalon.add(absSpec)
        var maxIdx = absSpec.indices.maxBy { absSpec[it] } ?: -1
        arrayTon[0] = maxIdx.toFloat()

        for (i in 1 until overlapListBuffer.size) {
            val temFFT = FloatArray(n / 2)
            val tempSamplWindow= hammingWindow(overlapListBuffer[i])
            val tempRe = FFT.rfft(tempSamplWindow, n)[0]
            val tempIm = FFT.rfft(tempSamplWindow, n)[1]
            val mag: FloatArray = magnitudeSpec(tempRe, tempIm)
            System.arraycopy(mag, 0, temFFT, 0, re.size / 2)
            bufferFFTDataEtalon.add(temFFT)
            maxIdx = temFFT.indices.maxBy { temFFT[it] } ?: -1
            arrayTon[i] = maxIdx.toFloat()
        }
        frq_view.setWaveFrequency(arrayTon)
        frq_view.invalidate()
    }
}