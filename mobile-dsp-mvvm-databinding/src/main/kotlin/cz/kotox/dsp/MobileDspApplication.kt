package cz.kotox.dsp

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import cz.kotox.core.logging.timber.CrashReportingTree
import cz.kotox.core.logging.timber.LineNumberDebugTree
import cz.kotox.core.CoreConfig
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.database.InstallPreferences
import cz.kotox.core.entity.AppId
import cz.kotox.core.entity.AppName
import cz.kotox.core.entity.AppVersion
import cz.kotox.core.rest.RestContract
import cz.kotox.dsp.app.AppLifecycle
import cz.kotox.dsp.di.AppComponentProvider
import cz.kotox.dsp.di.DaggerApplicationComponent
import timber.log.Timber
import javax.inject.Inject

class MobileDspApplication : Application(),
		AppComponentProvider,
		RestContract {

	@Inject
	lateinit var appPreferences: AppPreferences

	@Inject
	lateinit var installPreferences: InstallPreferences

	@Inject
	lateinit var appInitializer: AppInitializer

	override val component by lazy {
		DaggerApplicationComponent
				.builder()
				.application(this)
				.appVersion(AppVersion(BuildConfig.DSP_VERSION_CODE, BuildConfig.DSP_VERSION_NAME))
				.appId(AppId(BuildConfig.APPLICATION_ID))
				.appName(AppName(getString(R.string.app_name)))
				.restContract(this)
				.build()
	}

	override fun onCreate() {
		super.onCreate()

		component.inject(this)

		initTimber()

		installPreferences.setAppStartTime()

		appInitializer.init()

		registerActivityLifecycleCallbacks(component.activityLifecycleCallbacks)
		ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycle(component.activityLifecycleCallbacks/*, component.eventTracker*/))


	}

	override fun sessionExpired() {
		//userRepository.singOut()
	}

	@Suppress("ConstantConditionIf")
	private fun initTimber() {
		if (CoreConfig.IS_RELEASE_BUILD_TYPE) {
			// Report crashes for productionRelease
			Timber.plant(CrashReportingTree())

			if (!CoreConfig.IS_PRODUCTION_FLAVOR) {
				// Log to console for all the flavors except Production
				Timber.plant(LineNumberDebugTree())
			}
		} else {
			// Log to console for debug builds
			Timber.plant(LineNumberDebugTree())
		}
	}

}

