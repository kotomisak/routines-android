package cz.kotox.core.rest

import android.app.Application
import cz.kotox.core.rest.entity.RestError

abstract class ErrorResolver(
	private val application: Application
) {
	open fun resolveError(error: RestError): String =
		when (error) {
			is RestError.NetworkError -> application.getString(R.string.error_general)
			is RestError.NoConnectivityError -> application.getString(R.string.error_no_internet)
			else -> application.getString(R.string.error_general)
		}
}