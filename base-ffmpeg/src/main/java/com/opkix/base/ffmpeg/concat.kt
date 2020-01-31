package com.opkix.base.ffmpeg

import com.opkix.base.ffmpeg.model.CanvasItem
import com.opkix.base.ffmpeg.model.FFMPEGVideoEncoder
import com.opkix.base.ffmpeg.model.VideoCompositionItem
import com.opkix.base.media.VideoUtils.getVideoDimensions
import java.util.Locale

//Every input file could theoretically contains more video streams
//[0:v:0] trim=100.250:200.500, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480 [video00]; //input 0 stream 0
//[0:v:1] trim=100.250:200.500, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480 [video01]; //input 0 stream 1
private fun getFilterVideoStreamParams(inputIndex: Int, item: VideoCompositionItem, canvasItem: CanvasItem): String {
	//Passed translation is computed according to original video size
	val translationParams = TranslationParams(getVideoDimensions(item.path, item.rotateInDegreeClockwise), item.translationX, item.translationY)
	return item.videoStreamList.map { streamIndex ->
		"[$inputIndex:v:${streamIndex.index}] " +
			getVideoTrimFilter(item) +
			wrapMiddleFilterCommand(getVideoResetTimestampFilter()) +
			wrapMiddleFilterCommand(getVideoSpeedFilter(item.speedMultiplier)) +
			wrapMiddleFilterCommand(getRotateFilter(item.rotateInDegreeClockwise, ScaleType.FIT)) +
			wrapMiddleFilterCommand(getScaleToCanvasFilterParams(item.zoom, canvasItem, ScaleType.CROP, translationParams)) +
			wrapMiddleFilterCommand(getEffectFilterParams(item.effectType)) +
			" [video$inputIndex${streamIndex.index}];"
	}.reduce { composedVideoStreams, oneVideoStream -> composedVideoStreams.plus(oneVideoStream) }
}

//Every input file could theoretically contains more audio streams
// [0:a:0] atrim=100.250:200.500, asetpts=PTS-STARTPTS [audio00]; //input 0 stream 0
// [0:a:1] atrim=100.250:200.500, asetpts=PTS-STARTPTS [audio01]; //input 0 stream 1
private fun getFilterAudioStreamParams(inputIndex: Int, item: VideoCompositionItem): String {
	return item.audioStreamList.map { streamIndex ->
		"[$inputIndex:a:${streamIndex.index}] " +
			getAudioTrimFilter(item) +
			wrapMiddleFilterCommand(getAudioResetTimestampFilter()) +
			wrapMiddleFilterCommand(getAudioSpeedFilter(item.speedMultiplier)) +
			" [audio$inputIndex${streamIndex.index}];"
	}.reduce { composedAudioStreams, oneAudioStream -> composedAudioStreams.plus(oneAudioStream) }
}

private fun wrapMiddleFilterCommand(command: String): String {
	return if (command.isBlank()) "" else ", ${command}"
}

// [video00][video01][audio00][audio01]
// Note MJ - In case of more streams per input: Does the oder matter? Should they be grouped video+audio for every stream?
private fun getStreamReferences(inputIndex: Int, item: VideoCompositionItem): String {
	//[video00][video01]
	val videoStreamReferences = item.videoStreamList
		.map { videoStreamDefinition -> "[video$inputIndex${videoStreamDefinition.index}]" }
		.reduce { composedVideoStreams, oneVideoStream -> composedVideoStreams.plus(oneVideoStream) }
	//[audio00][audio01]
	val audioStreamReferences = item.audioStreamList
		.map { audioStreamDefinition -> "[audio$inputIndex${audioStreamDefinition.index}]" }
		.reduce { composedAudioStreams, oneAudioStream -> composedAudioStreams.plus(oneAudioStream) }

	return videoStreamReferences.plus(audioStreamReferences)
}

/**
 * Concat files with different codecs or different codec properties.
 * Do NOT use to concat more files with the same codec, since this function is the slowest way of concatenation!
 *
 * ffmpeg -y -loglevel warning
 * -i 5sec_640x480.mp4
 * -i 5sec_1920x1080.mp4
 * -i 5sec_720x1280.mp4
 * -filter_complex "
 * [0:v:0] trim=10.250:20.110, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480 [video00]
 * [1:v:0] trim=10:20, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480  [video10];
 * [2:v:0] trim=10:20, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480  [video20];
 * [0:a:0] atrim=10:20, asetpts=PTS-STARTPTS [audio00];
 * [1:a:0] atrim=10:20, asetpts=PTS-STARTPTS [audio10];
 * [2:a:0] anull [audio20];
 * [video00][audio00][video10][audio10][video20][audio20] concat=n=3:v=1:a=1 [v][a]
 * " -map "[v]" -map "[a]" -c:a aac -c:v h264 -crf 18 -preset veryfast -f mp4 output.mp4
 */
