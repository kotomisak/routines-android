package cz.kotox.routines.core.di

import dagger.android.DispatchingAndroidInjector

interface BaseFeatureComponent {
	fun activityInjector(): DispatchingAndroidInjector<Any>
}
