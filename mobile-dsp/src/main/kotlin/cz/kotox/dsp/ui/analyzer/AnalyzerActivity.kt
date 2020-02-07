package cz.kotox.dsp.ui.analyzer

import android.os.Bundle
import android.view.LayoutInflater
import androidx.navigation.Navigation.findNavController
import cz.kotox.core.arch.BaseActivity
import cz.kotox.core.arch.BaseActivityViewModel
import cz.kotox.core.database.PreferencesCommon
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerActivityBinding
import javax.inject.Inject

class AnalyzerActivity : BaseActivityViewModel<AnalyzerViewModel, AnalyzerActivityBinding>() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	override fun inflateBindingLayout(inflater: LayoutInflater) = AnalyzerActivityBinding.inflate(inflater)

	override fun setupViewModel() = findViewModel<AnalyzerViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		findNavController(this, R.id.analyzer_nav_host_fragment)
			.setGraph(R.navigation.analyzer_navigation, intent.extras)

	}

}