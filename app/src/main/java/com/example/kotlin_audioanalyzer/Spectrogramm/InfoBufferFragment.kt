package com.example.kotlin_audioanalyzer.Spectrogramm

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.utils.countVoice
import com.example.kotlin_audioanalyzer.utils.etalonRun
import com.example.kotlin_audioanalyzer.utils.realtimebolean
import com.example.kotlin_audioanalyzer.utils.showToast
import kotlinx.android.synthetic.main.fragment_info_buffer.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.galmiza.android.engine.sound.SoundEngine
import kotlin.coroutines.CoroutineContext

class InfoBufferFragment(var listBuffer:ArrayList<ShortArray>) : Fragment(R.layout.fragment_info_buffer) {
    private lateinit var nativeLib: SoundEngine
    var fftResolution=1024
    private lateinit var bufferStack // Store trunks of buffers
            : FloatArray
    private lateinit var ton:FloatArray
    private lateinit var tempton:FloatArray
    private lateinit var tempReal:FloatArray
    private  var bufferRealTime =ArrayList<Float>()
    private lateinit var re // buffer holding real part during fft process
            : FloatArray
    private lateinit var im // buffer holding imaginary part during fft process
            : FloatArray
    private  var mEtalonList=ArrayList<ShortArray>()

    private var Voicerecorder: VoiceRecord? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        this.nativeLib = SoundEngine()
        this.nativeLib.initFSin()
        Voicerecorder= VoiceRecord(44100)
        Voicerecorder?.prepare(1024)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.sittings_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_spectr-> showToast("Меню спектр")
            R.id.settings_menu_graph-> showToast("Меню график")
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initBtn() {
        info_start.setOnClickListener {
            if (!etalonRun) {
                etalonRun = true
                realtimebolean=true
                mEtalonList.clear()
                (it as Button).text = "stop"
                Voicerecorder?.startTime(){recordBuffer ->  
                    getTrunks(recordBuffer)
                }
            }
        }
    }

    private fun getTrunks(recordBuffer: ShortArray) {
       val  fftReal=fft(nativeLib,recordBuffer,fftResolution)
        val temp=FloatArray(fftResolution)
        System.arraycopy(fftReal, 0, temp, 0, fftResolution/2)
        val maxIdx = temp.indices.maxBy { temp[it] } ?: -1
        bufferRealTime.add(maxIdx.toFloat())
       frq_view.setRealTimeWave(bufferRealTime)
        CoroutineScope(Dispatchers.IO).launch {
            frq_view.invalidate()
            Log.d("TEST",bufferRealTime[0].toString())
        }
    }

    override fun onResume() {
        super.onResume()
        frq_view.setBackgroundColor(Color.BLACK)
        frequencyProcess()
        initBtn()
    }

    fun listShortArray2FloatArray(){

    }

    private fun fft(nativeLib: SoundEngine, input: ShortArray, fftResolution:Int): FloatArray {

        var real = FloatArray(fftResolution)
        var imag = FloatArray(fftResolution)

        nativeLib.shortToFloat(input, real, fftResolution)
        nativeLib.clearFloat(imag, fftResolution)

        val log2_n = (Math.log(fftResolution.toDouble()) / Math.log(2.0)).toInt()
        nativeLib.windowHamming(real, fftResolution)
        nativeLib.fft(real, imag, log2_n, 0) // Move into frquency domain
        nativeLib.toPolar(real, imag, fftResolution)

        return real
    }

    private fun frequencyProcess() {
        val n = fftResolution

        re = FloatArray(n)
        im = FloatArray(n)

        var arrayTon=FloatArray(listBuffer.size)

        tempReal= FloatArray(n/2)
        re=fft(nativeLib,listBuffer[2],n)
        System.arraycopy(re, 0, tempReal, 0, re.size/2)

        val maxIdx = tempReal.indices.maxBy { tempReal[it] } ?: -1
        ton= FloatArray(tempReal.size)

        arrayTon[0]=maxIdx.toFloat()

        for (i in 0 until tempReal.size){
            ton[i]=maxIdx.toFloat()
        }

        tempton=FloatArray(tempReal.size)
        bufferStack=tempReal
        for (i in 1 until listBuffer.size){

            re=fft(nativeLib,listBuffer[i],n)

            System.arraycopy(re, 0, tempReal, 0, re.size/2)

          val   maxIdxN = tempReal.indices.maxBy { tempReal[it] } ?: -1
            arrayTon[i]=maxIdxN.toFloat()
            for (k in  0 until  tempReal.size){
                tempton[k]=maxIdxN.toFloat()
            }
            ton=ton+tempton
        }
        frq_view.setWaveFrequency(arrayTon)
        frq_view.invalidate()
    }
}