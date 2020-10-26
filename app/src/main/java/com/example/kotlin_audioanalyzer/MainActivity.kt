package com.example.kotlin_audioanalyzer

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.kotlin_audioanalyzer.Spectrogramm.VoiceEtalonFragment
import com.example.kotlin_audioanalyzer.databinding.ActivityMainBinding
import com.example.kotlin_audioanalyzer.utils.APP_ACTIVITY
import com.example.kotlin_audioanalyzer.utils.RECORD_AUDIO
import com.example.kotlin_audioanalyzer.utils.checkPermissions
import com.example.kotlin_audioanalyzer.utils.replaceFragment

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY=this

        if (checkPermissions(RECORD_AUDIO)){
            replaceFragment(VoiceEtalonFragment())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ContextCompat.checkSelfPermission(APP_ACTIVITY, RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED){
            replaceFragment(VoiceEtalonFragment())
        }else replaceFragment(TitleFragment())
    }
}