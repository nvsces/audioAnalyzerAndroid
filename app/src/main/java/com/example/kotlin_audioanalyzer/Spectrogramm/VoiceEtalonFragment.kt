package com.example.kotlin_audioanalyzer.Spectrogramm

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.utils.countVoice
import com.example.kotlin_audioanalyzer.utils.etalonRun
import com.example.kotlin_audioanalyzer.utils.replaceFragment
import kotlinx.android.synthetic.main.fragment_voice_etalon.*
import kotlinx.android.synthetic.main.main.*
import java.io.File

class VoiceEtalonFragment : Fragment(R.layout.fragment_voice_etalon) {

    private var Voicerecorder: VoiceRecord? = null
    private var fs=44100
    private  var mEtalonList=ArrayList<ShortArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Voicerecorder= VoiceRecord(fs)
        Voicerecorder?.prepare(1024)
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
                        Voicerecorder?.start(){ countVoice++}
                } else if (event.action== MotionEvent.ACTION_UP){
                    btn_voice_etalon.setBackgroundResource(R.drawable.ic_voice)
                    etalonRun =false
                    Voicerecorder?.stop()
                    mEtalonList.addAll(Voicerecorder!!.etalonList)
                    Voicerecorder!!.etalonList.clear()
                    Log.d("TEST", countVoice.toString())
                   replaceFragment(InfoBufferFragment(mEtalonList))
                }
            true
        }
    }
}