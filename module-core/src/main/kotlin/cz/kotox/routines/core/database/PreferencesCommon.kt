package cz.kotox.routines.core.database

import android.annotation.SuppressLint
import android.content.SharedPreferences
import cz.kotox.routines.core.OpenForMocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OpenForMocking
class PreferencesCommon @Inject constructor(
	//val context: Context,
	private val sharedPreferences: SharedPreferences
) {
	companion object {
		private const val PREFS_ID_TOKEN = "prefs_id_token"
		private const val PREFS_ACCESS_TOKEN = "prefs_access_token"
		private const val PREFS_REFRESH_TOKEN = "prefs_refresh_token"
		private const val PREFS_USER_ID_TOKEN = "prefs_user_id_token"
		private const val PREFS_LAST_SIGNED_USER_ID_TOKEN = "prefs_last_signed_user_id_token"
		private const val PREFS_DISMISSED_BANNERS = "prefs_dismissed_banners"
		private const val PREFS_ONBOARDED = "prefs_onboarded"
		private const val PREFS_USER_TUTORIAL_PREFIX = "prefs_tutorial_"

		private const val ID_TOKEN_DEFAULT_VALUE = -1L
	}

	/**
	 * Is called via [LogoutAction]
	 */
	fun clearForSignOut() {
		clearUserId()
		clearTokens()
	}

	var userId: Long
		get() = sharedPreferences.getLong(PREFS_USER_ID_TOKEN, ID_TOKEN_DEFAULT_VALUE)
		set(userId) {
			sharedPreferences.edit().putLong(PREFS_USER_ID_TOKEN, userId).apply()
		}

	var isPreLoginOnboardingCompleted: Boolean
		get() = sharedPreferences.getBoolean(PREFS_ONBOARDED, false)
		set(value) {
			sharedPreferences.edit().putBoolean(PREFS_ONBOARDED, value).apply()
		}

	/**
	 * TODO should be in realm, but for now UserLoginInfo sometimes is not fetched on user log in
	 */
	var isPostLoginTutorialCompleted: Boolean
		get() = sharedPreferences.getBoolean(PREFS_USER_TUTORIAL_PREFIX + userId, false)
		set(value) {
			sharedPreferences.edit().putBoolean(PREFS_USER_TUTORIAL_PREFIX + userId, value).apply()
		}

	fun clearUserId() {
		sharedPreferences.edit().putLong(PREFS_USER_ID_TOKEN, ID_TOKEN_DEFAULT_VALUE).apply()
	}

	var idToken: String?
		get() = sharedPreferences.getString(PREFS_ID_TOKEN, null)
		@SuppressLint("ApplySharedPref")
		set(value) {
			sharedPreferences.edit().putString(PREFS_ID_TOKEN, value).commit()
		}

	var accessToken: String?
		get() = sharedPreferences.getString(PREFS_ACCESS_TOKEN, null)
		@SuppressLint("ApplySharedPref")
		set(value) {
			sharedPreferences.edit().putString(PREFS_ACCESS_TOKEN, value).commit()
		}

	val refreshToken: String?
		get() = sharedPreferences.getString(PREFS_REFRESH_TOKEN, null)

	/**
	 * Checks for [userId] and [accessToken]
	 */
	fun isUserLoggedIn(): Boolean = userId > 0 && accessToken != null

	@SuppressLint("ApplySharedPref")
	fun setTokensSynchronously(idToken: String, accessToken: String, refreshToken: String) {
		sharedPreferences.edit()
			.putString(PREFS_ID_TOKEN, idToken)
			.putString(PREFS_ACCESS_TOKEN, accessToken)
			.putString(PREFS_REFRESH_TOKEN, refreshToken)
			.commit()
	}

	fun clearTokens() {
		sharedPreferences.edit()
			.putString(PREFS_ID_TOKEN, null)
			.putString(PREFS_ACCESS_TOKEN, null)
			.putString(PREFS_REFRESH_TOKEN, null)
			.apply()
	}

	var lastSignedUserId: Long?
		get() = sharedPreferences.getLong(PREFS_LAST_SIGNED_USER_ID_TOKEN, -1).let { if (it == -1L) null else it }
		set(lastSignedUserId) {
			sharedPreferences.edit().putLong(PREFS_LAST_SIGNED_USER_ID_TOKEN, lastSignedUserId
				?: -1L).apply()
		}

	val currentUserDifferentThanLast: Boolean
		get() = lastSignedUserId != null && lastSignedUserId != userId

	val dismissedBannerIds: Set<String>
		get() = sharedPreferences.getStringSet(PREFS_DISMISSED_BANNERS, setOf()) ?: setOf()

	fun dismissBanner(bannerId: String) {
		val newBannerIds = dismissedBannerIds + bannerId

		sharedPreferences
			.edit()
			.putStringSet(PREFS_DISMISSED_BANNERS, newBannerIds)
			.apply()
	}
}
