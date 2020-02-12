package cz.kotox.dsp.ui.analyzer.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.DiffUtil
import be.tarsos.dsp.AudioGenerator
import cz.kotox.core.arch.ShowToastEvent
import cz.kotox.core.arch.ktools.DataBoundAdapter
import cz.kotox.core.dsp.DspPlayerProvider
import cz.kotox.core.dsp.DspPlayerResult
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.dsp.BR
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerResultListFragmentBinding
import cz.kotox.dsp.databinding.AnalyzerResultPlayerFragmentBinding
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerFragment
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

interface AnalyzerResultDetailView {
	val resultListAdapter: DataBoundAdapter<VoiceSample>
}

class AnalyzerResultListFragment : BaseAnalyzerFragment<AnalyzerResultListViewModel, AnalyzerResultListFragmentBinding>(), AnalyzerResultDetailView {

	override val resultListAdapter: DataBoundAdapter<VoiceSample> = DataBoundAdapter(this, R.layout.item_audio_sample, BR.item, object : DiffUtil.ItemCallback<VoiceSample>() {
		override fun areItemsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem.isItemTheSame(newItem)
		override fun areContentsTheSame(oldItem: VoiceSample, newItem: VoiceSample): Boolean = oldItem == newItem
	})

	override fun inflateBindingLayout(inflater: LayoutInflater) = AnalyzerResultListFragmentBinding.inflate(inflater)
	//
	override fun setupWizardViewModel() = findViewModel<AnalyzerResultListViewModel>()

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

class AnalyzerResultPlayerFragment : BaseAnalyzerFragment<AnalyzerResultPlayerViewModel, AnalyzerResultPlayerFragmentBinding>() {

	override fun inflateBindingLayout(inflater: LayoutInflater) = AnalyzerResultPlayerFragmentBinding.inflate(inflater)

	override fun setupWizardViewModel() = findViewModel<AnalyzerResultPlayerViewModel>()

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.playBt.setOnClickListener {
			startPlayer(it)
		}

		binding.stopBt.isEnabled = false
		binding.stopBt.setOnClickListener {
			stopPlayer(it)
		}
	}

	private fun startPlayer(it: View) {
		it.isEnabled = false
		viewModel.play()
		binding.stopBt.isEnabled = true
	}

	private fun stopPlayer(it: View) {
		it.isEnabled = false
		viewModel.stop()
		binding.playBt.isEnabled = true
	}

	companion object {
		fun newInstance() = AnalyzerResultPlayerFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}

	override fun onPause() {
		stopPlayer(binding.stopBt)
		super.onPause()
	}
}

class AnalyzerResultPlayerViewModel @Inject constructor(private val dspPlayer: DspPlayerProvider) : BaseAnalyzerViewModel(), LifecycleObserver {
	private var audioGenerator: AudioGenerator? = null

	@ExperimentalCoroutinesApi
	fun play() {
		launch() {
			dspPlayer.playFrequency().flowOn(Dispatchers.IO).collect {
				run {
					when (it) {
						is DspPlayerResult.Error -> {
							ShowToastEvent(it.exception.message ?: "Unexpected issue!")
						}
						is DspPlayerResult.Success -> {
							audioGenerator = it.audioGenerator
						}
					}
					Timber.d(">>> PLAYER PLAY generator[${audioGenerator}]")
				}

			}
		}
	}

	fun stop() {
		Timber.d(">>> PLAYER STOP generator[${audioGenerator}]")
		try {
			audioGenerator?.stop()
			dspPlayer.finished()
		} catch (ise: IllegalStateException) {
			Timber.w(ise, "Non fatal illegal state when stopping generator")
		}
	}
}