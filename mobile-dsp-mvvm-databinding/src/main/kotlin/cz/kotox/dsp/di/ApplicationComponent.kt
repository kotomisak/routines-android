package cz.kotox.dsp.di

import android.app.Application
import cz.kotox.core.entity.AppName
import cz.kotox.core.entity.AppVersion
import cz.kotox.core.media.di.MediaModule
import cz.kotox.core.arch.di.BaseArchModule
import cz.kotox.core.arch.di.activity.SoulvibeActivityLifecycleCallbacks
import cz.kotox.core.dsp.DspEventTracker
import cz.kotox.core.entity.AppId
import cz.kotox.core.rest.RestContract
import cz.kotox.core.rest.di.CoreRestModule
import cz.kotox.dsp.MobileDspApplication
import cz.kotox.dsp.ui.MainActivity
import cz.kotox.dsp.ui.analyzer.AnalyzerActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
	modules = [
		BaseArchModule::class,
		CoreRestModule::class,
		MediaModule::class,
		DspModule::class
	]
)
interface ApplicationComponent  {

	@Component.Builder
	interface Builder {

		@BindsInstance
		fun application(application: Application): Builder

		@BindsInstance
		fun appVersion(appVersion: AppVersion): Builder

		@BindsInstance
		fun appId(appId: AppId): Builder

		@BindsInstance
		fun appName(appName: AppName): Builder


		@BindsInstance
		fun restContract(restContract: RestContract): Builder

		fun build(): ApplicationComponent
	}

	fun inject(mobileSoulvibeApplication: MobileDspApplication)

	fun inject(analyzerActivity: AnalyzerActivity)

	fun inject(mainActivity: MainActivity)

	val activityLifecycleCallbacks: SoulvibeActivityLifecycleCallbacks

	val eventTracker: DspEventTracker

}
