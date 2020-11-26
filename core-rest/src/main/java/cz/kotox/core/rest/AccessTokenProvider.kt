package cz.kotox.core.rest

import cz.kotox.core.ktools.Either
import cz.kotox.core.rest.entity.RestError

interface AccessTokenProvider {
	suspend fun refreshAccessToken(): Either<RestError, Unit>
}