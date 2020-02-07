package com.opkix.base.ffmpeg

import cz.kotox.core.ffmpeg.model.VideoCompositionItem

/**
 * Start counting video PTS(presentation timestamp) from zero.
 */
internal fun getVideoResetTimestampFilter() = "setpts=PTS-STARTPTS"

/**
 * Start counting audio PTS(presentation timestamp) from zero.
 */
internal fun getAudioResetTimestampFilter() = "asetpts=PTS-STARTPTS"

internal fun getVideoTrimFilter(item: VideoCompositionItem) =
	"trim=${millisToFFMPEGTimeDuration(item.startTimeInMillis)}:${millisToFFMPEGTimeDuration(item.endTimeInMillis)}"

internal fun getAudioTrimFilter(item: VideoCompositionItem) =
	"atrim=${millisToFFMPEGTimeDuration(item.startTimeInMillis)}:${millisToFFMPEGTimeDuration(item.endTimeInMillis)}"

/**
 * https://ffmpeg.org/ffmpeg-utils.html#Time-duration
 * [-]S+[.m...]
 * S expresses the number of seconds, with the optional decimal part m.
 * The optional ‘-’ indicates negative duration.
 */
private fun millisToFFMPEGTimeDuration(timeInMillis: Long): String = "${timeInMillis.div(1000)}.${timeInMillis.rem(1000)}"
