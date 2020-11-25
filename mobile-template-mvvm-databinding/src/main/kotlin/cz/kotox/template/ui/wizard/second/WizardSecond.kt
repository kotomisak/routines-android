package cz.kotox.template.ui.wizard.second

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import cz.kotox.core.PreferencesCore
import cz.kotox.core.entity.AppVersion
import cz.kotox.template.databinding.WizardSecondFragmentBinding
import cz.kotox.template.ui.wizard.BaseWizardFragment
import cz.kotox.template.ui.wizard.BaseWizardViewModel
import timber.log.Timber
import javax.inject.Inject

class WizardSecondFragment : BaseWizardFragment<WizardSecondViewModel, WizardSecondFragmentBinding>() {

	override fun inflateBindingLayout(inflater: LayoutInflater) = WizardSecondFragmentBinding.inflate(inflater)

	override fun setupWizardViewModel() = findViewModel<WizardSecondViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.analyzerViewpager.adapter = AnalyzerResultPagerAdapter(requireActivity().supportFragmentManager, context)
		binding.analyzerTabs.setupWithViewPager(binding.analyzerViewpager)

		binding.navigateFinishAnalyzerBt.setOnClickListener {
			activity?.finish()
		}
	}
}

class WizardSecondViewModel @Inject constructor(appVersion: AppVersion) : BaseWizardViewModel(), LifecycleObserver {

//	@Inject
//	lateinit var appVersion: AppVersion

	@Inject
	lateinit var preferencesCore: PreferencesCore

	val token: MutableLiveData<String> = MutableLiveData()

	val appVersionString = "${appVersion.versionName} (${appVersion.versionCode})"

//	val appVersionString = preferencesCore.sampleToken

	init {
		Timber.e(">>> new process viewmodel")
	}

}

class AnalyzerResultPagerAdapter(
	fragmentManager: FragmentManager,
	context: Context
) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	private val fragments: List<Pair<String, () -> Fragment>> = listOf(
		Pair("result player", { WizardSecondPlayerFragment.newInstance() }),
		Pair("result list", { AnalyzerResultListFragment.newInstance() })
	)

	override fun getItem(pos: Int) = fragments[pos].second.invoke()

	override fun getCount() = fragments.size

	override fun getPageTitle(pos: Int): String = fragments[pos].first
}
