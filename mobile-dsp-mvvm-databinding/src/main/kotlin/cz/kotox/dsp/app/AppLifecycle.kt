package cz.kotox.dsp.app

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import cz.kotox.core.arch.di.activity.SoulvibeActivityLifecycleCallbacks
import timber.log.Timber

class AppLifecycle(
		private val activityLifecycleCallbacks: SoulvibeActivityLifecycleCallbacks,
		//private val eventTracker: AppEventTracker
) : LifecycleObserver {

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	fun onForeground() {
		Timber.d("${activityLifecycleCallbacks.topScreenName}")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	fun onBackground() {
		Timber.d("${activityLifecycleCallbacks.topScreenName}")

		//Fixme implement event tracker here
		//eventTracker.appBackground(activityLifecycleCallbacks.topScreenName ?: "Unknown")
	}
}