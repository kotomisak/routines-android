package cz.kotox.core.logging.timber

import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Locale

/** A tree which logs important information for crash reporting.  */
class CrashReportingTree : Timber.Tree() {

	override fun formatMessage(message: String, args: Array<Any>): String {
		return String.format(Locale.US, message, *args)
	}

	override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
		//val priorityString = CommonUtils.logPriorityToString(priority)
		//val messageWithPrefix = "| $priorityString/$tag: $message"

		//Crashlytics.log(messageWithPrefix)

		if (throwable != null) {
			//logCrashlyticsError(throwable)
		}
	}

	/**
	 * Do not report some of the errors as non-fatal but only log them
	 */
	private fun logCrashlyticsError(error: Throwable) {
		if (error.isIgnored || error.cause.isIgnored) {
			//Crashlytics.log(error.message)
		} else {
			//Crashlytics.logException(error)
		}
	}

	private val Throwable?.isIgnored
		get() = when (this) {
			is ConnectException,
			is SocketTimeoutException,
			is UnknownHostException -> true
//			is RestHttpException,
//			is NetworkNotAvailableException -> true
			else -> false
		}
}