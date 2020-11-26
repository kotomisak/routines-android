package cz.kotox.dsp.ui.analyzer.result

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.DiffUtil
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.arch.ktools.DataBoundAdapter
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.dsp.BR
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerResultListFragmentBinding
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerFragment
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerViewModel
import cz.kotox.dsp.ui.analyzer.record.AnalyzerRecordViewModel
import javax.inject.Inject

interface AnalyzerResultDetailView {
	val resultListAdapter: DataBoundAdapter<VoiceSample>
}

class AnalyzerResultListFragment @Inject constructor() : BaseAnalyzerFragment<AnalyzerResultListViewModel, AnalyzerResultListFragmentBinding>(
		R.layout.analyzer_result_list_fragment,
		NavigationType.CLOSE
), AnalyzerResultDetailView {

	override val resultListAdapter: DataBoundAdapter<VoiceSample> = DataBoundAdapter(this, R.layout.item_audio_sample, BR.item, object : DiffUtil.ItemCallback<VoiceSample>() {
		override fun areItemsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem.isItemTheSame(newItem)
		override fun areContentsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem == newItem
	})

	override val viewModel: AnalyzerResultListViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
	}

	companion object {
		fun newInstance() = AnalyzerResultListFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

}

class AnalyzerResultListViewModel @Inject constructor() : BaseAnalyzerViewModel(), LifecycleObserver {

}