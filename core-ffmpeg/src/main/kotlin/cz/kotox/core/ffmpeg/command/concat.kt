package cz.kotox.core.ffmpeg.command

import cz.kotox.core.ffmpeg.model.BackgroundMusicItem
import cz.kotox.core.ffmpeg.model.CanvasItem
import cz.kotox.core.ffmpeg.model.FFMPEGVideoEncoder
import cz.kotox.core.ffmpeg.model.VideoCompositionItem
import cz.kotox.core.media.video.VideoUtils.getVideoDimensions
import java.io.File

private const val VIDEO_REFERENCE = "video"
private const val VIDEO_WITH_TEXT_REFERENCE = "videotext"
private const val AUDIO_REFERENCE = "audio"
private const val VIDEO_TEXT_OVERLAY_REFERENCE = "text"

//Every input file could theoretically contains more video streams
//[0:v:0] trim=100.250:200.500, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480 [video00]; //input 0 stream 0
//[0:v:1] trim=100.250:200.500, setpts=PTS-STARTPTS, scale=w=max(iw*480/ih\,640):h=max(480\,ih*640/iw), setsar=1, crop=w=640:h=480 [video01]; //input 0 stream 1
private fun getFilterVideoStreamParams(inputIndex: Int, item: VideoCompositionItem, canvasItem: CanvasItem): String {

	//Check fonts if they exists.
	item.textItemList.forEach { check(File(it.fontPath).exists()) { "${it.fontPath} NOT FOUND!" } }

	//Passed translation is computed according to original video size
	val translationParams = TranslationParams(getVideoDimensions(item.videoPath, item.rotateInDegreeClockwise), item.translationX, item.translationY)
	return item.videoStreamList.map { streamIndex ->
		"[$inputIndex:v:${streamIndex.index}] " +
			getVideoTrimFilter(item) +
			wrapMiddleFilterCommand(getVideoResetTimestampFilter()) +
			wrapMiddleFilterCommand(getVideoSpeedFilter(item.speedMultiplier)) +
			wrapMiddleFilterCommand(getRotateFilter(item.rotateInDegreeClockwise, ScaleType.FIT)) +
			wrapMiddleFilterCommand(getScaleToCanvasFilterParams(item.zoom, canvasItem, ScaleType.CROP, translationParams)) +
			wrapMiddleFilterCommand(getEffectFilterParams(item.effectType)) +
			wrapMiddleFilterCommand(getTextFilterParams(item.textItemList)) + //Keep text params after scale to operate on scaled dimensions
			" [$VIDEO_REFERENCE$inputIndex${streamIndex.index}];"
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
			wrapMiddleFilterCommand(getVolumeFilter(item.volumeMultiplier)) +
			" [$AUDIO_REFERENCE$inputIndex${streamIndex.index}];"
	}.reduce { composedAudioStreams, oneAudioStream -> composedAudioStreams.plus(oneAudioStream) }
}

/**
 * movie=bitmap_overlay.png[text10];[video10][text10]overlay=x=0:y=0, setpts=PTS-STARTPTS[videotext10];
 */
private fun getFilterTextOverlayParams(inputIndex: Int, item: VideoCompositionItem): String {
	return if (item.videoOverlayPath == null) "" else
		return item.videoStreamList.map { streamIndex ->
			"movie=${item.videoOverlayPath}[$VIDEO_TEXT_OVERLAY_REFERENCE$inputIndex${streamIndex.index}];" +
				"[$VIDEO_REFERENCE$inputIndex${streamIndex.index}][$VIDEO_TEXT_OVERLAY_REFERENCE$inputIndex${streamIndex.index}]overlay=x=0:y=0, setpts=PTS-STARTPTS[$VIDEO_WITH_TEXT_REFERENCE$inputIndex${streamIndex.index}];"
		}.reduce { composedVideoStreams, oneVideoStream -> composedVideoStreams.plus(oneVideoStream) }
}

/**
 * [2:a:0] aloop=loop=-1, volume=0.5[outm]
 */
private fun getFilterMusicStreamParams(musicItem: BackgroundMusicItem?, outputMusicStreamName: String): String {
	val infiniteLoop = "loop=0"
	return if (musicItem == null) "" else "amovie=${musicItem.path}:${infiniteLoop}, asetpts=PTS-STARTPTS, volume=${musicItem.volumeMultiplier}[$outputMusicStreamName];"
}

private fun wrapMiddleFilterCommand(command: String): String {
	return if (command.isBlank()) "" else ", ${command}"
}

