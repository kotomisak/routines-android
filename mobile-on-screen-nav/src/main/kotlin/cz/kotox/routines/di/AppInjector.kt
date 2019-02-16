package cz.kotox.routines.di

import android.app.Activity
import android.app.Application
import android.os.Bundle
import cz.kotox.routines.AppOnScreenNavApplication
import cz.kotox.routines.core.di.Injectable
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector

/**
 * Helper class to automatically inject fragments if they implement [Injectable].
 */
object AppInjector {
	fun init(appOnScreenNavApplication: AppOnScreenNavApplication) {

		DaggerAppComponent
			.builder()
			.application(appOnScreenNavApplication)
			.build()
			.inject(appOnScreenNavApplication)

		appOnScreenNavApplication.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
			override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
				handleActivity(activity)
			}

			override fun onActivityStarted(activity: Activity) {

			}

			override fun onActivityResumed(activity: Activity) {

			}

			override fun onActivityPaused(activity: Activity) {

			}

			override fun onActivityStopped(activity: Activity) {

			}

			override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

			}

			override fun onActivityDestroyed(activity: Activity) {

			}
		})
	}

	private fun handleActivity(activity: Activity) {
		if (activity is Injectable) {
			AndroidInjection.inject(activity)
		}
		if (activity is HasSupportFragmentInjector) {
			AndroidInjection.inject(activity)
		}
		(activity as? androidx.fragment.app.FragmentActivity)?.supportFragmentManager?.registerFragmentLifecycleCallbacks(
			object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
				override fun onFragmentPreCreated(fm: androidx.fragment.app.FragmentManager, f: androidx.fragment.app.Fragment, savedInstanceState: Bundle?) {
					if (f is Injectable) {
						AndroidSupportInjection.inject(f)
					}
				}
			}, true)
	}
}