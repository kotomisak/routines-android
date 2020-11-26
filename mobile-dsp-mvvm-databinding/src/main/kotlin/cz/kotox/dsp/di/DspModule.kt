package cz.kotox.dsp.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.dagger2.AssistedModule
import cz.kotox.dsp.ui.MainActivity
import cz.kotox.dsp.ui.MainFragment
import cz.kotox.dsp.ui.MainViewModel
import cz.kotox.dsp.ui.analyzer.AnalyzerActivity
import cz.kotox.dsp.ui.analyzer.AnalyzerViewModel
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordFragment
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordViewModel
import cz.kotox.dsp.ui.analyzer.result.AnalyzerResultFragment
import cz.kotox.dsp.ui.analyzer.result.AnalyzerResultViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import cz.kotox.core.arch.di.annotation.mapKey.FragmentKey
import cz.kotox.core.arch.di.annotation.mapKey.ViewModelKey
import cz.kotox.core.arch.di.viewModel.AssistedSavedStateViewModelFactory

@AssistedModule
@Module(includes = [AssistedInject_DspModuleBuilder::class])
abstract class DspModuleBuilder {

	@Binds
	@IntoMap
	@FragmentKey(MainFragment::class)
	abstract fun bindMainFragment(fragment: MainFragment): Fragment

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun bindMainViewModel(f: MainViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>



	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerViewModel::class)
	abstract fun bindAnalyzerViewModel(f: AnalyzerViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

	@Binds
	@IntoMap
	@FragmentKey(AnalyzerRecordFragment::class)
	abstract fun bindAnalyzerRecordFragment(fragment: AnalyzerRecordFragment): Fragment

	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerRecordViewModel::class)
	abstract fun bindAnalyzerRecordViewModel(viewModel: AnalyzerRecordViewModel): ViewModel


	@Binds
	@IntoMap
	@FragmentKey(AnalyzerResultFragment::class)
	abstract fun contributeAnalyzerResultFragment(fragment: AnalyzerResultFragment): Fragment

	@Binds
	@IntoMap
	@ViewModelKey(AnalyzerResultViewModel::class)
	abstract fun bindAnalyzerResultViewModel(viewModel: AnalyzerResultViewModel): ViewModel
}

@Module(includes = [DspModuleBuilder::class])
object DspModule

