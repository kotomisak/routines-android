package cz.kotox.core.di

//@Module
//open class FeatureCoreModule {
//	@Provides
//	@Singleton
//	open fun provideAppContext(application: Application): Context {
//		return application.applicationContext
//	}
//
//	@Provides
//	@Singleton
//	fun provideSharedPreferences(context: Context): SharedPreferences {
//		return PreferenceManager.getDefaultSharedPreferences(context)
//	}
//
//	@Provides
//	@Singleton
//	fun provideCorePreferences(context: Context, sharedPreferences: SharedPreferences): PreferencesCore {
//		return PreferencesCore(context, sharedPreferences)
//	}
//
//	@Provides
//	@Singleton
//	fun provideAppVersion(): AppVersion {
//		return AppVersion(FeatureCore.getVersionCode(), FeatureCore.getVersionName())
//	}
//
//}