package cz.kotox.core.arch.di

import android.app.Application
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import cz.kotox.core.arch.converter.DateJsonAdapter
import cz.kotox.core.converter.SimpleDateJsonAdapter
import cz.kotox.core.converter.TimeJsonAdapter
import cz.kotox.core.entity.SimpleDate
import cz.kotox.core.entity.SimpleTime
import com.squareup.moshi.Moshi
import cz.kotox.core.converter.SerializeNulls
import dagger.Module
import dagger.Provides
import java.util.Date
import javax.inject.Named
import javax.inject.Singleton

@Module
object FeatureCoreModule {

	@Provides
	@JvmStatic
	fun provideMoshi(): Moshi {
		return Moshi.Builder()
			.add(SerializeNulls.JSON_ADAPTER_FACTORY)
			.add(Date::class.java, DateJsonAdapter().nullSafe()) //Rfc3339DateJsonAdapter
			.add(SimpleTime::class.java, TimeJsonAdapter().nullSafe())
			.add(SimpleDate::class.java, SimpleDateJsonAdapter().nullSafe())
			.build()
	}

	@Provides
	@JvmStatic
	@Singleton
	@Named("userPreferences")
	fun provideUserSharedPreferences(application: Application): SharedPreferences {
		return PreferenceManager.getDefaultSharedPreferences(application)
	}

	@Provides
	@JvmStatic
	@Singleton
	@Named("installPreferences")
	fun provideInstallSharedPreferences(application: Application): SharedPreferences {
		return application.getSharedPreferences("installation", ContextWrapper.MODE_PRIVATE)
	}
}