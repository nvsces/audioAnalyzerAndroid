package com.example.kotlin_audioanalyzer

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.example.kotlin_audioanalyzer.Spectrogramm.SpectrogramActivity
import com.example.kotlin_audioanalyzer.Spectrogramm.VoiceEtalonFragment
import com.example.kotlin_audioanalyzer.databinding.ActivityMainBinding
import com.example.kotlin_audioanalyzer.utils.APP_ACTIVITY
import com.example.kotlin_audioanalyzer.utils.replaceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY=this
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            0
        )
        replaceFragment(VoiceEtalonFragment())
    }
}