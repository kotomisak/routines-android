package cz.kotox.dsp.ui.analyzer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import cz.kotox.core.arch.BaseActivityViewModel
import cz.kotox.core.arch.BasePermissionFragmentViewModel
import cz.kotox.core.arch.BaseViewModel
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.arch.di.viewModel.AssistedSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.dsp.R
import cz.kotox.dsp.app.AppNavigator
import cz.kotox.dsp.databinding.AnalyzerActivityBinding
import cz.kotox.dsp.di.injector
import cz.kotox.dsp.ui.MainViewModel
import timber.log.Timber
import javax.inject.Inject

class AnalyzerActivity : BaseActivityViewModel<AnalyzerViewModel, AnalyzerActivityBinding>(R.layout.analyzer_activity, R.id.main_nav_host_fragment, R.navigation.analyzer_navigation) {

	@Inject
	lateinit var navigator: AppNavigator

	override val viewModel: AnalyzerViewModel by viewModels()


	override fun onCreate(savedInstanceState: Bundle?) {
		injector.inject(this)

		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)//Explicitly disable night mode for Xiaomi(MIUI)
		setTheme(R.style.Soulvibe_Default) //Switch from splash screen

		super.onCreate(savedInstanceState)


	}

	override fun onStart() {
		super.onStart()
		//Setup controller for AppNavigator
		navigator.bind(Navigation.findNavController(this, navHostFragmentId))
	}

}

class AnalyzerViewModel @AssistedInject constructor(
		@Assisted private val savedStateHandle: SavedStateHandle,
		@Assisted private val args: ViewModelArgs
) : BaseViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateViewModelFactory<AnalyzerViewModel>

	val pitchList = mutableListOf<VoiceSample>()

	init {
		Timber.e(">>> AnalyzerViewModel INIT")
	}

	override fun onCleared() {
		Timber.e(">>> AnalyzerViewModel CLEARED")
		super.onCleared()
	}
}

abstract class BaseAnalyzerFragment<V : BaseAnalyzerViewModel, B : ViewDataBinding>(
		@LayoutRes layoutResId: Int,
		navigationType: NavigationType
) : BasePermissionFragmentViewModel<V, B>(layoutResId, navigationType) {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.mainViewModel = ViewModelProvider(requireActivity(), requireActivity().defaultViewModelProviderFactory)[AnalyzerViewModel::class.java]

	}

}

abstract class BaseAnalyzerViewModel : BaseViewModel(), LifecycleObserver {
	lateinit var mainViewModel: AnalyzerViewModel // is inserted after constructor
		internal set

}