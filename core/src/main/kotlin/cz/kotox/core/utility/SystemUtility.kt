package cz.kotox.core.utility

import android.os.Build

fun getDeviceAndOsVersionString(): String = "Android ${Build.VERSION.SDK_INT}; ${Build.MANUFACTURER} ${Build.MODEL}"
fun getDeviceAndOsVersionShortString(): String = "${Build.VERSION.SDK_INT}${Build.MANUFACTURER}${Build.MODEL}".trim().replace(" ","")