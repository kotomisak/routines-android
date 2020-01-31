package cz.kotox.routines

import android.content.Context
import cz.kotox.routines.core.OpenForMocking
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
