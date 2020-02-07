package cz.kotox.dsp

import androidx.annotation.MainThread
import androidx.annotation.VisibleForTesting
import cz.kotox.core.CoreConfig
import cz.kotox.core.arch.BaseApplication
import cz.kotox.core.database.PreferencesCommon
import cz.kotox.core.entity.AppVersion
import cz.kotox.core.logging.timber.CrashReportingTree
import cz.kotox.core.utility.lazyUnsafe
import cz.kotox.dsp.di.DaggerAppComponent
import cz.kotox.dsp.di.UserComponent
import dagger.android.AndroidInjector
import timber.log.Timber
import javax.inject.Inject

class MobileDspApplication : BaseApplication() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	@Inject
	lateinit var appInitializer: AppInitializer

	override var userComponent: UserComponent? = null

//	@VisibleForTesting
//	internal val analyticsComponent: AnalyticsComponent by lazyUnsafe {
//		DaggerAnalyticsComponent
//			.builder()
//			.application(this)
//			.build()
//	}

	@VisibleForTesting
	internal val appComponent by lazyUnsafe {
		DaggerAppComponent
			.builder()
			.baseComponent(baseComponent)
			//.analyticsComponent(analyticsComponent)
			//.filesAuthority(BuildConfig.FILES_AUTHORITY)
			.applicationInterface(this)
			.appVersion(AppVersion(BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME))
			.build()
	}

	override fun onCreate() {
		super.onCreate()

//		initFabric()
		initTimber()

		appComponent.inject(this)

		// run all init actions from all modules
		appInitializer.init()

		if (isUserLoggedIn()) {
			onUserLoggedIn()
		}

//		registerActivityLifecycleCallbacks(object : TrackingActivityLifecycleCallbacks(crashlyticsTracker) {
//			override fun onActivityCreated(a: Activity, savedInstanceState: Bundle?) {
//				super.onActivityCreated(a, savedInstanceState)
//				foregroundRedirectManager.handleActivityOnCreate(a)
//			}
//		})
	}

//	override fun navigateHome() {
//		val homePendingIntent = NavDeepLinkBuilder(this)
//			.setGraph(R.navigation.mobile_navigation)
//			.setDestination(R.id.launcher_home)
//			.createPendingIntent()
//		homePendingIntent.send()
//	}

	@MainThread
	override fun redirectToLogin(authMessage: String?) {
		logoutCleanup()
		//TODO

//		val homePendingIntent = NavDeepLinkBuilder(this)
//			.setGraph(R.navigation.mobile_navigation)
//			.setDestination(R.id.launcher_home)
//			.createPendingIntent()
//		homePendingIntent.send()
	}

//	override fun provideApplication(): Application = this
//
//	override fun getVersionCode() = BuildConfig.VERSION_CODE
//
//	override fun getVersionName() = BuildConfig.VERSION_NAME

	override fun androidInjector(): AndroidInjector<Any> = AndroidInjector {
		userComponent?.activityInjector()?.maybeInject(it) ?: false || dispatchingAndroidInjector.maybeInject(it)
	}

	override fun onUserLoggedIn() {
		preferencesCommon.isPreLoginOnboardingCompleted = true
		userComponent = appComponent.userComponentBuilder().build()
		userComponent?.userInitializer()?.onUserLoggedIn()
		Timber.d("User ${preferencesCommon.userId} is logged in")
		//setAnalyticsLoggingDetails()

		// WARNING: must be after init actions, because it contains actions based on this
		preferencesCommon.lastSignedUserId = preferencesCommon.userId
	}

	/**
	 * Must be run from UI thread to avoid Realm crashes.
	 */
	@MainThread
	@VisibleForTesting
	internal fun logoutCleanup() {
		// WARNING subcomponent contains also its parent multibindings
		userComponent?.userInitializer()?.onUserLoggedOut()
		userComponent = null
	}

	@VisibleForTesting
	internal fun isUserLoggedIn(): Boolean = preferencesCommon.isUserLoggedIn()

	@Suppress("ConstantConditionIf")
	private fun initTimber() {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			// Report crashes for productionRelease
			if (CoreConfig.IS_PRODUCTION_FLAVOR_TYPE) {
				Timber.plant(CrashReportingTree())
			} else {
				// Log to console and report crashes for other release builds
				Timber.plant(Timber.DebugTree())
				Timber.plant(CrashReportingTree())
			}
		} else {
			// Log to console for debug builds
			Timber.plant(Timber.DebugTree())
		}
	}
}