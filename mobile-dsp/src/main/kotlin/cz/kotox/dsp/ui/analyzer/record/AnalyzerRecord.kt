package cz.kotox.dsp.ui.analyzer.record

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigation
import cz.kotox.core.arch.ShowToastEvent
import cz.kotox.core.arch.ktools.mutableLiveDataOf
import cz.kotox.core.dsp.DspAnalyzerProvider
import cz.kotox.core.dsp.DspAnalyzerResult
import cz.kotox.core.dsp.model.PitchAlgorithm
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.utility.FragmentPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import cz.kotox.core.view.graph.WaveSample
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.AnalyzerRecordFragmentBinding
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerFragment
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class AnalyzerRecordFragment : BaseAnalyzerFragment<AnalyzerRecordViewModel, AnalyzerRecordFragmentBinding>() {

	val permissionManager: FragmentPermissionManager by lazyUnsafe {
		FragmentPermissionManager(this)
	}

	override fun inflateBindingLayout(inflater: LayoutInflater) = AnalyzerRecordFragmentBinding.inflate(inflater)

	override fun setupWizardViewModel() = findViewModel<AnalyzerRecordViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
		verifyPermissions()
	}

	override fun onResume() {
		super.onResume()
		verifyPermissions()
	}

	private fun verifyPermissions() {
		if (!permissionManager.checkRecordAudioPermission()) {
			permissionManager.requestRecordAudioPermissions({}, {
				showToast("RecordAudio permissions is required for audio analysis")
				activity?.finish()
			})
		}
	}

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.navigateProcessingBt.setOnClickListener {
			viewModel.stopRecording()
			Navigation.findNavController(view).navigate(R.id.navigate_to_processing)
		}

		binding.changePitchAlgorithm.setOnClickListener {
			viewModel.changePitchAlgorithm()
		}

		binding.useProbability.setOnCheckedChangeListener { _, isChecked ->
			viewModel.changeProbabilityUsage(isChecked)
		}

		binding.graphView.apply {
			setGraphColor(Color.rgb(255, 255, 255))
			setCanvasColor(Color.rgb(20, 20, 20))
			setTimeColor(Color.rgb(255, 255, 255))
			setWaveLengthPX(15)
			maxAmplitude = 100 / 3
			setMasterList(viewModel.waveList)
			startPlotting()
		}
	}

	override fun onDestroyView() {
		binding.graphView.stopPlotting()
		super.onDestroyView()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		permissionManager.onPermissionResult(requestCode, permissions, grantResults)
	}
}

class AnalyzerRecordViewModel @Inject constructor(val dspAnalyzer: DspAnalyzerProvider) : BaseAnalyzerViewModel(), LifecycleObserver {

	val min: MutableLiveData<String> = mutableLiveDataOf("0")
	val max: MutableLiveData<String> = mutableLiveDataOf("0")
	val frequency: MutableLiveData<String> = mutableLiveDataOf("0")
	val amplitude: MutableLiveData<String> = mutableLiveDataOf("0")

	val pitchAlgorithm: MutableLiveData<PitchAlgorithm> = mutableLiveDataOf(PitchAlgorithm.FFT_YIN)
	var useProbability: MutableLiveData<Boolean> = mutableLiveDataOf(true)

	var recordingJob = Job()

	val pitchProbabilityThreshold = 0.8f

	var waveList: ArrayList<WaveSample> = ArrayList()

	override fun onCleared() {
		recordingJob.cancel()
		super.onCleared()
	}

	@ExperimentalCoroutinesApi
	fun changePitchAlgorithm() {
		//TODO MJ - temporarily commented because initRecording was called multiple times  (there is no way to stop previous service then)
//		dspAnalyzer.stopDispatch()
//		resetRecording()
//		pitchAlgorithm.value = requireNotNull(pitchAlgorithm.value).getNextPitchAlgorithm()
//		cleanUpMeasurement()
//		launch(recordingJob) { initRecording(requireNotNull(useProbability.value), pitchProbabilityThreshold, requireNotNull(pitchAlgorithm.value)) }

	}

	@ExperimentalCoroutinesApi
	fun changeProbabilityUsage(useProbability: Boolean) {
		//TODO MJ - temporarily commented because initRecording was called multiple times  (there is no way to stop previous service then)
//		dspAnalyzer.stopDispatch()
//		resetRecording()
//		this.useProbability.value = useProbability
//		cleanUpMeasurement()
//		launch(recordingJob) { initRecording(useProbability, pitchProbabilityThreshold, requireNotNull(pitchAlgorithm.value)) }
	}

	@ExperimentalCoroutinesApi
	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	private fun testLifeCycleOnResume() {
		launch {
			pitchAlgorithm.value?.let { initRecording(useProbability = false, probabilityThreshold = 0f, pitchAlgorithm = PitchAlgorithm.FFT_YIN) }
		}
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	private fun testLifeCycleOnPause() {
		stopRecording()
	}

	fun stopRecording() {
		dspAnalyzer.stopDispatch()
	}

	private fun cleanUpMeasurement() {
		mainViewModel.pitchList.clear()
		min.value = "0"
		max.value = "0"
		frequency.value = "0"
		amplitude.value = "0"
	}

	@ExperimentalCoroutinesApi
	private suspend fun initRecording(useProbability: Boolean, probabilityThreshold: Float, pitchAlgorithm: PitchAlgorithm) {
		Timber.w(">>>X init recording")
		dspAnalyzer.stopDispatch()
		dspAnalyzer.runDispatch(useProbability = useProbability, probabilityThreshold = probabilityThreshold, pitchAlgorithm = pitchAlgorithm, currentPitchList = mainViewModel.pitchList.toList())
				//.onStart { delay(5000) } //just the test whether recording start when collect is invoked.
				.flowOn(Dispatchers.IO)
				.collect { dispatchResult ->

					when (dispatchResult) {
						is DspAnalyzerResult.Error -> {
							sendEvent(ShowToastEvent(dispatchResult.throwable.message
									?: "Unexpected issue!"))
						}
						is DspAnalyzerResult.Data -> {
							val sample = dispatchResult.voiceSample
							Timber.i(">>> FLOW pitch[$sample.pitch], min[${mainViewModel.pitchList.map { it.pitch }.min()}],max[${mainViewModel.pitchList.map { it.pitch }.max()}]")

							waveList.add(WaveSample(sample.time.toLong(), (sample.amplitude * 100).toInt()))

							if (sample.pitch > 0) {

								if (sample.pitch < mainViewModel.pitchList.map { it.pitch }.min() ?: sample.pitch) {
									min.value = String.format("%.1f", sample.pitch)
								}
								if (sample.pitch > mainViewModel.pitchList.map { it.pitch }.max() ?: sample.pitch) {
									max.value = String.format("%.1f", sample.pitch)
								}

								mainViewModel.pitchList.add(sample)
								frequency.value = String.format("%.1f", sample.frequency)
								amplitude.value = String.format("%.3f", sample.amplitude)
							}
						}
					}
				}
	}


}


