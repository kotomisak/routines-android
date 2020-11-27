package cz.kotox.dsp.ui.analyzer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import cz.kotox.core.arch.*
import cz.kotox.core.arch.di.viewModel.AssistedSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.utility.ActivityPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import cz.kotox.dsp.R
import cz.kotox.dsp.app.AppNavigator
import cz.kotox.dsp.databinding.AnalyzerActivityBinding
import cz.kotox.dsp.di.injector
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordFragmentDirections
import cz.kotox.dsp.ui.analyzer.record.NoMicFragmentDirections
import timber.log.Timber
import javax.inject.Inject

class AnalyzerActivity : BaseActivityViewModel<AnalyzerViewModel, AnalyzerActivityBinding>(
		R.layout.analyzer_activity,
		R.id.analyzer_nav_host_fragment,
		R.navigation.analyzer_navigation
) {

	@Inject
	lateinit var navigator: AppNavigator

	@Inject
	lateinit var appPreferences: AppPreferences

	private val permissionManager: ActivityPermissionManager by lazyUnsafe { ActivityPermissionManager(this, appPreferences) }

	override val viewModel: AnalyzerViewModel by viewModels()


	override fun onCreate(savedInstanceState: Bundle?) {
		injector.inject(this)
		super.onCreate(savedInstanceState)
	}

	override fun onResume() {
		super.onResume()
		checkVoiceRecordingPermission()
	}

	private fun checkVoiceRecordingPermission() {
		if (!permissionManager.checkRecordAudioPermission() && navController.currentDestination?.id == R.id.analyzer_process) {
			navController.navigate(AnalyzerRecordFragmentDirections.navigateToNoMicScreen())
		} else if (permissionManager.checkRecordAudioPermission() && navController.currentDestination?.id == R.id.no_mic) {
			navController.navigate(NoMicFragmentDirections.navigateToVoiceRecording())
		}
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
) : BaseFragmentViewModel<V, B>(layoutResId, navigationType) {

	/**
	 * IMPORTANT! keep at least one field injection in this abstract class, since it solves known MembersInjector multiple issue
	 * https://github.com/google/dagger/issues/1104
	 */
	@Inject
	lateinit var appPreferences: AppPreferences

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		viewModel.analyzerViewModel = ViewModelProvider(requireActivity(), requireActivity().defaultViewModelProviderFactory)[AnalyzerViewModel::class.java]

	}

}

abstract class BaseAnalyzerViewModel : BaseViewModel(), LifecycleObserver {
	lateinit var analyzerViewModel: AnalyzerViewModel // is inserted after constructor
		internal set

}