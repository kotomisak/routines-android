package cz.kotox.core.rest.interceptror

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import cz.kotox.core.rest.entity.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectivityInterceptor @Inject constructor(private val application: Application) : Interceptor {

	override fun intercept(chain: Interceptor.Chain): Response {
		if (!isOnline()) throw NoConnectivityException()
		val builder = chain.request().newBuilder()
		return chain.proceed(builder.build())
	}

	private fun isOnline(): Boolean {
		(application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				val activeNetwork = activeNetwork ?: return false
				val networkCapabilities = getNetworkCapabilities(activeNetwork) ?: return false
				return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			} else {
				@Suppress("DEPRECATION")
				return activeNetworkInfo?.isConnected ?: false
			}
		}
	}
}