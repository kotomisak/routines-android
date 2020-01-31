package com.opkix.base.ffmpeg

import android.media.MediaMetadataRetriever
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.opkix.base.ffmpeg.model.CanvasItem
import com.opkix.base.ffmpeg.model.VideoCompositionItem
import com.opkix.base.ffmpeg.model.VideoCompositionResult
import com.opkix.base.ffmpeg.model.VideoDurationItem
import com.opkix.base.ffmpeg.model.VideoOutputItem
import com.opkix.base.media.VideoUtils
import com.opkix.base.media.VideoUtils.getVideoDimensions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.math.abs

class FFmpegException(val errorCode: Int) : RuntimeException()

// TODO It's probably not necessary to return T in Success.
sealed class FFmpegResponse<out T> {
	class Failure(val exception: RuntimeException) : FFmpegResponse<Nothing>()
	class Error(val exception: FFmpegException) : FFmpegResponse<Nothing>()
	data class Success<T>(val result: T) : FFmpegResponse<T>()
	object Cancelled : FFmpegResponse<Nothing>()
	object AlreadyRunning : FFmpegResponse<Nothing>()
}

// TODO remove
object RotationResult

/**
 * Wrapper around com.arthenica.mobileffmpeg.FFmpeg implementation.
 * Use it as Singleton in the App to be able check state of running commands!
 */
class FFmpegWrapper {

	private var commandRunning: Boolean = false

	fun isRunning() = commandRunning

	suspend fun terminateRunningOperation() {
		withContext(Dispatchers.IO) {
			FFmpeg.cancel()
		}
	}