// [video00][video01][audio00][audio01]
// Note MJ - In case of more streams per input: Does the oder matter? Should they be grouped video+audio for every stream?
private fun getStreamReferences(inputIndex: Int, item: VideoCompositionItem): String {

	val videoReference = if (item.videoOverlayPath == null) VIDEO_REFERENCE else VIDEO_WITH_TEXT_REFERENCE

	//[video00][video01] or [videotext00][videotext01]
	val videoStreamReferences = item.videoStreamList
		.map { videoStreamDefinition -> "[$videoReference$inputIndex${videoStreamDefinition.index}]" }
		.reduce { composedVideoStreams, oneVideoStream -> composedVideoStreams.plus(oneVideoStream) }
	//[audio00][audio01]
	val audioStreamReferences = item.audioStreamList
		.map { audioStreamDefinition -> "[$AUDIO_REFERENCE$inputIndex${audioStreamDefinition.index}]" }
		.reduce { composedAudioStreams, oneAudioStream -> composedAudioStreams.plus(oneAudioStream) }

	return videoStreamReferences.plus(audioStreamReferences)
}

/**
 *
 * -y -loglevel warning
 * -i /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/VID_20191224_101322_1579035419077.mp4
 * -i /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/VID_20200103_152551_1579035419346.mp4
 * -i /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/VID_20191229_083059_1579035419802.mp4
 * -pix_fmt yuv420p -filter_complex
 * [0:v:0] trim=0.0:20.624, setpts=PTS-STARTPTS, scale=w=(1.0*max(iw*1080/ih\,1920)):h=(1.0*max(1080\,ih*1920/iw)), setsar=1, crop=w=1920:h=1080:x=(iw-ow)/2-((iw*0.0)/1920.0):y=(ih-oh)/2-((ih*0.0)/1080.0) [video00];
 * [1:v:0] trim=0.0:21.625, setpts=PTS-STARTPTS, scale=w=(1.0*max(iw*1080/ih\,1920)):h=(1.0*max(1080\,ih*1920/iw)), setsar=1, crop=w=1920:h=1080:x=(iw-ow)/2-((iw*0.0)/1920.0):y=(ih-oh)/2-((ih*0.0)/1080.0) [video10];
 * [2:v:0] trim=0.0:26.30, setpts=PTS-STARTPTS, scale=w=(1.0*max(iw*1080/ih\,1920)):h=(1.0*max(1080\,ih*1920/iw)), setsar=1, crop=w=1920:h=1080:x=(iw-ow)/2-((iw*0.0)/1920.0):y=(ih-oh)/2-((ih*0.0)/1080.0) [video20];
 * movie=/data/user/0/com.opkix.android.develop/cache/opkix_cache/text_bitmap_1_1579089962498_-1770103187.png[text00];
 * [video00][text00]overlay=x=0:y=0, setpts=PTS-STARTPTS[videotext00];
 * movie=/data/user/0/com.opkix.android.develop/cache/opkix_cache/text_bitmap_1_1579089962498_-1406561475.png[text10];
 * [video10][text10]overlay=x=0:y=0, setpts=PTS-STARTPTS[videotext10];
 * [0:a:0] atrim=0.0:20.624, asetpts=PTS-STARTPTS, volume=1.0 [audio00];
 * [1:a:0] atrim=0.0:21.625, asetpts=PTS-STARTPTS, volume=1.0 [audio10];
 * [2:a:0] atrim=0.0:26.30, asetpts=PTS-STARTPTS, volume=1.0 [audio20];
 * amovie=/storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/custom_music_1579079782900.mp3:loop=0, asetpts=PTS-STARTPTS, volume=0.2[outm];
 * [videotext00][audio00][videotext10][audio10][video20][audio20]concat=n=3:v=1:a=1 [outv][outav];
 * [outav][outm]amix=duration=first, asetpts=PTS-STARTPTS[outa]
 * -map [outv] -map [outa] -c:a aac -c:v h264 -crf 18 -preset veryfast -f mp4 /storage/emulated/0/Movies/Opkix Develop/Projects/text_1579089962498.mp4
 *
 */
