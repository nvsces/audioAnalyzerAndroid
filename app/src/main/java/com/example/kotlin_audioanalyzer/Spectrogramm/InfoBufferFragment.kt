package com.example.kotlin_audioanalyzer.Spectrogramm

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.feature.FFT_
import com.example.kotlin_audioanalyzer.feature.sonopy.FFT
import com.example.kotlin_audioanalyzer.utils.*
import kotlinx.android.synthetic.main.fragment_info_buffer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.galmiza.android.engine.sound.SoundEngine

class InfoBufferFragment(var listBuffer: ArrayList<ShortArray>, val countEtalon: Long) :
    Fragment(R.layout.fragment_info_buffer) {

    private lateinit var fftClass: FFT_
    private lateinit var tempReal: FloatArray
    private var bufferRealTime = ArrayList<Float>()
    private var bufferFFTDataEtalon=ArrayList<FloatArray>()
    private var bufferFFTDataCurrent=ArrayList<FloatArray>()
    private lateinit var re // buffer holding real part during fft process
            : FloatArray
    private lateinit var im // buffer holding imaginary part during fft process
            : FloatArray
    private var mEtalonList = ArrayList<ShortArray>()
    private var voiceRecord: VoiceRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        fftClass= FFT_()
        voiceRecord = VoiceRecord(44100)
        voiceRecord?.prepare(1024)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.sittings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings_menu_spectr -> {
                showToast("Меню спектр")
                replaceFragment(SpecFragment(bufferFFTDataEtalon,bufferFFTDataCurrent))
            }
            R.id.settings_menu_graph -> showToast("Меню график")
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initBtn() {
        info_start.setOnClickListener {
        frameSizeEdit=info_size_frame.text.toString().toFloat()
            if (!etalonRun) {
                etalonRun = true
                stream=true
                realtimebolean = true
                mEtalonList.clear()
                bufferRealTime.clear()
                (it as Button).text = "stop"
                voiceRecord?.startTime() { recordBuffer ->
                    getTrunks(recordBuffer)
                }
                strartCurrentVoice()
            }
        }
    }

    private fun strartCurrentVoice() {
        CoroutineScope(Dispatchers.Default).launch {
            while (stream){
                if (countVoice<1) {
                    voiceRecord?.stop()
                    APP_ACTIVITY.runOnUiThread { info_start.text="Start" }
                    startCurrentVoiceReset()
                }
            }
        }
    }
    private fun startCurrentVoiceReset() {
        countVoice=countEtalon
        etalonRun=false
        realtimebolean=false
        stream=false
    }

    private fun  getTrunks(recordBuffer: ShortArray) {
        val fftReal = FFT.rfft(short2FloatArray(recordBuffer), fftResolution)
        val temp = FloatArray(fftResolution/2)
        System.arraycopy(fftReal, 0, temp, 0, fftResolution / 2)
        bufferFFTDataCurrent.add(temp)
        val maxIdx = temp.indices.maxBy { temp[it] } ?: -1
        bufferRealTime.add(maxIdx.toFloat())
        frq_view.setRealTimeWave(bufferRealTime)
        CoroutineScope(Dispatchers.IO).launch {
            frq_view.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        frq_view.setBackgroundColor(Color.BLACK)
        countVoice=countEtalon
        frequencyProcess()
        initBtn()
    }

    fun listShortArray2FloatArray() {

    }
    
    private fun frequencyProcess() {
        val n = fftResolution
        re = FloatArray(n)
        im = FloatArray(n)
        val arrayTon = FloatArray(listBuffer.size)

        tempReal = FloatArray(n / 2)
        val fftTemp=FFT.rfft(short2FloatArray(listBuffer[2]), n)
        re=fftTemp[0]
        im=fftTemp[1]
        System.arraycopy(re, 0, tempReal, 0, re.size / 2)

        bufferFFTDataEtalon.add(tempReal)
        var maxIdx = tempReal.indices.maxBy { tempReal[it] } ?: -1

        arrayTon[0] = maxIdx.toFloat()

        for (i in 1 until listBuffer.size) {
            val temFFT=FloatArray(n / 2)
            re = FFT.rfft(short2FloatArray(listBuffer[i]), n)[0]
            System.arraycopy(re, 0, temFFT, 0, re.size / 2)
            bufferFFTDataEtalon.add(temFFT)
            maxIdx = temFFT.indices.maxBy { temFFT[it] } ?: -1
            arrayTon[i] = maxIdx.toFloat()
        }
        frq_view.setWaveFrequency(arrayTon)
        frq_view.invalidate()
    }

}