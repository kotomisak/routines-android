package cz.kotox.core.rest

import cz.kotox.core.rest.entity.ApiErrorEntity
import cz.kotox.core.rest.entity.RestHttpException
import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import javax.inject.Inject

class ErrorParserCallAdapterFactory @Inject constructor(private val moshi: Moshi) : CallAdapter.Factory() {

	override fun get(
		returnType: Type,
		annotations: Array<out Annotation>,
		retrofit: retrofit2.Retrofit
	): CallAdapter<*, *>? {

		if (Call::class.java != getRawType(returnType)) {
			return null
		}

		if (returnType !is ParameterizedType) {
			throw IllegalStateException("Return type must be parameterized as Call<T> or the suspend keyword must be used for request methods")
		}

		val responseType = getParameterUpperBound(0, returnType)

		return BodyCallAdapter<Any>(responseType, moshi)
	}

	private class BodyCallAdapter<T>(
		private val responseType: Type,
		private val moshi: Moshi
	) : CallAdapter<T, Call<T>> {

		override fun responseType() = responseType

		override fun adapt(call: Call<T>): Call<T> {
			return ErrorParserCall(call, moshi)
		}
	}

	private class ErrorParserCall<T>(
		val call: Call<T>,
		private val moshi: Moshi
	) : Call<T> by call {

		override fun enqueue(callback: Callback<T>) {
			call.enqueue(object : Callback<T> {
				override fun onFailure(call: Call<T>, error: Throwable) {
					Timber.d("${call.request().toString().substringBefore(", tags=", "")}}: $error")
					callback.onFailure(call, error)
				}

				override fun onResponse(call: Call<T>, response: Response<T>) {
					if (response.isSuccessful) {
						callback.onResponse(call, response)
					} else {
						try {
							val error = moshi.adapter(ApiErrorEntity::class.java).fromJson(response.errorBody()!!.string())!!
							callback.onFailure(call, RestHttpException(error))
						} catch (ex: Exception) {
							Timber.d("${call.request().toString().substringBefore(", tags=", "")}}: $ex")
							callback.onFailure(call, ex)
						}
					}
				}
			})
		}
	}
}