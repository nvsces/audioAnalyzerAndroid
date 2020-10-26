package com.example.kotlin_audioanalyzer.Spectrogramm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.feature.sonopy.Sonopy
import com.example.kotlin_audioanalyzer.utils.*
import kotlinx.android.synthetic.main.fragment_voice_etalon.*

class VoiceEtalonFragment : Fragment(R.layout.fragment_voice_etalon) {

    private var Voicerecorder: VoiceRecord? = null
    private  var mEtalonList=ArrayList<ShortArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Voicerecorder= VoiceRecord(samplingRate)
        Voicerecorder?.prepare(fftResolution)
    }

    override fun onResume() {
        super.onResume()
        initButton()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initButton() {
        btn_voice_etalon.setOnTouchListener { v, event ->
                if (event.action== MotionEvent.ACTION_DOWN){
                    btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice_blue)
                        etalonRun = true
                    mEtalonList.clear()
                        Voicerecorder?.start(){ }
                } else if (event.action== MotionEvent.ACTION_UP){
                    btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice)
                    etalonRun =false
                    Voicerecorder?.stop()
                    mEtalonList.addAll(Voicerecorder!!.etalonList)
                    Voicerecorder!!.etalonList.clear()
                   replaceFragment(InfoBufferFragment(mEtalonList))
                }
            true
        }
    }
}