package cz.kotox.core.dsp

import android.media.AudioFormat

class VoiceAnalysisSettings {
	var crashOnStereoFile = true
	// tarsos global settings
	var pitchDetectAlgo = "yin"
	var bufferSize = 2048 // 1024 too small for lower notes
	var overlap = 10 // mic listen=0, file read/play=1
	var filterSize = 1 // default 5 : reducing to 1 reduce latency
	// envelope follower for smoother amplitude
	var envelopeFollow = true
	var envelopeFollowAttackTime = 0.005
	var envelopeFollowReleaseTime = 0.05
	// voice analysis tarsos settings
	var estimationGainValue = 1.0
	var sourceGainValue = 0.0
	// voice analysis microphone tarsos
	var sampleRate = 44100
	var sampleSizeInBits = 16
	var channels = 1
	/** A property writable by the framework (see voice analysis) */
	var format: AudioFormat? = null
	// onset simple
	var amplitudeThreshold = 0.05f
	// onset v2
	var onset = false
	var onsetPickThreshold = 0.15
	var onsetMinInterOnsetInterv = 0.25
	var onsetSilenceThreshold = -55.0

	//Noice threshold related to amplitude computation
	val noiseLevelThreshold = 0.01f
	/**
	 *
	 * @param peakThreshold A threshold used for peak picking. Values between 0.1 and 0.8. Default is 0.3, if too many onsets are detected adjust to 0.4 or 0.5.
	 * @param silenceThreshold The threshold that defines when a buffer is silent. Default is -70dBSPL. -90 is also used.
	 * @param minimumInterOnsetInterval The minimum inter-onset-interval in seconds. When two onsets are detected within this interval the last one does not count. Default is 0.004 seconds.
	 */
/*
     *
     *
     * double pickThreshold = 0.15;
    double minInterOnsetInterv= 0.25;
    double silenceThreshold = -55;*/
	companion object {
		const val PITCH_DETECT_YIN = "yin"
		var DEFAULT = VoiceAnalysisSettings()
	}
}