internal fun getConcatFilterScaleCommand(
	inputItemsList: List<VideoCompositionItem>,
	outputPath: String,
	requiredCanvas: CanvasItem
): Array<String> {

	val commandPrefixFlags = arrayOf("-y", "-loglevel", "warning")

	val commandInputFiles: Array<String> = inputItemsList.map { arrayOf("-i", it.path) }.reduce { composedInput, oneInput -> composedInput.plus(oneInput) }

	val commandInputStreamsPrefix = "-filter_complex"

	val commandVideoInputStreamDetails: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getFilterVideoStreamParams(itemIndex, videoCompositionItem, requiredCanvas) }
		.reduce { composedStreamCommand, oneFileVideoStreamCommand -> composedStreamCommand.plus(oneFileVideoStreamCommand) }

	val commandAudioInputStreamDetails: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getFilterAudioStreamParams(itemIndex, videoCompositionItem) }
		.reduce { composedStreamCommand, oneFileAudioStreamCommand -> composedStreamCommand.plus(oneFileAudioStreamCommand) }

	val commandStreamReferences: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getStreamReferences(itemIndex, videoCompositionItem) }
		.reduce { composedReferencesCommand, oneFileReferenceCommand -> composedReferencesCommand.plus(oneFileReferenceCommand) }

	//NOTE - there is currently hardcoded ONE audio and ONE video stream per segment
	val outputVideoStreamName = "outv"
	val outputAudioStreamName = "outa"
	val countOfAudioStreamsPerInputItem = 1
	val countOfVideoStreamsPerInputItem = 1
	val commandInputStreamsSummary = commandStreamReferences
		.plus("concat=n=${inputItemsList.size}:v=$countOfAudioStreamsPerInputItem:a=$countOfVideoStreamsPerInputItem [$outputVideoStreamName][$outputAudioStreamName]")

	val commandInputStreamsParams = commandVideoInputStreamDetails.plus(commandAudioInputStreamDetails).plus(commandInputStreamsSummary)

	val commandVideoCodecParams: Array<String> = arrayOf("-c:v",
		FFMPEGVideoEncoder.H264.FLAG,
		FFMPEGVideoEncoder.H264.RateControlMode.CRF.FLAG, FFMPEGVideoEncoder.H264.RateControlMode.CRF.VALUE_OPTIMAL,
		FFMPEGVideoEncoder.H264.RateControlMode.CRF.Preset.FLAG, FFMPEGVideoEncoder.H264.RateControlMode.CRF.Preset.VALUE_VERY_FAST)

	val commandAudioCodecParams: Array<String> = arrayOf("-c:a", "aac")
	val commandCodecParams = commandAudioCodecParams.plus(commandVideoCodecParams).plus(arrayOf("-f", "mp4"))
	val commandOutput: Array<String> = arrayOf("-map", "[$outputVideoStreamName]", "-map", "[$outputAudioStreamName]").plus(commandCodecParams).plus(outputPath)

	return commandPrefixFlags.plus(commandInputFiles).plus(commandInputStreamsPrefix).plus(commandInputStreamsParams).plus(commandOutput)
}

/**
 * Certain files (MPEG-2 transport streams, possibly others) can be concatenated.
 *
 * ffmpeg -i "concat:input1.ts|input2.ts|input3.ts" -c copy output.ts
 */
internal fun getDirectConcatProtocolCommand(
	inputItemsList: List<VideoCompositionItem>,
	outputPath: String,
	commonSuffix: String): Array<String> {

	check(commonSuffix.toLowerCase(Locale.ROOT) != "mp4") { "MP4 files cannot be concatenated directly!" }

	val commandInputFilesPrefix = arrayOf("-i")
	val inputPaths = inputItemsList.map { it.path }
		.reduce { composedInput, oneInput -> composedInput.plus("|$oneInput") }

	val commandInputFilesParams: String = "concat:".plus(inputPaths)
	val commandOutput = arrayOf("-c", "copy", outputPath)

	return commandInputFilesPrefix.plus(commandInputFilesParams).plus(commandOutput)
	//Log.e(">>>:", command.joinToString(separator = " "))
}

/**
 * ffmpeg -i input1.mp4 -c copy -bsf:v h264_mp4toannexb -f mpegts intermediate1.ts
 */
internal fun getTransportStreamTranscodingToInterFileCommand(videoItem: VideoCompositionItem, intermediateFileName: String): Array<String> {
	check(videoItem.path.toLowerCase(Locale.ROOT).endsWith(".mp4"))
	{ "Transcoding to MPEG-2 transport stream is implemented for MP4 files only! Currently requested file differs: ${videoItem.path}" }

	return arrayOf("-i", videoItem.path, "-c", "copy", "-bsf:v", "h264_mp4toannexb", "-f", "mpegts", intermediateFileName)
}

/**
 * ffmpeg -f mpegts -i "concat:temp1|temp2" -c copy -bsf:a aac_adtstoasc output.mp4
 */
internal fun getConcatFromInterFileCommand(intermediateFileNameList: List<String>, outputPath: String): Array<String> {
	check(outputPath.toLowerCase(Locale.ROOT).endsWith(".mp4"))
	{ "Transcoding to MPEG-2 transport stream is implemented for MP4 files only! Currently requested output file differs: $outputPath" }

	val concatParams: String = intermediateFileNameList
		.foldIndexed("concat:", { index, composedParams, oneParam -> composedParams.plus("${getSeparatorAfterIndexZero("|", index)}${oneParam}") })

	return arrayOf("-f", "mpegts", "-i", "$concatParams", "-c", "copy", "-bsf:a", "aac_adtstoasc", outputPath)
}

private fun getSeparatorAfterIndexZero(separator: String, index: Int): String = if (index > 0) separator else ""
