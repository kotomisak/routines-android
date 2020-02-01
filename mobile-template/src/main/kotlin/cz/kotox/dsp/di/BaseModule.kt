package cz.kotox.dsp.di

import dagger.Module
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [
	AndroidInjectionModule::class,
	AndroidSupportInjectionModule::class,
	ViewModelModule::class
])
object BaseModule
