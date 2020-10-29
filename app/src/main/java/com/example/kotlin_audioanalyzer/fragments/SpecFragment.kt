package com.example.kotlin_audioanalyzer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.widget.SeekBar
import com.example.kotlin_audioanalyzer.R
import kotlinx.android.synthetic.main.fragment_spec.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SpecFragment( val listBufferEtalon:  ArrayList<FloatArray>,val listBufferCureent: ArrayList<FloatArray>)
    : Fragment(R.layout.fragment_spec) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        spec_textView.text="0"
        spec_seekBar.max=listBufferEtalon.size-1
        fram_spec_etalon_view.setWave(listBufferEtalon[0])
        fram_spec_current_view.setWave(listBufferCureent[0])
        fram_spec_etalon_view.invalidate()
        fram_spec_current_view.invalidate()

        spec_seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                spec_textView.text=progress.toString()
                fram_spec_etalon_view.setWave(listBufferEtalon[progress])
                fram_spec_current_view.setWave(listBufferCureent[progress])
                CoroutineScope(Dispatchers.IO).launch{
                    fram_spec_etalon_view.invalidate()
                    fram_spec_current_view.invalidate()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

    }
}