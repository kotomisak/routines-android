package cz.kotox.core.rest.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

sealed class RestError {
	data class ApiError(val apiError: ApiErrorEntity) : RestError()
	object NoConnectivityError : RestError()
	data class NetworkError(val exception: Exception) : RestError()
}

const val ERROR_NOT_FOUND_TYPE = "E_NOT_FOUND"

@JsonClass(generateAdapter = true)
data class ApiErrorEntity(
	@Json(name = "type") val type: String?,
	@Json(name = "message") val message: String?
)

class RestHttpException(val error: ApiErrorEntity) : RuntimeException()
