package com.opkix.base.ffmpeg

/**
 * Rotate video.
 * Define scale type for the case rotation is not just flip.
 */
internal fun getRotateFilter(angelInDegree: Float, scaleType: ScaleType): String {
	val adjustDimensions = when (angelInDegree) {
		90f, 270f -> ":ow=ih:oh=iw" //just switch dimensions
		180f, 360f, 0f -> "" //dimensions stays the same
		else -> when (scaleType) {
			//Fit respects x264 codec requiring even numbers, this rotation follow even numbers in final size.
			//Without respect to x264 codec you can use just :ow='hypot(iw,ih)':oh=ow
			ScaleType.FIT -> ":ow='ceil(hypot(iw,ih)/2)*2':oh=ow"
			ScaleType.CROP -> ":ow='min(iw,ih)/sqrt(2)':oh=ow:c=none"
		}
	}
	return when (angelInDegree) {
		0f, 360f -> "" //rotation is not required
		else -> "rotate=${angelInDegree}*PI/180${adjustDimensions}"
	}
}