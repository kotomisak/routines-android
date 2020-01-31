package cz.kotox.template.di

import androidx.lifecycle.ViewModel
import cz.kotox.core.di.ViewModelKey
import cz.kotox.template.ui.MainActivity
import cz.kotox.template.ui.MainFragment
import cz.kotox.template.ui.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MobileTemplateDaggerModule {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivity(): MainActivity

	@ContributesAndroidInjector
	abstract fun contributeMainFragment(): MainFragment

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun bindSettingsViewModel(settingsViewModel: MainViewModel): ViewModel

}