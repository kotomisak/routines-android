package cz.kotox.dsp

import android.content.Context
import cz.kotox.core.OpenForMocking
import javax.inject.Inject

@OpenForMocking
class UserInitializer @Inject constructor(
	private val applicationContext: Context
) {
	fun onUserLoggedIn() {
		//do init
	}

	fun onUserLoggedOut() {
		clearBase()
		//cancel user related operations
		//close private db
	}

	internal fun clearBase() {
		//preferencesCommon.clearForSignOut()
	}
}
