package cz.kotox.core.arch.di.activity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
//import com.google.android.libraries.places.widget.AutocompleteActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoulvibeActivityLifecycleCallbacks @Inject constructor(
	private val customFragmentFactory: FragmentFactory
) : EmptyActivityLifecycleCallbacks {

	var topScreenName: String? = null
		private set

	/**
	 * Here might be an issue with this registration when use without navigation components.
	 * onActivityCreated might be too late to register fragments, therefore in case of any issue use BaseActivity to register proper fragments.
	 * Do NOT use onActivityPreCreated, since this is not called on old Android versions (e.g. API21)
	 */
	override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

		//Fixme avoid activities like AutocompleteActivity but without dependency on them - by name?
		//if (activity is AutocompleteActivity) return

		if (activity is FragmentActivity) {
			Timber.d(">>>_activity: ${activity}")
			//Set InjectFragmentFactory to every activity to use all fragments defined by FragmentKey
			activity.supportFragmentManager.apply {
				fragmentFactory = customFragmentFactory

				registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
					override fun onFragmentResumed(fm: FragmentManager, fragment: Fragment) {
						super.onFragmentResumed(fm, fragment)
						if (fragment !is NavHostFragment) {
							topScreenName = fragment.javaClass.simpleName
						}
					}
				}, true)
			}
		}
	}
}