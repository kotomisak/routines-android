package cz.kotox.core.media.video.utility

import android.content.res.AssetFileDescriptor
import android.media.MediaMetadataRetriever

fun getMediaDurationInMillis(afd: AssetFileDescriptor): Long {
	val mediaRetriever = MediaMetadataRetriever().apply { setDataSource(afd.fileDescriptor, afd.startOffset, afd.length) }
	val durationInMillis: String? = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
	mediaRetriever.release()
	return durationInMillis?.toLong() ?: 0L
}