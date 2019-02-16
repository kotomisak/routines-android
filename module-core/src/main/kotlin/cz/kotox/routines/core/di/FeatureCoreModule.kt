package cz.kotox.routines.core.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import cz.kotox.routines.core.FeatureCore
import cz.kotox.routines.core.PreferencesCore
import cz.kotox.routines.core.entity.AppVersion
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
open class FeatureCoreModule {
	@Provides
	@Singleton
	open fun provideAppContext(application: Application): Context {
		return application.applicationContext
	}

	@Provides
	@Singleton
	fun provideSharedPreferences(context: Context): SharedPreferences {
		return PreferenceManager.getDefaultSharedPreferences(context)
	}

	@Provides
	@Singleton
	fun provideCorePreferences(context: Context, sharedPreferences: SharedPreferences): PreferencesCore {
		return PreferencesCore(context, sharedPreferences)
	}

	@Provides
	@Singleton
	fun provideAppVersion(): AppVersion {
		return AppVersion(FeatureCore.getVersionCode(), FeatureCore.getVersionName())
	}

}