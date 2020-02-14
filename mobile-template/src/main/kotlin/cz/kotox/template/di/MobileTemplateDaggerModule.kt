package cz.kotox.template.di

import androidx.lifecycle.ViewModel
import cz.kotox.core.di.ViewModelKey
import cz.kotox.template.ui.MainActivity
import cz.kotox.template.ui.MainFragment
import cz.kotox.template.ui.MainViewModel
import cz.kotox.template.ui.wizard.WizardActivity
import cz.kotox.template.ui.wizard.WizardViewModel
import cz.kotox.template.ui.wizard.first.WizardFirstFragment
import cz.kotox.template.ui.wizard.first.WizardFirstViewModel
import cz.kotox.template.ui.wizard.second.AnalyzerResultListFragment
import cz.kotox.template.ui.wizard.second.WizardSecondFragment
import cz.kotox.template.ui.wizard.second.WizardSecondListViewModel
import cz.kotox.template.ui.wizard.second.WizardSecondPlayerFragment
import cz.kotox.template.ui.wizard.second.WizardSecondPlayerViewModel
import cz.kotox.template.ui.wizard.second.WizardSecondViewModel
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
	abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

	@ContributesAndroidInjector()
	abstract fun contributeWizardActivity(): WizardActivity

	@Binds
	@IntoMap
	@ViewModelKey(WizardViewModel::class)
	abstract fun bindWizardViewModel(wizardViewModel: WizardViewModel): ViewModel

	@ContributesAndroidInjector
	abstract fun contributeWizardFirstFragment(): WizardFirstFragment

	@Binds
	@IntoMap
	@ViewModelKey(WizardFirstViewModel::class)
	abstract fun bindWizardFirstViewModel(wizardFirstViewModel: WizardFirstViewModel): ViewModel

	@ContributesAndroidInjector
	abstract fun contributeAnalyzerResultFragment(): WizardSecondFragment

	@Binds
	@IntoMap
	@ViewModelKey(WizardSecondViewModel::class)
	abstract fun bindAnalyzerResultViewModel(wizardSecondViewModel: WizardSecondViewModel): ViewModel

	@ContributesAndroidInjector
	abstract fun contributeAnalyzerResultListFragment(): AnalyzerResultListFragment

	@Binds
	@IntoMap
	@ViewModelKey(WizardSecondListViewModel::class)
	abstract fun bindAnalyzerResultListViewModel(wizardSecondViewModel: WizardSecondListViewModel): ViewModel

	@ContributesAndroidInjector
	abstract fun contributeAnalyzerResultPlayerFragment(): WizardSecondPlayerFragment

	@Binds
	@IntoMap
	@ViewModelKey(WizardSecondPlayerViewModel::class)
	abstract fun bindAnalyzerResultPlayerViewModel(wizardSecondViewModel: WizardSecondPlayerViewModel): ViewModel

}