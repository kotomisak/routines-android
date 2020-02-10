package cz.kotox.dsp.ui.analyzer

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cz.kotox.core.arch.BaseFragmentViewModel
import cz.kotox.core.arch.BaseViewModel

abstract class BaseAnalyzerFragment<V : BaseAnalyzerViewModel, B : ViewDataBinding> : BaseFragmentViewModel<V, B>() {
	companion object {
		fun getCreateConsultationViewModel(activity: FragmentActivity, viewModelFactory: ViewModelProvider.Factory) =
			ViewModelProviders.of(activity, viewModelFactory).get(AnalyzerViewModel::class.java)
	}

	/**
	 * Different abstract class, so that every wizard fragment has main view model set
	 */
	abstract fun setupWizardViewModel(): V

	/**
	 * For wizard screens this is final, so that we can ensure, every screen has mainViewModel initialized
	 */
	final override fun setupViewModel(): V {
		val currentViewModel = setupWizardViewModel()
		currentViewModel.mainViewModel = getCreateConsultationViewModel(baseActivity, viewModelFactory)
		return currentViewModel
	}
}

abstract class BaseAnalyzerViewModel : BaseViewModel(), LifecycleObserver {
	lateinit var mainViewModel: AnalyzerViewModel // is inserted after constructor
		internal set

}