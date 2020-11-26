package cz.kotox.core.utility

import timber.log.Timber
import java.util.Locale

fun getHexCodeForIntColorWithAlpha(color: Int?): String {
	val ret = color?.let { Integer.toHexString(it).toUpperCase(Locale.ROOT) } ?: ""
	Timber.d(">>>_ hexCode for colorWithAlpha=${color} is $ret")
	return ret
}

fun getHexCodeForIntColorNoAlpha(color: Int?): String {
	val ret = color?.let { Integer.toHexString(it).toUpperCase(Locale.ROOT).substring(2) } ?: ""
 	Timber.d(">>>_ hexCode for colorNoAlpha=${color} is $ret")
	return ret
}
