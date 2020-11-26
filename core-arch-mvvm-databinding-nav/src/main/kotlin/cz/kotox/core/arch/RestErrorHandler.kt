package cz.kotox.core.arch

import cz.kotox.core.liveevent.Event
import cz.kotox.core.rest.ErrorResolver
import cz.kotox.core.rest.entity.RestError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

interface RestErrorHandler {

	fun showErrorToUserCondition() = true
	fun sendErrorHandlerEvent(event: Event)
	fun getErrorResolver(): ErrorResolver

	suspend fun handleRestError(
		restError: RestError,
		offensiveOffline: Boolean = false,
		offensiveNetworkError: Boolean = false,
		offensiveUnexpectedError: Boolean = true
	) {
		when (restError) {
			is RestError.NoConnectivityError -> {
				if (offensiveOffline) {
					withContext(Dispatchers.Main) {
						sendErrorHandlerEvent(ShowOfflineDialog)
					}
				} else {
					//Don't be offensive, show just snack about being offline.
					withContext(Dispatchers.Main) {
						sendErrorHandlerEvent(ShowError(messageResId = R.string.error_no_internet))
					}
				}
			}
			is RestError.NetworkError -> {
				Timber.w(restError.exception, "Unexpected NetworkError issue!") //E.g. JobCancellationException or corrupted data (non nul fields are null)
				showErrorToUser(restError, offensiveNetworkError)
			}
			else -> {
				Timber.e(IllegalStateException("$restError"))
				showErrorToUser(restError, offensiveUnexpectedError)
			}
		}
	}

	private suspend fun showErrorToUser(restError: RestError, offensiveError: Boolean) {
		if (showErrorToUserCondition() && offensiveError) {
			withContext(Dispatchers.Main) {
				sendErrorHandlerEvent(ShowError(message = getErrorResolver().resolveError(restError)))
			}
		}
	}
}