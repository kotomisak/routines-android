package cz.kotox.dsp.di

import androidx.lifecycle.ViewModel
import cz.kotox.core.di.ViewModelKey
import cz.kotox.dsp.ui.MainActivity
import cz.kotox.dsp.ui.MainFragment
import cz.kotox.dsp.ui.MainViewModel
import cz.kotox.dsp.ui.analyzer.AnalyzerActivity
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordFragment
import cz.kotox.dsp.ui.analyzer.result.AnalyzerResultFragment
import cz.kotox.dsp.ui.analyzer.result.AnalyzerResultViewModel
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordViewModel
import cz.kotox.dsp.ui.analyzer.AnalyzerViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MobileDspDaggerModule {

	@ContributesAndroidInjector()
	abstract fun contributeMainActivity(): MainActivity

	@ContributesAndroidInjector
	abstract fun contributeMainFragment(): MainFragment

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

	@ContributesAndroidInjector()
	abstract fun contributeAnalyzerActivity(): AnalyzerActivity

	@ContributesAndroidInjector
	abstract fun contributeAnalyzerFragment(): AnalyzerRecordFragment

	@ContributesAndroidInjector
	abstract fun contributeAnalyzerResultFragment(): AnalyzerResultFragment

	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerViewModel::class)
	abstract fun bindAnalyzerViewModel(analyzerViewModel: AnalyzerViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerRecordViewModel::class)
	abstract fun bindAnalyzerRecordViewModel(analyzerRecordViewModel: AnalyzerRecordViewModel): ViewModel

	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerResultViewModel::class)
	abstract fun bindAnalyzerProcessViewModel(analyzerResultViewModel: AnalyzerResultViewModel): ViewModel
}