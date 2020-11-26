package cz.kotox.core.database

import cz.kotox.core.ktools.UserSharedPreferencesProvider
import cz.kotox.core.ktools.boolean
import cz.kotox.core.ktools.long
import cz.kotox.core.ktools.map
import cz.kotox.core.ktools.string
import cz.kotox.core.ktools.stringLiveData
import cz.kotox.core.ktools.stringSet
import cz.kotox.core.utility.AndroidAppUUID
import timber.log.Timber
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

private const val DEVICE_TOKEN = "deviceToken"
private const val DEVICE_TOKEN_FCM = "fcmDeviceToken"
private const val ACCESS_TOKEN = "accessToken"
private const val REFRESH_TOKEN = "refreshToken"
private const val USER_ID = "userId"
private const val USER_EMAIL = "userEmail"
private const val USER_FIRST_NAME = "userFirstName"
private const val USER_LAST_NAME = "userLastName"
private const val MEDITATIONS_LAST_UPDATE = "meditationsLastUpdate"

private const val ONBOARDING_DISPLAYED = "onboardingDisplayed"
private const val POPUP_INFO_ACCEPTED = "popupInfoAccepted"

private const val REMINDER_POPUP_COUNTER = "reminderPopupCounter"

const val TIME_NEVER = 0L

enum class StartTimeType(val key: String) {
	DASHBOARD("start_time_dashboard")
}

@Singleton
class AppPreferences @Inject constructor(
	private val provider: UserSharedPreferencesProvider,
	private val uuid: AndroidAppUUID
) {
	// Onboarding
	var onboardingDisplayed by provider.boolean(false, ONBOARDING_DISPLAYED)
		private set

	// Popup Info
	var popupInfoAccepted by provider.boolean(false, POPUP_INFO_ACCEPTED)
		private set

	// Auth
	private val refreshTokenLD by provider.stringLiveData(null, REFRESH_TOKEN)
	val userLoggedIn get() = refreshToken != null
	val userLoggedInLD = refreshTokenLD.map { it != null }

	var accessToken by provider.string(null, ACCESS_TOKEN)
		private set
	var refreshToken by provider.string(null, REFRESH_TOKEN)
		private set
	var userId by provider.long(Long.MIN_VALUE, USER_ID)
		private set
	var userEmail by provider.string(null, USER_EMAIL)
		private set
	var userFirstName by provider.string(null, USER_FIRST_NAME)
		private set
	var userLastName by provider.string(null, USER_LAST_NAME)
		private set
	var deviceTokenInternal by provider.string(null, DEVICE_TOKEN)
		private set
	var deviceTokenRegisteredByFcm by provider.string(null, DEVICE_TOKEN_FCM)
		private set

	private val userFirstNameInternal by provider.stringLiveData(null, USER_FIRST_NAME)
	val userFirstNameLD = userFirstNameInternal.map { it }

	private val userLastNameInternal by provider.stringLiveData(null, USER_LAST_NAME)
	val userLastNameLD = userLastNameInternal.map { it }

	private val userEmailInternal by provider.stringLiveData(null, USER_EMAIL)
	val userEmailLD = userEmailInternal.map { it }

	// Meditations
	private var meditationsLastUpdateTimestamp by provider.long(0, MEDITATIONS_LAST_UPDATE)
	var meditationsLastUpdate: Date?
		get() = if (meditationsLastUpdateTimestamp == 0L) null else Date(meditationsLastUpdateTimestamp)
		set(value) {
			meditationsLastUpdateTimestamp = value?.time ?: 0
		}

	// Permissions
	private var requestedPermissionSet: Set<String>? by provider.stringSet()

	// Time
	private var timeDashboardFirstStart by provider.long(TIME_NEVER, StartTimeType.DASHBOARD.key)


	//Reminder
	var reminderPopupCounter by provider.long(0, REMINDER_POPUP_COUNTER)
		private set

	fun accessTokenExpired() {
		accessToken = null
	}

	fun newAccessToken(accessToken: String) {
		this.accessToken = accessToken
	}

	fun refreshTokenExpired() {
		refreshToken = null
	}

	fun userAuthenticated(
		accessToken: String,
		refreshToken: String,
		userId: Long,
		userEmail: String?,
		firstName: String?,
		lastName: String?
	) {
		this.accessToken = accessToken
		this.refreshToken = refreshToken
		this.userId = userId
		this.userEmail = userEmail
		this.userFirstName = firstName
		this.userLastName = lastName
	}

	fun userNameChanged(
		firstName: String?,
		lastName: String?
	) {
		this.userFirstName = firstName
		this.userLastName = lastName
	}

	fun singOutUser() {
		provider.clear()
		/**
		 * https://stackoverflow.com/a/24612025
		 * Since provider.clear() doesn't currently invoke onSharedPreferenceChanged(),
		 * update proper liveData here to notify listeners:
		 */
		Timber.d(">>>_ expect refreshToken is cleared... ${refreshToken}")
		this.refreshTokenLD.postValue(refreshToken)//Trigger update of userLoggedInLD
		this.userFirstNameInternal.postValue(userFirstName)//Trigger update of userFirstNameLD
		this.userLastNameInternal.postValue(userLastName)//Trigger update of userLastNameLD
		this.userEmailInternal.postValue(userEmail)//Trigger update of userEmailLD

		onboardingDisplayed = true
	}

	fun userUpdated(
		firstName: String?,
		lastName: String?
	) {
		firstName?.let { this.userFirstName = it }
		lastName?.let { this.userLastName = it }
	}

	fun onboardingDisplayed() {
		onboardingDisplayed = true
	}

	fun clearOnboardingDisplayed() {
		onboardingDisplayed = false
	}

	fun getDeviceToken(): String {
		val token = deviceTokenInternal
		return if (token == null) {
			val newToken = uuid.createAppImmutableUniqueId()
			Timber.d(">>>_ device token NEW $newToken")
			deviceTokenInternal = newToken
			newToken
		} else {
			Timber.d(">>>_ device token REUSE $token")
			token
		}
	}

	fun isFcmRegisteredWithLatestDeviceToken(): Boolean = getDeviceToken() == deviceTokenRegisteredByFcm

	fun setFcmTokenRegistered() {
		deviceTokenRegisteredByFcm = getDeviceToken()
	}

	fun setStartTime(timeType: StartTimeType) {
		when (timeType) {
			StartTimeType.DASHBOARD -> {
				if (this.timeDashboardFirstStart == TIME_NEVER) {
					this.timeDashboardFirstStart = Date().time
				}
			}
		}
	}

	fun getStartTimeInMillis(timeType: StartTimeType): Long =
		when (timeType) {
			StartTimeType.DASHBOARD -> {
				if (this.timeDashboardFirstStart > 0) {
					Date().time - this.timeDashboardFirstStart
				} else 0L
			}
		}

	fun acceptInfoPopup() {
		popupInfoAccepted = true
	}

	fun markPermissionRequested(permission: String) {
		requestedPermissionSet = requestedPermissionSet?.plus(permission) ?: setOf(permission)
	}

	fun permissionAccepted(permission: String) {
		requestedPermissionSet = requestedPermissionSet?.minus(permission) ?: emptySet()
	}

	fun wasPermissionRequested(permission: String): Boolean = requestedPermissionSet?.contains(permission) == true

	//Reminder
	fun incrementReminderCounter() {
		reminderPopupCounter++
	}
}


