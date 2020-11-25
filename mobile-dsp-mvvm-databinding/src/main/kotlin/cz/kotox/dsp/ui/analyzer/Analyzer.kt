package cz.kotox.dsp.ui.analyzer

import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import cz.kotox.core.arch.BaseActivityViewModel
import cz.kotox.core.arch.BaseFragmentViewModel
import cz.kotox.core.arch.BaseViewModel
import cz.kotox.core.database.PreferencesCommon
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.entity.AppVersion
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerActivityBinding
import timber.log.Timber
import javax.inject.Inject

class AnalyzerActivity : BaseActivityViewModel<AnalyzerViewModel, AnalyzerActivityBinding>() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	override fun inflateBindingLayout(inflater: LayoutInflater) = AnalyzerActivityBinding.inflate(inflater)

	override fun setupViewModel() = findViewModel<AnalyzerViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Navigation.findNavController(this, R.id.analyzer_nav_host_fragment)
			.setGraph(R.navigation.analyzer_navigation, intent.extras)

	}

}

class AnalyzerViewModel @Inject constructor(appVersion: AppVersion) : BaseViewModel(), LifecycleObserver {

	val pitchList = mutableListOf<VoiceSample>()

	init {
		Timber.e(">>> AnalyzerViewModel INIT")
	}

	override fun onCleared() {
		Timber.e(">>> AnalyzerViewModel CLEARED")
		super.onCleared()
	}
}

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