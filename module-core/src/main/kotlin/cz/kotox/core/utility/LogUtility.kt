package cz.kotox.core.utility

import timber.log.Timber

/**
 * Use this method when you want to override default Timber tag.
 * (e.g. Activity lifecycle - logging from abstract class but using the name of subclass)
 */
@Suppress("NOTHING_TO_INLINE")
inline fun logWithTag(tag: String, message: String) {
	Timber.tag(tag)
	Timber.v(message)
}

/**
 * Use this as a convenience method to log a message as an exception.
 * It also sends the exception to Crashlytics.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun logException(message: String) {
	Timber.e(Exception(message))
}
