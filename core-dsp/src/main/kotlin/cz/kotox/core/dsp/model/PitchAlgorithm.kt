package cz.kotox.core.dsp.model

import be.tarsos.dsp.pitch.PitchProcessor

enum class PitchAlgorithm {
	YIN,
	MPM,
	FFT_YIN,
	DYNAMIC_WAVELET,
	FFT_PITCH,
	AMDF
}

fun PitchAlgorithm.toPitchEstimationAlgorithm(): PitchProcessor.PitchEstimationAlgorithm {
	return when (this) {
		PitchAlgorithm.AMDF -> PitchProcessor.PitchEstimationAlgorithm.AMDF
		PitchAlgorithm.YIN -> PitchProcessor.PitchEstimationAlgorithm.YIN
		PitchAlgorithm.MPM -> PitchProcessor.PitchEstimationAlgorithm.MPM
		PitchAlgorithm.FFT_YIN -> PitchProcessor.PitchEstimationAlgorithm.FFT_YIN
		PitchAlgorithm.DYNAMIC_WAVELET -> PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET
		PitchAlgorithm.FFT_PITCH -> PitchProcessor.PitchEstimationAlgorithm.FFT_PITCH
	}
}

fun PitchAlgorithm.getNextPitchAlgorithm(): PitchAlgorithm {
	val currentPitchAlgorithm = requireNotNull(this)
	val newIndex = if (currentPitchAlgorithm.ordinal == PitchAlgorithm.values().size - 1) {
		0
	} else {
		currentPitchAlgorithm.ordinal + 1
	}
	return PitchAlgorithm.values()[newIndex]
}




