package cz.kotox.dsp.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.Navigation
import cz.kotox.core.entity.AppVersion
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import cz.kotox.core.arch.BaseActivityViewModel
import cz.kotox.core.arch.BaseFragmentViewModel
import cz.kotox.core.arch.BaseViewModel
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.arch.di.viewModel.AssistedSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import cz.kotox.dsp.R
import cz.kotox.dsp.app.AppNavigator
import cz.kotox.dsp.databinding.AnalyzerActivityBinding
import cz.kotox.dsp.databinding.MainFragmentBinding
import cz.kotox.dsp.di.injector
import cz.kotox.dsp.ui.analyzer.AnalyzerViewModel
import javax.inject.Inject

class MainActivity : BaseActivityViewModel<AnalyzerViewModel, AnalyzerActivityBinding>(R.layout.main_activity, R.id.main_nav_host_fragment, R.navigation.dsp_main_navigation) {

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

class MainViewModel @AssistedInject constructor(
		private val appVersion: AppVersion,
		@Assisted private val savedStateHandle: SavedStateHandle,
		@Assisted private val args: ViewModelArgs
) : BaseViewModel() {

	@AssistedInject.Factory
	interface Factory : AssistedSavedStateViewModelFactory<MainViewModel>

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	fun testLifeCycleOnStart(){
		//Timber.e(">>> MainViewModel ON_START")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	fun testLifeCycleOnResume(){
		//Timber.e(">>> MainViewModel ON_RESUME")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	fun testLifeCycleOnPause(){
		//Timber.e(">>> MainViewModel ON_PAUSE")
	}
}

interface MainFragmentView {

}

class MainFragment  @Inject constructor(
) : BaseFragmentViewModel<MainViewModel,MainFragmentBinding>(
		R.layout.main_fragment,
		NavigationType.CLOSE
), MainFragmentView {

	override val viewModel: MainViewModel by viewModels()

}