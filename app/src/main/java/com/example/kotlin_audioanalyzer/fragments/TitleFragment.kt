package com.example.kotlin_audioanalyzer.fragments

import android.content.ContentResolver
import android.media.MediaPlayer
import android.net.Uri
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.R
import com.example.kotlin_audioanalyzer.utils.APP_ACTIVITY
import java.io.File
import java.net.URI

class TitleFragment : Fragment(R.layout.fragment_title) {

    var mp= MediaPlayer.create(APP_ACTIVITY,R.raw.doremi )


    val fil:File= File("android.resource://com.example.kotlin_audioanalyzer/" + R.raw.doremi)
}