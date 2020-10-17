package com.example.kotlin_audioanalyzer.utils

import android.app.Activity
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.kotlin_audioanalyzer.MainActivity
import com.example.kotlin_audioanalyzer.R
import java.util.*

lateinit var APP_ACTIVITY: MainActivity
var checkqq=false
var countVoice:Long=0
var etalonRun=false

var realtimebolean=false

var map = HashMap<String, Any>()
fun getAttribute(s: String): Any? {
    return map[s]
}

fun setAttribute(s: String, o: Any) {
    map[s] = o
}

fun resetAttributes() {
    map = HashMap()
}


fun AppCompatActivity.replaceFragment(fragment: Fragment, addStack:Boolean=true){
    if (addStack) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }else{
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.data_container,
                fragment
            ).commit()
    }
}

fun Fragment.replaceFragment(fragment: Fragment){
    this.fragmentManager?.beginTransaction()
        ?.addToBackStack(null)
        ?.replace(
            R.id.data_container,
            fragment
        )?.commit()
}

fun showToast(message: String){
    Toast.makeText(APP_ACTIVITY,message, Toast.LENGTH_SHORT).show()
}