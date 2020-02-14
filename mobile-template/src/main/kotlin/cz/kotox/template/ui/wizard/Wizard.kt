package cz.kotox.template.ui.wizard

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
import cz.kotox.core.entity.AppVersion
import cz.kotox.template.R
import cz.kotox.template.databinding.WizardActivityBinding
import cz.kotox.template.model.ValueSample
import timber.log.Timber
import javax.inject.Inject

class WizardActivity : BaseActivityViewModel<WizardViewModel, WizardActivityBinding>() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	override fun inflateBindingLayout(inflater: LayoutInflater) = WizardActivityBinding.inflate(inflater)

	override fun setupViewModel() = findViewModel<WizardViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		Navigation.findNavController(this, R.id.analyzer_nav_host_fragment)
			.setGraph(R.navigation.analyzer_navigation, intent.extras)

	}

}

class WizardViewModel @Inject constructor(appVersion: AppVersion) : BaseViewModel(), LifecycleObserver {

	val pitchList = mutableListOf<ValueSample>(
		ValueSample(100f, 0.12500, 0.3f, 100f),
		ValueSample(101f, 0.14000, 0.3f, 1011f)
	)

	init {
		Timber.e(">>> AnalyzerViewModel INIT")
	}

	override fun onCleared() {
		Timber.e(">>> AnalyzerViewModel CLEARED")
		super.onCleared()
	}
}

abstract class BaseWizardFragment<V : BaseWizardViewModel, B : ViewDataBinding> : BaseFragmentViewModel<V, B>() {
	companion object {
		fun getCreateConsultationViewModel(activity: FragmentActivity, viewModelFactory: ViewModelProvider.Factory) =
			ViewModelProviders.of(activity, viewModelFactory).get(WizardViewModel::class.java)
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

abstract class BaseWizardViewModel : BaseViewModel(), LifecycleObserver {
	lateinit var mainViewModel: WizardViewModel // is inserted after constructor
		internal set

}