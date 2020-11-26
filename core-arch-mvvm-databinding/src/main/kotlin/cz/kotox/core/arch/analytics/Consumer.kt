package cz.kotox.core.arch.analytics

import android.app.Application
import android.os.Bundle
//FIXME decouple firebase analytics to analytics module
//import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

typealias EventParams = Map<String, Any>

interface EventsConsumer {
	fun updateUser(userId: String?)
	fun trackSimple(eventName: String)
	fun trackParams(eventName: String, params: EventParams)
}

@Singleton
class FirebaseEventsConsumer @Inject constructor(
	application: Application
) : EventsConsumer {

	//FIXME decouple firebase analytics to analytics module
//	private val firebaseAnalytics = FirebaseAnalytics.getInstance(application)

	override fun updateUser(userId: String?) {

		//FIXME decouple firebase analytics to analytics module
		//firebaseAnalytics.setUserId(userId)
	}

	override fun trackSimple(eventName: String) {
		try {
			val params = Bundle()
			//FIXME decouple firebase analytics to analytics module
			//firebaseAnalytics.logEvent(eventName.replace(" ", "_"), params)
		} catch (e: Exception) {
			Timber.e(e, "FirebaseEventsConsumer - simple: $e")
		}
	}

	override fun trackParams(eventName: String, params: EventParams) {
		try {
			val bundle = Bundle().apply {
				for (param in params.entries) {
					when (param.value) {
						is String -> putString(param.key, param.value as String)
						is Long -> putLong(param.key, param.value as Long)
						is Int -> putLong(param.key, (param.value as Int).toLong())
						is Double -> putDouble(param.key, param.value as Double)
						is Float -> putDouble(param.key, (param.value as Float).toDouble())
						is Boolean -> putBoolean(param.key, param.value as Boolean)
						else -> throw IllegalArgumentException("${param.value.javaClass} is not supported.")
					}
				}
			}

			//FIXME decouple firebase analytics to analytics module
			//firebaseAnalytics.logEvent(eventName.replace(" ", "_"), bundle)
		} catch (e: Exception) {
			Timber.e(e, "FirebaseEventsConsumer - params: $e")
		}
	}
}