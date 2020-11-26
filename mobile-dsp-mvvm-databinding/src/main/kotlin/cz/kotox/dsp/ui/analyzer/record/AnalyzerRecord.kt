package cz.kotox.dsp.ui.analyzer.record

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigation
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.dsp.DspAnalyzerProvider
import cz.kotox.core.dsp.DspAnalyzerResult
import cz.kotox.core.dsp.model.PitchAlgorithm
import cz.kotox.core.ktools.mutableLiveDataOf
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

class AnalyzerRecordFragment @Inject constructor(
		//private val appPreferences: AppPreferences
) : BaseAnalyzerFragment<AnalyzerRecordViewModel, AnalyzerRecordFragmentBinding>(
		R.layout.analyzer_record_fragment,
		NavigationType.CLOSE
) {

	override val viewModel: AnalyzerRecordViewModel by viewModels()

	private val permissionManager: FragmentPermissionManager by lazyUnsafe { FragmentPermissionManager(this, appPreferences) }

	override fun onResume() {
		super.onResume()
		checkVoiceRecordingPermission()
	}


//	private fun verifyPermissions() {
//		if (!permissionManager.checkRecordAudioPermission()) {
//			permissionManager.requestRecordAudioPermissions({}, {
//				showToast("RecordAudio permissions is required for audio analysis")
//				activity?.finish()
//			})
//		}
//	}

	private fun checkVoiceRecordingPermission() {
		if (!permissionManager.checkRecordAudioPermission()) {

			TODO("No Mic screen is not implemented")
			//navController.navigate(VoiceRecordingFragmentDirections.navigateToNoMicQuestion())

		} else /*if (permissionManager.checkRecordAudioPermission())*/ {
			//navController.navigate(NoMicFragmentDirections.navigateToVoiceRecording())
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

	@ExperimentalCoroutinesApi
	override fun onStart() {
		super.onStart()
		viewModel.startRecording()
	}

	override fun onPause() {
		viewModel.stopRecording()
		super.onPause()
	}


	override fun onDestroyView() {
		binding.graphView.stopPlotting()
		super.onDestroyView()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		permissionManager.onPermissionResult(requestCode, permissions, grantResults)
	}
}

class AnalyzerRecordViewModel @Inject constructor(
		val dspAnalyzer: DspAnalyzerProvider
) : BaseAnalyzerViewModel() {

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
	fun startRecording() {
		launch {
			pitchAlgorithm.value?.let { initRecording(useProbability = false, probabilityThreshold = 0f, pitchAlgorithm = PitchAlgorithm.FFT_YIN) }
		}
	}

	fun stopRecording() {
		dspAnalyzer.stopDispatch()
	}

	private fun cleanUpMeasurement() {
		analyzerViewModel.pitchList.clear()
		min.value = "0"
		max.value = "0"
		frequency.value = "0"
		amplitude.value = "0"
	}

	@ExperimentalCoroutinesApi
	private suspend fun initRecording(useProbability: Boolean, probabilityThreshold: Float, pitchAlgorithm: PitchAlgorithm) {
		Timber.w(">>>X init recording")
		dspAnalyzer.stopDispatch()
		dspAnalyzer.runDispatch(useProbability = useProbability, probabilityThreshold = probabilityThreshold, pitchAlgorithm = pitchAlgorithm, currentPitchList = analyzerViewModel.pitchList.toList())
				//.onStart { delay(5000) } //just the test whether recording start when collect is invoked.
				.flowOn(Dispatchers.IO)
				.collect { dispatchResult ->

					when (dispatchResult) {
						is DspAnalyzerResult.Error -> {
							TODO("Show toast error event not implemented")
//							sendEvent(ShowToastEvent(dispatchResult.throwable.message
//									?: "Unexpected issue!"))
						}
						is DspAnalyzerResult.Data -> {
							val sample = dispatchResult.voiceSample
							Timber.i(">>> FLOW pitch[$sample.pitch], min[${analyzerViewModel.pitchList.map { it.pitch }.min()}],max[${analyzerViewModel.pitchList.map { it.pitch }.max()}]")

							waveList.add(WaveSample(sample.time.toLong(), (sample.amplitude * 100).toInt()))

							if (sample.pitch > 0) {

								if (sample.pitch < analyzerViewModel.pitchList.map { it.pitch }.min() ?: sample.pitch) {
									min.value = String.format("%.1f", sample.pitch)
								}
								if (sample.pitch > analyzerViewModel.pitchList.map { it.pitch }.max() ?: sample.pitch) {
									max.value = String.format("%.1f", sample.pitch)
								}

								analyzerViewModel.pitchList.add(sample)
								frequency.value = String.format("%.1f", sample.frequency)
								amplitude.value = String.format("%.3f", sample.amplitude)
							}
						}
					}
				}
	}


}



