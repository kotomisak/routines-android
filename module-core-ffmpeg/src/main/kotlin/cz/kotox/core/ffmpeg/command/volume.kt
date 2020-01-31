package com.opkix.base.ffmpeg

/**
 * Adjust the input audio volume.
 * output_volume = volumeMultiplier * input_volume
 * The default value for volume is "1.0" = unity gain.
 * Amplification (values over 1.0) works well too.
 */
internal fun getVolumeFilter(volumeMultiplier: Float): String {
	return "volume=${volumeMultiplier}"
}
