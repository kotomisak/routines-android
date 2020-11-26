package cz.kotox.core.rest.interceptror

import cz.kotox.core.CoreConfig
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private const val IMAGE_IN_MESSAGE_DETECTOR = "�"

object LoggingInterceptor {

	fun loggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor(
		object : HttpLoggingInterceptor.Logger {
			override fun log(message: String) {
				if (message.contains(IMAGE_IN_MESSAGE_DETECTOR)) {
					//TODO temporary hack to avoid logging images in multipart
					Timber.w("Skipping logging of message containing character $IMAGE_IN_MESSAGE_DETECTOR")
				} else {
					Timber.d(message)
				}
			}
		}
	).apply {
		level = when (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			true -> HttpLoggingInterceptor.Level.BASIC
			false -> HttpLoggingInterceptor.Level.BODY
		}
	}
}