package cz.kotox.dsp.app

import android.app.Application
import androidx.navigation.NavController
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppNavigator @Inject constructor(private val application: Application) {
	private var navController: NavController? = null

	fun bind(navController: NavController) {
		this.navController = navController
	}

	fun unbind() {
		navController = null
	}

	/**
	 * 	Navigation from inside the MainActivity
	 * 	TODO ensure this will be not called from different activity!
	 */

	internal fun showOnBoarding() {
		Timber.d(">>>_ BOARD with controller ${navController}")
		//navController?.navigate(MainNavigationDirections.navigateToOnboarding())
	}

	internal fun showHome() {
		//navController?.navigate(MainNavigationDirections.navigateToHome())
	}


//	/**
//	 * 	Navigation from any activity to the root of specific navigation.
//	 */
//
//	fun startPlayer(meditationEntity: MeditationEntity, savedMeditation: Boolean) {
//		// Navigate to different activity must be done like this if you are navigating from different activity than MainActivity, because navController is binded in the MainActivity.
//		// It means that navController is null when the system kills the process and the main activity was not last one.
//		// It's not the best looking solution, but it keeps SaveArgs at least.
//
//		val navDirections = MainNavigationDirections.navigateToPlayer(meditationEntity, savedMeditation)
//
//		ActivityNavigator(application).apply {
//			navigate(
//				createDestination().setIntent(Intent(application, MeditationPlayerActivity::class.java)),
//				navDirections.arguments,
//				null,
//				null)
//		}
//	}



//	fun startEducation(infoTitle: String, infoText: ParcelableSpanned) {
//		// Navigate to different activity must be done like this if you are navigating from different activity than MainActivity, because navController is binded in the MainActivity.
//		// It means that navController is null when the system kills the process and the main activity was not last one.
//		// It's not the best looking solution, but it keeps SaveArgs at least.
//
//		val navDirections = MainNavigationDirections.navigateToEducation(
//			infoTitle = infoTitle,
//			infoText = infoText
//		)
//
//		ActivityNavigator(application).apply {
//			navigate(
//				createDestination().setIntent(Intent(application, EducationActivity::class.java)),
//				navDirections.arguments,
//				null,
//				null)
//		}
//
//	}

//	fun startMeditationShare(
//		entityList: List<MeditationItemEntity>,
//		reusePhoto: Boolean,
//		explicitShareTitle: String?
//	) {
//		// Navigate to different activity must be done like this if you are navigating from different activity than MainActivity, because navController is binded in the MainActivity.
//		// It means that navController is null when the system kills the process and the main activity was not last one.
//		// It's not the best looking solution, but it keeps SaveArgs at least.
//
//		val navDirections = MainNavigationDirections.navigateToMeditationShare(
//			entities = entityList.toTypedArray(),
//			reusePhoto = reusePhoto,
//			explicitShareTitle = explicitShareTitle
//		)
//
//		ActivityNavigator(application).apply {
//			navigate(
//				createDestination().setIntent(Intent(application, ShareActivity::class.java)),
//				navDirections.arguments,
//				null,
//				null)
//		}
//
//	}

}