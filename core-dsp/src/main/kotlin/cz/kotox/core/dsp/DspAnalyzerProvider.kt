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
import cz.kotox.core.dsp.tarsos.SoulvibeAudioDispatcherFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

sealed class DspAnalyzerResult {
	class Data(val voiceSample: VoiceSample) : DspAnalyzerResult()
	class Error(val throwable: Throwable) : DspAnalyzerResult()
}

@Singleton
class DspAnalyzerProvider @Inject constructor(
	val dspEventTracker: DspEventTracker
) {

	private var audioDispatcher: AudioDispatcher? = null
	private var currentAudioProcessor: PitchProcessor? = null

	private var voiceAnalysisSettings: VoiceAnalysisSettings = VoiceAnalysisSettings.DEFAULT
	private val sampleRate = voiceAnalysisSettings.sampleRate//22050 //The requested sample rate must be supported by the capture device. Nonstandard sample rates can be problematic!
	private val audioBufferSize = voiceAnalysisSettings.bufferSize  //The size of the buffer defines how much samples are processed in one step. Common values are 1024,2048.
	private val bufferOverlap = audioBufferSize / 2// How much consecutive buffers overlap (in samples). Half of the AudioBufferSize is common. Originally tested with 0
	private val envelopeFollower = EnvelopeFollower(sampleRate.toDouble(), voiceAnalysisSettings.envelopeFollowAttackTime, voiceAnalysisSettings.envelopeFollowReleaseTime);//attack, release

	fun stopDispatch() {
		currentAudioProcessor?.processingFinished()
		currentAudioProcessor?.let { audioDispatcher?.removeAudioProcessor(it) }
		//if (audioDispatcher?.isStopped == false) {
		audioDispatcher?.let {
			Timber.d("Stopping dispatcher [${audioDispatcher}]")
			try {
				it.stop()
			} catch (e: IllegalStateException) {
				//stop() called on an uninitialized AudioRecord - this might also happen when called stop multiple times (better more than never :-)
				Timber.w(e)
			}
		}
	}

	@ExperimentalCoroutinesApi
	suspend fun runDispatch(
			currentPitchList: List<VoiceSample> = emptyList(),
			useProbability: Boolean = false,
			probabilityThreshold: Float = 0f,
			pitchAlgorithm: PitchAlgorithm = PitchAlgorithm.FFT_YIN
	) = callbackFlow<DspAnalyzerResult> {
		var finalSampleRate = sampleRate
		audioDispatcher = try {
			SoulvibeAudioDispatcherFactory.fromMicrophone(finalSampleRate, audioBufferSize, bufferOverlap, dspEventTracker)
		} catch (ise: IllegalStateException) {
			/**
			 *  java.lang.IllegalStateException: startRecording() called on an uninitialized AudioRecord.
			 *  at android.media.AudioRecord.startRecording(AudioRecord.java:1075)
			 *  at be.tarsos.dsp.io.android.AudioDispatcherFactory.fromDefaultMicrophone(Unknown Source:44)
			 */
			Timber.e(ise, "Failure when init dispatcher !!!")
			stopDispatch()
			try {
				//re-try, it usually works (But use original tarsos code in this case in order to be sure issue is not caused by custom factory)
				AudioDispatcherFactory.fromDefaultMicrophone(finalSampleRate, audioBufferSize, bufferOverlap)
			} catch (th: Throwable) {
				Timber.e(th, "Re-try of init failed! Resulting in error.")
				sendBlocking(DspAnalyzerResult.Error(th))
				null
			}
		} catch (iae: IllegalArgumentException) {
			/**
			 * Fatal Exception: java.lang.IllegalArgumentException
			 * Usually - Buffer size too small should be at least 16384
			 */
			Timber.e(iae, "Failure when init dispatcher !!!")
			stopDispatch()
			try {
				//re-try with lower sample rate ((But use original tarsos code in this case in order to be sure issue is not caused by custom factory))
				finalSampleRate = sampleRate / 2
				AudioDispatcherFactory.fromDefaultMicrophone(finalSampleRate, audioBufferSize, bufferOverlap)
			} catch (th: Throwable) {
				Timber.e(th, "Re-try with lower sample rate failed! Resulting in error.")
				sendBlocking(DspAnalyzerResult.Error(th))
				null
			}

		} catch (th: Throwable) {
			Timber.e(th, "Unexpected issue when using microphone by TarsosDSP! Resulting in error.")
			sendBlocking(DspAnalyzerResult.Error(th))
			null
		}

		val latestAudioDispatcher = audioDispatcher
		if (latestAudioDispatcher == null) {
			Timber.e(RuntimeException("Unable to initialize AudioDispatcher!"))
		} else {
			currentAudioProcessor = PitchProcessor(
				pitchAlgorithm.toPitchEstimationAlgorithm(),
				finalSampleRate.toFloat(),
				audioBufferSize,
				getPitchDetectionHandler(useProbability, probabilityThreshold, pitchAlgorithm, currentPitchList) { sendBlocking(DspAnalyzerResult.Data(it)) }
			)

			latestAudioDispatcher.addAudioProcessor(currentAudioProcessor)
			latestAudioDispatcher.run()
		}

		awaitClose { stopDispatch() }
	}

	private fun getPitchDetectionHandler(
			useProbability: Boolean,
			probabilityThreshold: Float,
			pitchAlgorithm: PitchAlgorithm,
			currentPitchList: List<VoiceSample>,
			sendSample: (sample: VoiceSample) -> Unit
	) = PitchDetectionHandler { pitchDetectionResult, audioEvent ->
		val pitchInHz = if (pitchDetectionResult.pitch < 0) 0f else pitchDetectionResult.pitch
		val amplitude: Float = computeAmplitude(audioEvent, voiceAnalysisSettings, envelopeFollower) ?: 0f

		val frequency: Float = computeFrequency(pitchDetectionResult, currentPitchList.map { it.pitch }) ?: 0f

		//NOTE: RMS or DBSPL is calculated every time we request it, so this logging should be uncommented just for development.
		//Timber.d("HANDLER EVENT time[${audioEvent.timeStamp}] pitch[$pitchInHz]Hz, freqency[${frequency}]Hz, amplitude[${amplitude}], probability[${pitchDetectionResult.probability}] RMS[${audioEvent.rms}],DBSPL[${audioEvent.getdBSPL()}], ALGORITHM[$pitchAlgorithm]")


		if (useProbability) {
			if (pitchDetectionResult.probability > probabilityThreshold) {
//				Timber.d(">>> HANDLER2a pitch[$pitchInHz]Hz, RMS[${audioEvent.rms}], dbSPL[${audioEvent.getdBSPL()}]dB")
//				Timber.d(">>> HANDLER2b pitch[$pitchInHz]Hz, freq[${frequency}], amplitude[${amplitude}]")
//				Timber.d(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
				sendSample(VoiceSample(pitchInHz, audioEvent.timeStamp, amplitude, frequency))
			}
		} else {
			sendSample(VoiceSample(pitchInHz, audioEvent.timeStamp, amplitude, frequency))
		}
	}
}