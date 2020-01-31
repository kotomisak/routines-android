package com.opkix.base.ffmpeg

internal fun getVideoSpeedFilter(speedMultiplier: Float): String {
	if (speedMultiplier <= 0) return ""
	return when (speedMultiplier) {
		1f -> ""
		else -> "setpts=${1 / speedMultiplier}*PTS"
	}
}

internal fun getAudioSpeedFilter(speedMultiplier: Float): String {
	if (speedMultiplier <= 0) return ""
	return when (speedMultiplier) {
		1f -> ""
		else -> "atempo=${speedMultiplier}"
	}
}
