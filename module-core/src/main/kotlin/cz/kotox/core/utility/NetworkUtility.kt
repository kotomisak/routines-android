package cz.kotox.core.utility

import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.NonNull


fun isOnline(@NonNull context: Context): Boolean {
	val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	val networkInfo = connectivityManager.activeNetworkInfo
	return networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected
}