package cz.kotox.core.ffmpeg.model

import com.opkix.base.ffmpeg.model.VideoEffectType
import java.io.Serializable

enum class VideoTextAlignment() {
	LEFT,
	CENTER,
	RIGHT
}

data class TextMeasurement(val textBoxWidth: Float, val lineBaselineList: List<Float>) : Serializable

data class VideoTextItem(
	val text: String,
	val translationX: Float,// Translation of top left corner according to the output video resolution
	val translationY: Float,// Translation of top left corner according to the output video resolution
	val fontPath: String,
	val fontSize: Float,
	val fontColorHex: String,
	val textMeasurement: TextMeasurement,
	val textAlignment: VideoTextAlignment
) : Serializable

data class VideoCompositionItem(
	val videoPath: String,
	val videoOverlayPath: String?,
	val startTimeInMillis: Long,
	val endTimeInMillis: Long,
	val audioStreamList: List<AudioStreamDefinition>,
	val videoStreamList: List<VideoStreamDefinition>,
	val effectType: VideoEffectType = VideoEffectType.NONE, //Temporarily default because feature is not implemented yet
	val rotateInDegreeClockwise: Float,
	val zoom: Float,
	val translationX: Float,
	val translationY: Float,
	val speedMultiplier: Float,
	val volumeMultiplier: Float,
	val textItemList: List<VideoTextItem>
) : Serializable


data class BackgroundMusicItem(
	val path: String,
	val volumeMultiplier: Float
) : Serializable

data class VideoDurationItem(
	val path: String,
	val speedMultiplier: Float,
	val startTimeInMillis: Long? = null,
	val endTimeInMillis: Long? = null
)

data class VideoOutputItem(
	val path: String,
	val width: Int,
	val height: Int
) : Serializable

data class CanvasItem(val width: Int, val height: Int)

data class AudioStreamDefinition(val index: Int) : Serializable

data class VideoStreamDefinition(val index: Int) : Serializable

data class VideoCompositionResult(val composedPath: String) : Serializable

object FFMPEGVideoEncoder {
	object H264 {
		//https://trac.ffmpeg.org/wiki/Encode/H.264
		const val FLAG = "h264"

		object RateControlMode {
			object CRF {
				//Constant Rate Factor - Use this mode if you want to keep the best quality and don't care about the file size.
				const val FLAG = "-crf"
				const val VALUE_LOSSLESS = "0"
				const val VALUE_OPTIMAL = "18"
				const val VALUE_GOOD_COMPROMISE = "20"
				const val VALUE_DEFAULT = "23"
				const val VALUE_WORST_QUALITY = "51"

				/**
				 * A preset is a collection of options that will provide a certain encoding speed to compression ratio.
				 * A slower preset will provide better compression.
				 * Use the slowest preset that you have patience for.
				 */
				object Preset {
					const val FLAG = "-preset"
					const val VALUE_ULTRA_FAST = "ultrafast"
					const val VALUE_SUPER_FAST = "superfast"
					const val VALUE_VERY_FAST = "veryfast"
					const val VALUE_FASTER = "faster"
					const val VALUE_FAST = "fast"
					const val VALUE_DEFAULT = "medium"
					const val VALUE_SLOW = "slow"
					const val VALUE_SLOWER = "slower"
					const val VALUE_VERY_SLOW = "veryslow"

				}
			}
		}
	}
}