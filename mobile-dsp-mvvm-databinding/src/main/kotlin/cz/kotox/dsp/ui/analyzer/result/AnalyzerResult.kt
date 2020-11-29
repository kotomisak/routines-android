package cz.kotox.dsp.ui.analyzer.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.arch.ktools.DataBoundAdapter
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.entity.AppVersion
import cz.kotox.dsp.BR
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerResultFragmentBinding
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerFragment
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerViewModel
import timber.log.Timber
import javax.inject.Inject

interface AnalyzerResultView {
	val resultListAdapter: DataBoundAdapter<VoiceSample>
}

class AnalyzerResultFragment  @Inject constructor() : BaseAnalyzerFragment<AnalyzerResultViewModel, AnalyzerResultFragmentBinding>(
		R.layout.analyzer_result_fragment,
		NavigationType.UP
), AnalyzerResultView {

	override val resultListAdapter: DataBoundAdapter<VoiceSample> = DataBoundAdapter(this, R.layout.item_audio_sample, BR.item, object : DiffUtil.ItemCallback<VoiceSample>() {
		override fun areItemsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem.isItemTheSame(newItem)
		override fun areContentsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem == newItem
	})

	override val viewModel: AnalyzerResultViewModel by viewModels()

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		//binding.analyzerViewpager.adapter = AnalyzerResultPagerAdapter(requireActivity().supportFragmentManager)
		//binding.analyzerTabs.setupWithViewPager(binding.analyzerViewpager)

		binding.navigateFinishAnalyzerBt.setOnClickListener {
			activity?.finish()
		}
	}
}

class AnalyzerResultViewModel @Inject constructor(appVersion: AppVersion) : BaseAnalyzerViewModel() {

//	@Inject
//	lateinit var appVersion: AppVersion


	val token: MutableLiveData<String> = MutableLiveData()

	val appVersionString = "${appVersion.versionName} (${appVersion.versionCode})"

//	val appVersionString = preferencesCore.sampleToken

	init {
		Timber.e(">>> new process viewmodel")
	}

}

//class AnalyzerResultPagerAdapter(
//	fragmentManager: FragmentManager
//) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
//
//	private val fragments: List<Pair<String, () -> Fragment>> = listOf(
//		Pair("result list", { AnalyzerResultListFragment.newInstance() })
//	)
//
//	override fun getItem(pos: Int) = fragments[pos].second.invoke()
//
//	override fun getCount() = fragments.size
//
//	override fun getPageTitle(pos: Int): String = fragments[pos].first
//}
