package cz.kotox.dsp.ui.analyzer

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.Navigation
import cz.kotox.core.arch.BaseActivityViewModel
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