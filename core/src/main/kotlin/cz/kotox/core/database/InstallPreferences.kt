package cz.kotox.core.database

import cz.kotox.core.ktools.InstallationSharedPreferencesProvider
import cz.kotox.core.ktools.boolean
import cz.kotox.core.ktools.booleanLiveData
import cz.kotox.core.ktools.long
import cz.kotox.core.ktools.string
import cz.kotox.core.ktools.stringLiveData
import cz.kotox.core.ktools.stringSet
import cz.kotox.core.ktools.stringSetLiveData
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_START_TIME = "start_time_app"
private const val REFERRAL_CODE = "referralCode"

private const val PLAYBACK_GUIDED_MEDITATION = "playbackGuidedMeditation"
private const val INSTRUMENT_SURPRISE = "instrumentSurprise"
private const val INSTRUMENT_SET = "instrumentSet"

private const val LOCATION_REQUESTED_AUTOMATICALLY = "locationRequestedAutomatically"
private const val SHARE_PHOTO_URL = "sharePhotoUrl"
private const val SHARE_ANIMATION_COUNTER = "shareAnimationCounter"
private const val GOALS_POPUP_COUNTER = "goalsPopupCounter"

const val SHARE_ANIMATION_MAX_VIEW_COUNT = 3L

@Singleton
class InstallPreferences @Inject constructor(
	private val provider: InstallationSharedPreferencesProvider
) {

	// Time
	private var timeAppFirstStart by provider.long(TIME_NEVER, APP_START_TIME)

	// Referrals
	var referralCode by provider.string(null, REFERRAL_CODE)
		private set

	val referralCodeLD by provider.stringLiveData(null, REFERRAL_CODE)

	// Meditations - Playback
	var guidedMeditationEnabled by provider.boolean(true, PLAYBACK_GUIDED_MEDITATION)
		private set
	val guidedMeditationEnabledLD by provider.booleanLiveData(true, PLAYBACK_GUIDED_MEDITATION)

	var additionalInstrumentSurpriseEnabled by provider.boolean(true, INSTRUMENT_SURPRISE)
		private set
	val additionalInstrumentSurpriseEnabledLD by provider.booleanLiveData(true, INSTRUMENT_SURPRISE)

	private var additionalInstrumentSet: Set<String>? by provider.stringSet(def = emptySet(), key = INSTRUMENT_SET)

	val additionalInstrumentSetLD by provider.stringSetLiveData(def = emptySet(), key = INSTRUMENT_SET)

	// Location
	private var locationPermissionRequestedAutomatically by provider.boolean(false, LOCATION_REQUESTED_AUTOMATICALLY)

	//Share
	var sharePhotoUrl by provider.string(null, SHARE_PHOTO_URL)
		private set

	var shareAnimationCounter by provider.long(0, SHARE_ANIMATION_COUNTER)
		private set

	//Goals
	var goalsPopupCounter by provider.long(0, GOALS_POPUP_COUNTER)
		private set

	// Time
	fun setAppStartTime() {
		if (this.timeAppFirstStart == TIME_NEVER) {
			this.timeAppFirstStart = Date().time
		}
	}

	fun getAppStartTimeInMillis(timeType: StartTimeType): Long =
		if (this.timeAppFirstStart > 0) {
			Date().time - this.timeAppFirstStart
		} else 0L

	// Referrals
	fun setReferralCodeApplied(appliedReferralCode: String) {
		referralCode = appliedReferralCode
	}

	// Meditations - Playback
	fun setupGuideMeditationSettings(enabled: Boolean) {
		guidedMeditationEnabled = enabled
	}

	fun enableInstrumentSurprise() {
		additionalInstrumentSurpriseEnabled = true
	}

	fun disableInstrumentSurprise() {
		additionalInstrumentSurpriseEnabled = false
	}

	fun addAdditionalInstrument(instrument: String) {
		additionalInstrumentSet = additionalInstrumentSet?.plus(instrument) ?: setOf(instrument)
	}

	fun removeAdditionalInstrument(instrument: String) {
		additionalInstrumentSet = additionalInstrumentSet?.minus(instrument) ?: emptySet()
	}

	fun additionalInstruments(): List<String> = additionalInstrumentSet?.toList() ?: emptyList()

	fun isAdditionalInstrument(instrument: String): Boolean = additionalInstrumentSet?.contains(instrument) == true

	// Location
	fun isLocationAlreadyRequestedAutomatically(): Boolean = locationPermissionRequestedAutomatically

	fun setLocationAlreadyRequestedAutomatically() {
		locationPermissionRequestedAutomatically = true
	}

	// Share
	fun setPhotoShareUrl(photoUrl: String?) {
		sharePhotoUrl = photoUrl
	}

	fun incrementAnimationCounter() {
		if (shareAnimationCounter >= 0) {
			shareAnimationCounter++
		}
	}

	fun deactivateAnimationCounter() {
		shareAnimationCounter = SHARE_ANIMATION_MAX_VIEW_COUNT + 1
	}

	//Goals
	fun incrementGoalsCounter() {
		goalsPopupCounter++
	}

}


