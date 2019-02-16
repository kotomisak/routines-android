package cz.kotox.routines.di

import cz.kotox.routines.ui.MainActivity
import cz.kotox.routines.ui.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AppOnScreenNavDaggerModule {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivityTroubleMaker(): MainActivity

	@ContributesAndroidInjector
	abstract fun contributeCreateMainFragment(): MainFragment
}