	suspend fun requestVideoRotation(videoPath: String, job: Job? = null): FFmpegResponse<RotationResult> {
		if (commandRunning) return FFmpegResponse.AlreadyRunning //Prevent wasting of time with data preparation when ffmpeg is already running.

		return withContext(job?.let { Dispatchers.IO + it } ?: Dispatchers.IO) {
			check(videoPath.isNotBlank())

			// get current rotation
			val currentRotation = MediaMetadataRetriever().apply { setDataSource(videoPath) }.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION).toInt()

			// If you want to set 90° to metadata, you have to set 270° to ffmpeg and vice versa. 0° and 180° work correctly.
			val newRotation = when (currentRotation) {
				0 -> 90
				90 -> 0
				180 -> 270
				270 -> 180
				else -> 0
			}

			val fileType = videoPath.substringAfterLast(".")
			val rotatedPath = videoPath.replace(".$fileType", "_rotated.$fileType")

			val rotationCommandArray = arrayOf(
				"-y",
				"-i",
				videoPath,
				"-map_metadata",
				"0",
				"-metadata:s:v",
				"rotate=$newRotation",
				"-codec",
				"copy",
				rotatedPath
			)

			val success = FFmpegResponse.Success(RotationResult)
			val result = executeFFmpegCommand(rotationCommandArray, success)

			if (result is FFmpegResponse.Success) {
				// remove original file and rename rotated one

				val originalFile = File(videoPath)
				val rotatedFile = File(rotatedPath)

				originalFile.delete()
				rotatedFile.renameTo(File(videoPath))
			}

			result
		}
	}

	fun doesFileTypeRequireConcatWithIntermediateFiles(filePath: String): Boolean = arrayOf("mp4").contains(File(filePath).extension.toLowerCase(Locale.ROOT))

	/**
	 * https://trac.ffmpeg.org/wiki/Concatenate
	 */
	suspend fun requestVideoComposition(
		inputItemsList: List<VideoCompositionItem>,
		outputItem: VideoOutputItem,
		progressCallback: (Float) -> Unit,
		job: Job? = null
	): FFmpegResponse<VideoCompositionResult> {
		if (commandRunning) return FFmpegResponse.AlreadyRunning //Prevent wasting of time with data preparation when ffmpeg is already running.
		return withContext(job?.let { Dispatchers.IO + it } ?: Dispatchers.IO) {

			try {
				//val inputItemsList = inputItemsListIn.map { it.copy(startTimeInMillis = 15_000, endTimeInMillis = 25_000) } //Just for dev tracing purpose
				val commandArray = getConcatFilterScaleCommand(inputItemsList, outputItem.path, CanvasItem(outputItem.width, outputItem.height))
				//Log.e(">>>:", commandArray.joinToString(separator = " "))
				val success = FFmpegResponse.Success(VideoCompositionResult(outputItem.path))
				executeFFmpegCommand(
					commandArray,
					success,
					progressCallback,
					inputItemsList.map { VideoDurationItem(it.path, it.speedMultiplier, it.startTimeInMillis, it.endTimeInMillis) }.toTypedArray()
				)
			} catch (exception: RuntimeException) {
				FFmpegResponse.Failure(exception)
			}
		}
	}

	/**
	 * CONCAT MP4 SOLUTION: transcoding to TS using intermediate files.
	 * ffmpeg -i input1.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts intermediate1.ts
	 * ffmpeg -i input2.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts intermediate2.ts
	 * ffmpeg -i "concat:intermediate1.ts|intermediate2.ts" -c copy -bsf:a aac_adtstoasc output.mp4
	 *
	 * 2videos 857MB original will be finally 1170MB when concatenated using this configuration. (duration about 1minute)
	 *
	 * Filtergraph 'scale=1080:1920' was defined for video output stream 0:0 but codec copy was selected.
	 * Filtering and streamcopy cannot be used together.
	 */
	private fun concatProtocolMp4UsingIntermediateFiles(
		inputItemsList: List<VideoCompositionItem>,
		progressCallback: (Float) -> Unit,
		outputPath: String,
		requiredCanvas: CanvasItem
	): FFmpegResponse<VideoCompositionResult> {

		val intermediateFileNameList = inputItemsList.map {
			val videoFile = File(it.path)
			val videoIntermediateFileName = "${videoFile.parent}/${videoFile.nameWithoutExtension}_tmp.ts"
			val videoIntermediateFile = File(videoIntermediateFileName)
			if (videoIntermediateFile.exists()) videoIntermediateFile.delete()
			videoIntermediateFileName
		}

		val stepsCount = 2 * inputItemsList.size + 1
		val stepsScaleCount = inputItemsList.size

		val scaledItemsList = inputItemsList.map {
			val originalFile = File(it.path)
			val scaledPath = "${originalFile.parent}/${originalFile.nameWithoutExtension}_scl.${originalFile.extension}"
			val scaledFile = File(scaledPath)
			if (scaledFile.exists()) scaledFile.delete() //TODO use -y instead of this way of delete!
			it.copy(path = scaledPath)
		}

		inputItemsList.forEachIndexed { index, videoItem ->
			val translationParams = TranslationParams(getVideoDimensions(videoItem.path, videoItem.rotateInDegreeClockwise), videoItem.translationX, videoItem.translationY)
			val scaleCommand = getScaleToCanvasStandaloneCommand(videoItem.path, scaledItemsList[index].path, requiredCanvas, ScaleType.CROP, translationParams)
			val scaleSuccess = FFmpegResponse.Success(Any())
			val scaleResult = executeFFmpegCommand(
				scaleCommand,
				scaleSuccess,
				{ progressCallback(getNonLinearProgress(stepsCount, index + 1, it)) },
				arrayOf(VideoDurationItem(videoItem.path, videoItem.speedMultiplier, videoItem.startTimeInMillis, videoItem.endTimeInMillis))
			)
			check(scaleResult is FFmpegResponse.Success<Any>) { "Unable to scale ${videoItem.path} to ${scaledItemsList[index].path}!" }
		}

		scaledItemsList.forEachIndexed { index, videoItem ->
			val transcodeToStreamCommand = getTransportStreamTranscodingToInterFileCommand(videoItem, intermediateFileNameList[index])
			val transcodeToStreamSuccess = FFmpegResponse.Success(Any())
			val transcodeResult = executeFFmpegCommand(
				transcodeToStreamCommand,
				transcodeToStreamSuccess,
				{ progressCallback(getNonLinearProgress(stepsCount, index + 1 + stepsScaleCount, it)) },
				arrayOf(VideoDurationItem(videoItem.path, videoItem.speedMultiplier, videoItem.startTimeInMillis, videoItem.endTimeInMillis))
			)
			check(transcodeResult is FFmpegResponse.Success<Any>) { "Unable to transcode ${videoItem.path} to stream!" }
		}

		val composeCommand = getConcatFromInterFileCommand(intermediateFileNameList, outputPath)
		val composeSuccess = FFmpegResponse.Success(VideoCompositionResult(outputPath))
		return executeFFmpegCommand(
			composeCommand,
			composeSuccess,
			{ progressCallback(getNonLinearProgress(stepsCount, stepsCount, it)) },
			inputItemsList.map { VideoDurationItem(it.path, it.speedMultiplier, it.startTimeInMillis, it.endTimeInMillis) }.toTypedArray()
		)
	}

	private fun getNonLinearProgress(stepsCount: Int, step: Int, localProgress: Float): Float {
		var ret = (localProgress + (step - 1)) / stepsCount
		//Use thresholds to eliminate random peaks in the local progress
		val thresholdMin: Float = (step - 1f) / stepsCount
		val thresholdMax: Float = step / stepsCount.toFloat()
		if (ret < thresholdMin) ret = thresholdMin
		if (ret > thresholdMax) ret = thresholdMax
		return ret
	}

	private fun getVideoDuration(videoPath: Array<VideoDurationItem>): Float {
		return videoPath.map {
			check(it.speedMultiplier != 0f) { "Speed multiplier must NOT be zero!" }
			val videoTime = if (it.endTimeInMillis != null && it.startTimeInMillis != null) {
				abs(it.endTimeInMillis - it.startTimeInMillis)
			} else {
				VideoUtils.getVideoDuration(it.path)
			}
			videoTime / it.speedMultiplier
		}.reduce { totalTime, oneItemTime -> totalTime + oneItemTime }
	}

	private fun <T> executeFFmpegCommand(
		ffmpegCommand: Array<String>,
		success: FFmpegResponse.Success<T>,
		progress: ((Float) -> Unit)? = null,
		durationArray: Array<VideoDurationItem> = emptyArray()
	): FFmpegResponse<T> {
		if (commandRunning) return FFmpegResponse.AlreadyRunning //Parallel execution is not supported
		synchronized(commandRunning) {
			commandRunning = true

			Config.resetStatistics()

			if (progress != null) {
				val videoDuration = getVideoDuration(durationArray)
				Config.enableStatisticsCallback {
					if (commandRunning) progress.invoke(it.time.toFloat() / videoDuration)
				}
			}
			//Log.e("FFMPEG:", ffmpegCommand.joinToString(separator = " "))
			val returnCode = FFmpeg.execute(ffmpegCommand)
			commandRunning = false

			Config.enableStatisticsCallback(null)

			return when {
				returnCode == 0 -> success
				returnCode == 255 -> FFmpegResponse.Cancelled
				returnCode < 0 -> FFmpegResponse.Error(FFmpegException(returnCode))
				else -> FFmpegResponse.Failure(IllegalStateException("Unexpected result code [$returnCode] from ffmpeg execution of command: ${ffmpegCommand.joinToString(separator = " ")}"))
			}
		}
	}
}

