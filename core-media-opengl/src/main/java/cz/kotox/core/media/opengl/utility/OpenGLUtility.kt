package cz.kotox.core.media.opengl.utility

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import timber.log.Timber

fun getOpenGLVersionFromActivityManager(context: Context): Int =
	try {
		val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
		val configInfo = activityManager.deviceConfigurationInfo
		if (configInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
			configInfo.reqGlEsVersion
		} else {
			1 shl 16 // Lack of property means OpenGL ES version 1
		}
	} catch (th: Throwable) {
		Timber.e(th, "Unable to detect openGL ES version")
		-1
	}

/**
 * ro.opengles.version
 */
fun getOpenGLESString(openGLESCode: Int): String {
	val openGLESName = when (openGLESCode) {
		65535 -> "OpenGL ES 1.0"
		65536, 65537 -> "OpenGL ES 1.1"
		131072 -> "OpenGL ES 2.0"
		196608 -> "OpenGL ES 3.0"
		196609 -> "OpenGL ES 3.1"
		196610 -> "OpenGL ES 3.2"
		else -> "Unknown"
	}
	return "$openGLESCode $openGLESName"
}
