package cz.kotox.routines.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
object AppCommonModule {
	@Provides
	@JvmStatic
	fun provideAppContext(application: Application): Context = application.applicationContext

	@Provides
	@JvmStatic
	fun provideSharedPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}

@Component(
	modules = [AppCommonModule::class]
)
interface BaseComponent {
	@Component.Builder
	interface Builder {
		@BindsInstance
		fun application(application: Application): Builder

		fun build(): BaseComponent
	}

	fun application(): Application
	fun context(): Context
	fun sharedPreferences(): SharedPreferences
}