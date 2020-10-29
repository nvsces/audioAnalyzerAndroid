package com.example.kotlin_audioanalyzer.fragments

import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.VoiceRecord
import com.example.kotlin_audioanalyzer.feature.sonopy.Sonopy
import com.example.kotlin_audioanalyzer.utils.*
import kotlinx.android.synthetic.main.fragment_mfcc.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MfccFragment(var listBuffer: ArrayList<ShortArray>) : Fragment(R.layout.fragment_mfcc) {

    private lateinit var sonopy: Sonopy
    private var listMFCC= ArrayList<Array<FloatArray>>()
    private var listMFCCOut=ArrayList<FloatArray>()
    private var bufferMfccRealTime = ArrayList<FloatArray>()
    private var voiceRecord: VoiceRecord? = null

    private var listMFCCSpec=ArrayList<FloatArray>()
    override fun onStart() {
        super.onStart()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        voiceRecord = VoiceRecord(samplingRate)
        voiceRecord?.prepare(1024)
        sonopy= Sonopy(samplingRate,listBuffer[0].size,0, fftResolution/2, numFilters)
    }

    override fun onResume() {
        super.onResume()
        initMFCC()
        initGraph()
        initBtn()
    }

    private fun initBtn() {
        mfcc_btn_start.setOnClickListener {
            streamMFCC=true
            currentRun=true
            bufferMfccRealTime.clear()
            (it as Button).text = "stop"
            voiceRecord?.startTime() { recordBuffer ->
                mfccRealTimeProsessing(recordBuffer)
            }
            strartCurrentVoice()
        }
    }

    private fun strartCurrentVoice() {
        CoroutineScope(Dispatchers.Default).launch {
            while (streamMFCC){
                if (bufferMfccRealTime.size==listBuffer.size) {
                    currentRun=false
                    streamMFCC=false
                    voiceRecord?.stop()
                    APP_ACTIVITY.runOnUiThread { mfcc_btn_start.text="Start" }
                    //startCurrentVoiceReset()
                }
            }
        }
    }

    private fun mfccRealTimeProsessing(recordBuffer: ShortArray) {
        val mfcc=sonopy.mfccSpec(short2FloatArray(recordBuffer), numFilters)
        bufferMfccRealTime.add(deleteFrameIsMFCC(mfcc))
        mfcc_realtime=true
        v_mfcc_view.setRealTimeWave(bufferMfccRealTime)
        CoroutineScope(Dispatchers.IO).launch {
            v_mfcc_view.invalidate()
        }
    }


    private fun initGraph() {
        mfcc_textView.text="MFCC"
        listMFCCOut.addAll(deleteFrameListIsMFCC(listMFCC))
        v_mfcc_view.setWave(listMFCCOut)
        v_mfcc_view.invalidate()
    }

    private fun initMFCC() {
        for (i in 0 until listBuffer.size){
            val mfcc=sonopy.mfccSpec(short2FloatArray(listBuffer[i]), numFilters)
            listMFCC.add(mfcc)
        }
    }
}