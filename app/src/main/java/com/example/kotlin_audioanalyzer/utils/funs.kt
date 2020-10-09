package com.example.kotlin_audioanalyzer.utils

import android.app.Activity
import android.preference.PreferenceManager
import java.util.*

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

// PREFERENCES
fun setPreference(a: Activity?, key: String?, value: Boolean) {
    PreferenceManager.getDefaultSharedPreferences(a).edit().putBoolean(key, value).commit()
}

fun setPreference(a: Activity?, key: String?, value: Float) {
    PreferenceManager.getDefaultSharedPreferences(a).edit().putFloat(key, value).commit()
}

fun setPreference(a: Activity?, key: String?, value: Int) {
    PreferenceManager.getDefaultSharedPreferences(a).edit().putInt(key, value).commit()
}

fun setPreference(a: Activity?, key: String?, value: Long) {
    PreferenceManager.getDefaultSharedPreferences(a).edit().putLong(key, value).commit()
}

fun setPreference(a: Activity?, key: String?, value: String?) {
    PreferenceManager.getDefaultSharedPreferences(a).edit().putString(key, value).commit()
}

fun getPreference(a: Activity?, key: String?, def: Boolean): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(a).getBoolean(key, def)
}

fun getPreference(a: Activity?, key: String?, def: Float): Float {
    return PreferenceManager.getDefaultSharedPreferences(a).getFloat(key, def)
}

fun getPreference(a: Activity?, key: String?, def: Int): Int {
    return PreferenceManager.getDefaultSharedPreferences(a).getInt(key, def)
}

fun getPreference(a: Activity?, key: String?, def: Long): Long {
    return PreferenceManager.getDefaultSharedPreferences(a).getLong(key, def)
}

fun getPreference(a: Activity?, key: String?, def: String?): String? {
    return PreferenceManager.getDefaultSharedPreferences(a).getString(key, def)
}

fun setPreference(key: String?, value: Boolean) {
    setPreference(getActivity(), key, value)
}

fun setPreference(key: String?, value: Float) {
    setPreference(getActivity(), key, value)
}

fun setPreference(key: String?, value: Int) {
    setPreference(getActivity(), key, value)
}

fun setPreference(key: String?, value: Long) {
    setPreference(getActivity(), key, value)
}

fun setPreference(key: String?, value: String?) {
    setPreference(getActivity(), key, value)
}

fun getPreference(key: String?, def: Boolean): Boolean {
    return getPreference(getActivity(), key, def)
}

fun getPreference(key: String?, def: Float): Float {
    return getPreference(getActivity(), key, def)
}

fun getPreference(key: String?, def: Int): Int {
    return getPreference(getActivity(), key, def)
}

fun getPreference(key: String?, def: Long): Long {
    return getPreference(getActivity(), key, def)
}

fun getPreference(key: String?, def: String?): String? {
    return getPreference(getActivity(), key, def)
}

fun getActivity(): Activity? {
    return getAttribute("activity") as Activity?
}