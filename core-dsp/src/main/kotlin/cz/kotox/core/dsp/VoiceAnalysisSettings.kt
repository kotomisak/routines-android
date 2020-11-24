package com.lightbrite.android.core.dsp

import android.media.AudioFormat

data class VoiceAnalysisSettings(
	val crashOnStereoFile: Boolean = true,
	// tarsos global settings
	val pitchDetectAlgo: String = PITCH_DETECT_YIN,
	val bufferSize: Int = 2048, // 1024 too small for lower notes
	val overlap: Int = 10, // mic listen=0, file read/play=1
	val filterSize: Int = 1, // default 5 : reducing to 1 reduce latency
	// envelope follower for smoother amplitude
	val envelopeFollow: Boolean = true,
	val envelopeFollowAttackTime: Double = 0.005,
	val envelopeFollowReleaseTime: Double = 0.05,
	// voice analysis tarsos settings
	val estimationGainValue: Double = 1.0,
	val sourceGainValue: Double = 0.0,
	// voice analysis microphone tarsos
	val sampleRate: Int = 44100,
	val sampleSizeInBits: Int = 16,
	val channels: Int = 1,
	/** A property writable by the framework (see voice analysis) */
	val format: AudioFormat? = null,
	// onset simple
	val amplitudeThreshold: Float = 0.05f,
	// onset v2
	val onset: Boolean = false,
	val onsetPickThreshold: Double = 0.15,
	val onsetMinInterOnsetInterv: Double = 0.25,
	val onsetSilenceThreshold: Double = -55.0,

	//Noice threshold related to amplitude computation
	val noiseLevelThreshold: Float = 0.01f) {

	/**
	 *
	 * @param peakThreshold A threshold used for peak picking. Values between 0.1 and 0.8. Default is 0.3, if too many onsets are detected adjust to 0.4 or 0.5.
	 * @param silenceThreshold The threshold that defines when a buffer is silent. Default is -70dBSPL. -90 is also used.
	 * @param minimumInterOnsetInterval The minimum inter-onset-interval in seconds. When two onsets are detected within this interval the last one does not count. Default is 0.004 seconds.
	 *
	 * double pickThreshold = 0.15;
	 * double minInterOnsetInterv= 0.25;
	 * double silenceThreshold = -55;
	 *
	 **/

	companion object {
		const val PITCH_DETECT_YIN = "yin"
		val DEFAULT = VoiceAnalysisSettings()
	}
}