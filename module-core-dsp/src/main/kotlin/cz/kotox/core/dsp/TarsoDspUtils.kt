package cz.kotox.core.dsp

import be.tarsos.dsp.AudioEvent
import be.tarsos.dsp.EnvelopeFollower
import be.tarsos.dsp.pitch.PitchDetectionResult
import cz.kotox.core.utility.median

object TarsoDspUtils {
	fun computeFrequency(pitchDetectionResult: PitchDetectionResult, previousPitchList: List<Float>): Float? {
		var frequency: Float? = pitchDetectionResult.pitch
		if (frequency == -1.0f || frequency == null) {
			frequency = previousPitchList.lastOrNull()
		} else {
			if (previousPitchList.isNotEmpty()) { // median filter
				// use the median as frequency
				frequency = median(previousPitchList.plus(frequency))
			}
		}
		return frequency
	}

	fun computeAmplitude(audioEvent: AudioEvent, voiceAnalysisSettings: VoiceAnalysisSettings, envelopeFollower: EnvelopeFollower): Float? {
		val env: FloatArray = computeEnvelope(audioEvent.floatBuffer, voiceAnalysisSettings, envelopeFollower)
		return env.lastOrNull()//env[env.size - 1]
	}

	private fun computeEnvelope(audioBuffer: FloatArray, voiceAnalysisSettings: VoiceAnalysisSettings, envelopeFollower: EnvelopeFollower): FloatArray {
		var envelope: FloatArray = floatArrayOf()
		if (voiceAnalysisSettings.envelopeFollow) {
			envelope = audioBuffer.clone()
			envelopeFollower.calculateEnvelope(envelope)
		}
		return envelope
	}
}
