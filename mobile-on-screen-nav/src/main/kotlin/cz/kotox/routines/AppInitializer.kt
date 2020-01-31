package cz.kotox.routines

import android.content.Context
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject

class AppInitializer @Inject constructor(private val appContext: Context) {

	/**
	 * Should be called in application onCreate method
	 */
	fun init() {
		// Init default RxError handling
		RxJavaPlugins.setErrorHandler { e ->
			when (e) {
				is UndeliverableException -> {
					//"Irrelevant network problem or API that throws on cancellation"
					Timber.e(e.cause ?: e)
				}
				is InterruptedException -> {
					//Blocking code was interrupted by a dispose call
					Timber.e(e)
				}
				else -> {
					//Keep other exception types flowing
					Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
				}
			}
		}

		// initialize analytics providers
//		if (BuildConfig.ANALYTICS_ENABLED) {
//			analyticsProvider.init()
//		}

	}
}