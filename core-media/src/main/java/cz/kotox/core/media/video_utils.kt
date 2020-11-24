package cz.kotox.core.media

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import cz.kotox.core.media.model.VideoDimensions
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

object VideoUtils {

	fun getVideoDuration(path: String): Long {
		val mediaRetriever = MediaMetadataRetriever().apply { setDataSource(path) }
		val durationString = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
		mediaRetriever.release()
		if (durationString == null) Log.e(VideoUtils.javaClass.name, "Unable to extract metadata MediaMetadataRetriever.METADATA_KEY_DURATION for path: $path")
		return durationString?.toLong() ?: 0
	}

	/**
	 * Get video dimensions with respect to rotation in metadata.
	 */
	fun getVideoDimensions(path: String, virtualRotation: Float = 0f): VideoDimensions {
		val mediaRetriever = MediaMetadataRetriever().apply { setDataSource(path) }
		val width = Integer.valueOf(mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
		val height = Integer.valueOf(mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
		val rotation = Integer.valueOf(mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION))
		mediaRetriever.release()
		Log.d(">>>", "Dimensions [$width,$height] with rotation [$rotation] virtualRotation [$virtualRotation] for $path")
		//return VideoDimensions(width, height)//keep width/height
		return when (val finalRotation = (rotation + virtualRotation).rem(360)) {
			90f, 270f -> VideoDimensions(height, width)//switch width/height
			0f, 180f, 360f -> VideoDimensions(width, height)//keep width/height
			else -> { //Compute FIT dimensions for rotated image
				//https://iiif.io/api/annex/notes/rotation/
				val adjustedDimensions: VideoDimensions = if (finalRotation in 0f..89f) VideoDimensions(width, height) else VideoDimensions(height, width)
				val rotationDouble = finalRotation.toDouble()
				val rotatedWidth: Int = ceil(adjustedDimensions.width * cos(rotationDouble) + adjustedDimensions.height * sin(rotationDouble)).toInt()
				val rotatedHeight: Int = ceil(adjustedDimensions.width * sin(rotationDouble) + adjustedDimensions.height * cos(rotationDouble)).toInt()
				VideoDimensions(rotatedWidth, rotatedHeight)
			}
		}
	}

	fun getStabilizedPath(path: String) = path.replace(".mp4", "_stabilized.mp4")

	fun getBitmapForThumbnail(videoPath: String, frameMs: Long = 0): Bitmap? {
		val mediaRetriever = MediaMetadataRetriever().apply { setDataSource(videoPath) }
		return mediaRetriever.getFrameAtTime(frameMs * 1000)
	}
}
