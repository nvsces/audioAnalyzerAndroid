package com.example.kotlin_audioanalyzer.Spectrogramm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.feature.sonopy.Sonopy
import com.example.kotlin_audioanalyzer.utils.*
import kotlinx.android.synthetic.main.fragment_mfcc.*
import kotlinx.android.synthetic.main.fragment_spec.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MfccFragment(var listBuffer: ArrayList<ShortArray>) : Fragment(R.layout.fragment_mfcc) {

    private lateinit var sonopy: Sonopy
    private var listMFCC= ArrayList<Array<FloatArray>>()

    private var listMFCCOut=ArrayList<FloatArray>()


    private var listMFCCSpec=ArrayList<FloatArray>()
    override fun onStart() {
        super.onStart()
    }


    override fun onResume() {
        super.onResume()
        sonopy= Sonopy(samplingRate,listBuffer[0].size,0, fftResolution, numFilters)
        initMFCC()
        initGraph()
      //  initSeekBar()
    }

    private fun initSeekBar() {
        mfcc_seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mfcc_textView.text=progress.toString()
            //    mfcc_spec_view.setWave(listMFCCOut[progress])
                CoroutineScope(Dispatchers.IO).launch{
           //         mfcc_spec_view.invalidate()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

    }

    private fun initGraph() {
        mfcc_textView.text="0"
        mfcc_seekBar.max=listBuffer.size-1

        listMFCCOut.addAll(deleteFrameIsMFCC(listMFCC))
        v_mfcc_view.setWave(listMFCCOut)
        v_mfcc_view.invalidate()
//        mfcc_spec_view.image=false
//        mfcc_spec_view.setWave(listMFCCOut[0])
//        mfcc_spec_view.invalidate()
    }

    private fun initMFCC() {

        for (i in 0 until listBuffer.size){
            val mfcc=sonopy.mfccSpec(short2FloatArray(listBuffer[i]), numFilters)
            listMFCC.add(mfcc)
            val mfccSpec=Sonopy.powerSpec2(short2FloatArray(listBuffer[i]),listBuffer[i].size,0,
                fftResolution)
            listMFCCSpec.add(mfccSpec)

        }
      //  Log.d("TEST",listMFCCSpec.toString())
    }

    private fun test(mfcc: Array<FloatArray>) {

    }
}