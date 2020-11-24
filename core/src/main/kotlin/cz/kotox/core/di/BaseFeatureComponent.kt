package cz.kotox.core.di

import dagger.android.DispatchingAndroidInjector

interface BaseFeatureComponent {
	fun activityInjector(): DispatchingAndroidInjector<Any>
}