internal fun getConcatFilterScaleCommand(
	inputItemsList: List<VideoCompositionItem>,
	inputBackgroundMusic: BackgroundMusicItem?,
	outputPath: String,
	requiredCanvas: CanvasItem
): Array<String> {

	val pixelFormatFlag = arrayOf("-pix_fmt", "yuv420p") //Use pixelFormatFlag at least with usage of colorchannelmixer, otherwise output will not be playable everywhere.

	val commandPrefixFlags = arrayOf("-y", "-loglevel", "warning")

	val commandInputFiles: Array<String> = inputItemsList.map { arrayOf("-i", it.videoPath) }.reduce { composedInput, oneInput -> composedInput.plus(oneInput) }

	val commandInputStreamsPrefix = "-filter_complex"

	val commandVideoInputStreamDetails: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getFilterVideoStreamParams(itemIndex, videoCompositionItem, requiredCanvas) }
		.reduce { composedStreamCommand, oneFileVideoStreamCommand -> composedStreamCommand.plus(oneFileVideoStreamCommand) }

	val commandVideoTextOverlayStreamDetails: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getFilterTextOverlayParams(itemIndex, videoCompositionItem) }
		.reduce { composedStreamCommand, oneFileVideoStreamCommand -> composedStreamCommand.plus(oneFileVideoStreamCommand) }

	val commandAudioInputStreamDetails: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getFilterAudioStreamParams(itemIndex, videoCompositionItem) }
		.reduce { composedStreamCommand, oneFileAudioStreamCommand -> composedStreamCommand.plus(oneFileAudioStreamCommand) }

	val commandStreamReferences: String = inputItemsList
		.mapIndexed { itemIndex, videoCompositionItem -> getStreamReferences(itemIndex, videoCompositionItem) }
		.reduce { composedReferencesCommand, oneFileReferenceCommand -> composedReferencesCommand.plus(oneFileReferenceCommand) }

	val outputVideoStreamName = "outv"
	val outputAudioVideoStreamName = "outav"
	//NOTE - there is currently hardcoded ONE audio and ONE video stream per segment
	val countOfAudioStreamsPerInputItem = 1
	val countOfVideoStreamsPerInputItem = 1

	val outputAudioStream: AudioStreamParams = getAudioStreamParams(inputBackgroundMusic, outputAudioVideoStreamName)

	val concatClips = "concat=n=${inputItemsList.size}:v=$countOfAudioStreamsPerInputItem:a=${countOfVideoStreamsPerInputItem} [$outputVideoStreamName][$outputAudioVideoStreamName]"

	val commandInputStreamsSummary = commandStreamReferences
		.plus("$concatClips${outputAudioStream.audioStreamMergeCommand}") //Do not place any space in between filter commands, keep just semicolon as separator!

	val commandInputStreamsParams = commandVideoInputStreamDetails
		.plus(commandVideoTextOverlayStreamDetails)
		.plus(commandAudioInputStreamDetails)
		.plus(outputAudioStream.musicStreamProcessingDetails)
		.plus(commandInputStreamsSummary)

	val commandVideoCodecParams: Array<String> = arrayOf("-c:v",
		FFMPEGVideoEncoder.H264.FLAG,
		FFMPEGVideoEncoder.H264.RateControlMode.CRF.FLAG, FFMPEGVideoEncoder.H264.RateControlMode.CRF.VALUE_OPTIMAL,
		FFMPEGVideoEncoder.H264.RateControlMode.CRF.Preset.FLAG, FFMPEGVideoEncoder.H264.RateControlMode.CRF.Preset.VALUE_VERY_FAST)

	val commandAudioCodecParams: Array<String> = arrayOf("-c:a", "aac")
	val commandCodecParams = commandAudioCodecParams.plus(commandVideoCodecParams).plus(arrayOf("-f", "mp4"))
	val commandOutput: Array<String> = arrayOf("-map", "[$outputVideoStreamName]", "-map", "[${outputAudioStream.finalAudioStreamName}]").plus(commandCodecParams).plus(outputPath)

	return commandPrefixFlags.plus(commandInputFiles).plus(pixelFormatFlag).plus(commandInputStreamsPrefix).plus(commandInputStreamsParams).plus(commandOutput)
}

data class AudioStreamParams(
	val finalAudioStreamName: String,
	val audioStreamMergeCommand: String,
	val musicStreamProcessingDetails: String)

private fun getAudioStreamParams(
	inputBackgroundMusic: BackgroundMusicItem?,
	outputAudioVideoStreamName: String
): AudioStreamParams {
	val outputAudioStreamName = "outa"
	val outputMusicStreamName = "outm"
	val commandBackgroundAudioStreamDetails = getFilterMusicStreamParams(inputBackgroundMusic, outputMusicStreamName)
	//TODO MJ - when amix would decrease volume, think about dynaudrom:https://stackoverflow.com/questions/35509147/ffmpeg-amix-filter-volume-issue-with-inputs-of-different-duration/38714779#38714779
	val mergeAudioCommand = if (inputBackgroundMusic != null) ";[$outputAudioVideoStreamName][$outputMusicStreamName]amix=duration=first, asetpts=PTS-STARTPTS[$outputAudioStreamName]" else ""
	val finalAudioStreamName = if (inputBackgroundMusic != null) outputAudioStreamName else outputAudioVideoStreamName

	return AudioStreamParams(finalAudioStreamName, mergeAudioCommand, commandBackgroundAudioStreamDetails)
}