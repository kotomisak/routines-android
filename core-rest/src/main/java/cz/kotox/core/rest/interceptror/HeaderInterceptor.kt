package cz.kotox.core.rest.interceptror

import cz.kotox.core.database.AppPreferences
import cz.kotox.core.entity.AppVersion
import cz.kotox.core.utility.getDeviceAndOsVersionString
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

const val AUTHORIZATION_HEADER = "Authorization"
private const val ACCEPT_CHARSET_HEADER = "Accept-Charset"
private const val CONTENT_TYPE_HEADER = "Content-Type"
private const val USER_AGENT_HEADER = "User-Agent"

@Singleton
open class HeaderInterceptor @Inject constructor(private val appVersion: AppVersion, private val preferences: AppPreferences) : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		val request = buildNewRequest(chain)
		return chain.proceed(request)
	}

	private fun buildNewRequest(chain: Interceptor.Chain): Request = chain.request()
		.newBuilder()
		.addHeader(ACCEPT_CHARSET_HEADER, "utf-8")
		.addHeader(CONTENT_TYPE_HEADER, "application/json")
		.addHeader(USER_AGENT_HEADER, "build:${appVersion.versionName}(${appVersion.versionCode}); ${getDeviceAndOsVersionString()}")
		.apply {
			if (preferences.accessToken != null) {
				addHeader(AUTHORIZATION_HEADER, "Bearer ${preferences.accessToken}")
			}
		}
		.build()
}