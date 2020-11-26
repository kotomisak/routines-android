package cz.kotox.core.arch.analytics

import cz.kotox.core.CoreConfig
import timber.log.Timber
import java.util.Calendar

const val PARAMETER_VALUE_LIMIT = 95 //Given by custom parameter value limit 100 on Firebase

abstract class BaseEventTracker(
	private val firebase: FirebaseEventsConsumer
) {

	protected fun getCurrentHourOfTheDay() = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

	protected fun trackScreenView(screenEventName: String) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackSimple(screenEventName)
		}else{
			Timber.d("SCREEN EVENT: ${screenEventName}")
		}
	}

	protected fun trackScreenViewWithParams(screenEventName: String, screenEventParams: EventParams) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackParams(screenEventName, screenEventParams)
		}else{
			Timber.d("SCREEN EVENT: ${screenEventName} with params ${screenEventParams}")
		}
	}

	protected fun trackAction(actionEventName: String) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackSimple(actionEventName)
		}else{
			Timber.d("ACTION EVENT: ${actionEventName}")
		}
	}

	protected fun trackActionWithParams(actionEventName: String, actionEventParams: EventParams) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackParams(actionEventName, actionEventParams)
		}else{
			Timber.d("ACTION EVENT: ${actionEventName} with params ${actionEventParams}")
		}
	}

	@Deprecated("Use trackScreenView or trackAction instead.")
	protected fun trackSimple(eventName: String) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackSimple(eventName)
		}
	}

	@Deprecated("Use trackActionWithParams or trackScreenViewWithParams instead.")
	protected fun trackParams(eventName: String, params: EventParams) {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			firebase.trackParams(eventName, params)
		}
	}
}