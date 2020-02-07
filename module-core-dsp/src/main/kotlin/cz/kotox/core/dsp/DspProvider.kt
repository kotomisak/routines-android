package cz.kotox.core.dsp

import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.EnvelopeFollower
import be.tarsos.dsp.io.android.AudioDispatcherFactory
import be.tarsos.dsp.pitch.PitchDetectionHandler
import be.tarsos.dsp.pitch.PitchProcessor
import cz.kotox.core.dsp.TarsoDspComputation.computeAmplitude
import cz.kotox.core.dsp.TarsoDspComputation.computeFrequency
import cz.kotox.core.dsp.model.PitchAlgorithm
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.dsp.model.toPitchEstimationAlgorithm
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DspProvider @Inject constructor() {

	private var audioDispatcher: AudioDispatcher? = null
	private var currentAudioProcessor: PitchProcessor? = null

	private var voiceAnalysisSettings: VoiceAnalysisSettings = VoiceAnalysisSettings.DEFAULT
	private val sampleRate = voiceAnalysisSettings.sampleRate//22050 //sample rate must be supported by the capture device. Nonstandard sample rates can be problematic!
	private val audioBufferSize = voiceAnalysisSettings.bufferSize //size of the buffer defines how much samples are processed in one step.
	private val bufferOverlap = 0// How much consecutive buffers overlap (in samples). Half of the AudioBufferSize is common.
	private val envelopeFollower = EnvelopeFollower(sampleRate.toDouble(), voiceAnalysisSettings.envelopeFollowAttackTime, voiceAnalysisSettings.envelopeFollowReleaseTime);//attack, release

	fun stopDispatch() {
		currentAudioProcessor?.processingFinished()
		currentAudioProcessor?.let { audioDispatcher?.removeAudioProcessor(it) }
		//if (audioDispatcher?.isStopped == false) {
		audioDispatcher?.let {
			Timber.e(">>>X stopping dispatcher [${audioDispatcher}]")
			try {
				it.stop() //TODO MJ - think about checking for stopped
			} catch (e: IllegalStateException) {
				e.printStackTrace() //stop() called on an uninitialized AudioRecord.
			}
		}
	}

	@ExperimentalCoroutinesApi
	fun runDispatch(
		useProbability: Boolean,
		probabilityThreshold: Float,
		pitchAlgorithm: PitchAlgorithm,
		currentPitchList: List<VoiceSample>
	) = callbackFlow<VoiceSample> {

		stopDispatch()
		audioDispatcher = try {
			AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, audioBufferSize, bufferOverlap)
		} catch (ise: IllegalStateException) {
			stopDispatch()
			ise.printStackTrace()
			/**
			 *  java.lang.IllegalStateException: startRecording() called on an uninitialized AudioRecord.
			 *  at android.media.AudioRecord.startRecording(AudioRecord.java:1075)
			 *  at be.tarsos.dsp.io.android.AudioDispatcherFactory.fromDefaultMicrophone(Unknown Source:44)
			 */
			AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, audioBufferSize, bufferOverlap)
		}
		currentAudioProcessor = PitchProcessor(
			pitchAlgorithm.toPitchEstimationAlgorithm(),
			sampleRate.toFloat(),
			audioBufferSize,
			getPitchDetectionHandler(useProbability, probabilityThreshold, pitchAlgorithm, currentPitchList) { sendBlocking(it) }
		)
		audioDispatcher?.addAudioProcessor(currentAudioProcessor)
		Timber.e(">>>X running dispatcher [${audioDispatcher}]")
		audioDispatcher?.run()

	}

	private fun getPitchDetectionHandler(
		useProbability: Boolean,
		probabilityThreshold: Float,
		pitchAlgorithm: PitchAlgorithm,
		currentPitchList: List<VoiceSample>,
		sendSample: (sample: VoiceSample) -> Unit
	) = PitchDetectionHandler { pitchDetectionResult, audioEvent ->
		val pitchInHz = pitchDetectionResult.pitch
		Timber.i(">>> HANDLER pitch[$pitchInHz]Hz, probability[${pitchDetectionResult.probability}] RMS[${audioEvent.rms}] EVENT time[${audioEvent.timeStamp}], ALGORITHM[$pitchAlgorithm]")
		val amplitude = computeAmplitude(audioEvent, voiceAnalysisSettings, envelopeFollower)
		val frequency = computeFrequency(pitchDetectionResult, currentPitchList.map { it.pitch })

		if (useProbability) {
			if (pitchDetectionResult.probability > probabilityThreshold) {

				Timber.i(">>> HANDLER2 pitch[$pitchInHz]Hz, RMS[${audioEvent.rms}], dbSPL[${audioEvent.getdBSPL()}]dB")
				Timber.i(">>> HANDLER2b pitch[$pitchInHz]Hz, freq[${frequency}], amplitude[${amplitude}]")
				Timber.i(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
				sendSample(VoiceSample(pitchInHz, audioEvent.timeStamp, amplitude
					?: 0f, frequency ?: 0f))

			}
		} else {
			sendSample(VoiceSample(pitchInHz, audioEvent.timeStamp, amplitude ?: 0f, frequency
				?: 0f))
		}
	}

}