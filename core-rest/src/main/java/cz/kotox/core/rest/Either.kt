package cz.kotox.core.rest

import cz.kotox.core.rest.entity.NoConnectivityException
import cz.kotox.core.rest.entity.RestError
import cz.kotox.core.rest.entity.RestHttpException
import cz.kotox.core.ktools.Either
import cz.kotox.core.ktools.value

suspend fun <V> eitherApi(action: suspend () -> V): Either<RestError, V> =
	try {
		value(action())
	} catch (t: Exception) {
		when (t) {
			is RestHttpException -> error(RestError.ApiError(t.error))
			is NoConnectivityException -> error(RestError.NoConnectivityError)
			else -> error(RestError.NetworkError(t))
		}
	}