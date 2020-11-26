package cz.kotox.core.logging.timber

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CommonUtils
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Locale

private const val CALL_STACK_INDEX = 5

/** A tree which logs important information for crash reporting.  */
class CrashReportingTree : Timber.Tree() {

	override fun formatMessage(message: String, args: Array<Any>): String {
		return String.format(Locale.US, message, *args)
	}

	override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
		val stackTrace = Throwable().stackTrace
		val customTag =  if(stackTrace.size > CALL_STACK_INDEX) {
			val element = stackTrace[CALL_STACK_INDEX]

			"Soulvibe:(${element.fileName}:${element.lineNumber})#${element.methodName}"
		} else {
			tag
		}

		val priorityString = CommonUtils.logPriorityToString(priority)
		val messageWithPrefix = "| $priorityString/$customTag: $message"

		FirebaseCrashlytics.getInstance().log(messageWithPrefix)

		if (throwable != null) {
			logCrashlyticsError(throwable)
		}
	}

	/**
	 * Do not report some of the errors as non-fatal but only log them
	 */
	private fun logCrashlyticsError(error: Throwable) {
		if (error.isIgnored || error.cause.isIgnored) {
			error.message?.let { FirebaseCrashlytics.getInstance().log(it) }
		} else {
			FirebaseCrashlytics.getInstance().recordException(error)
		}
	}

	private val Throwable?.isIgnored
		get() = when (this) {
			is ConnectException,
			is SocketTimeoutException,
			is UnknownHostException -> true
			else -> false
		}
}