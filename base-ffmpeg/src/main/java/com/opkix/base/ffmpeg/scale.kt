package com.opkix.base.ffmpeg

import com.opkix.base.ffmpeg.model.CanvasItem
import com.opkix.base.media.model.VideoDimensions

enum class ScaleType() {
	FIT,
	CROP
}

data class TranslationParams(val translationRelatedDimensions: VideoDimensions, val translationX: Int, val translationY: Int)

/**
 * https://trac.ffmpeg.org/wiki/Scaling
 * https://ffmpeg.org/ffmpeg-filters.html#scale-1
 * http://ffmpeg.unixpin.com/2018/09/12/concatenation-videos-with-different-resolution-part-1-pad/
 * http://ffmpeg.unixpin.com/2018/09/17/concatenation-videos-with-different-resolution-part-2-crop/
 *
 * CROP
 * ffmpeg -i /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/1574249196109.mp4
 * -vf scale=w=max(iw*1080/ih\,1920):h=max(1080\,ih*1920/iw),
 * setsar=1,
 * crop=w=1920:h=1080 /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/1574249196109_scl.mp4
 *
 * FIT
 * ffmpeg -i /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/1574114019390.mp4
 * -vf scale=w=min(iw*1080/ih\,1920):h=min(1080\,ih*1920/iw),
 * pad=w=1920:h=1080:x=(1920-iw)/2:y=(1080-ih)/2 /storage/emulated/0/Android/data/com.opkix.android.develop/files/Projects/Project_1/1574114019390_scl.mp4
 *
 */
internal fun getScaleToCanvasStandaloneCommand(input: String, output: String, canvasItem: CanvasItem, scaleType: ScaleType, translation: TranslationParams): Array<String> {
	return arrayOf("-i", input, "-vf").plus(getScaleToCanvasFilterParams(0f, canvasItem, scaleType, translation)).plus(output)
	//Log.e(">>>${scaleType}:", command.joinToString(separator = " "))
}

internal fun getScaleToCanvasFilterParams(zoom: Float, canvasItem: CanvasItem, scaleType: ScaleType, translation: TranslationParams): String = when (scaleType) {
	ScaleType.FIT ->
		"scale=w=min(iw*${canvasItem.height}/ih\\,${canvasItem.width}):h=min(${canvasItem.height}\\,ih*${canvasItem.width}/iw), " +
			"pad=w=${canvasItem.width}:h=${canvasItem.height}:x=(${canvasItem.width}-iw)/2:y=(${canvasItem.height}-ih)/2"

	ScaleType.CROP -> {
		val translateXCommand = "(iw-ow)/2-((iw*${translation.translationX})/${translation.translationRelatedDimensions.width * zoom})"
		val translateYCommand = "(ih-oh)/2-((ih*${translation.translationY})/${translation.translationRelatedDimensions.height * zoom})"
		//Translation from params corresponds to original dimensions, so it has to be recomputed to scaled dimensions
		"scale=w=(${zoom}*max(iw*${canvasItem.height}/ih\\,${canvasItem.width})):h=(${zoom}*max(${canvasItem.height}\\,ih*${canvasItem.width}/iw)), " +
			"setsar=1, crop=w=${canvasItem.width}:h=${canvasItem.height}:x=$translateXCommand:y=$translateYCommand"
	}
}

internal fun getZoomAndMoveFilterParams(zoom: Float, translationX: Int, translationY: Int): String {
	//https://stackoverflow.com/questions/36221925/ffmpeg-scale-and-crop-in-single-command
	//https://video.stackexchange.com/questions/9947/how-do-i-change-frame-size-preserving-width-using-ffmpeg
	//ffmpeg -i input.mp4 -vf "scale=2*iw:-1, setsar=1, crop=iw/2:ih/2" output.mp4 -> 2x zoom
	val zoomFactor = zoom//0
	return if (zoomFactor > 0) {
		val centerXPosition = "(iw-ow)/2"
		val centerYPosition = "(ih-oh)/2"
		//right bottom quarter
		val xPosition = translationX//centerXPosition// right bottom part: "iw*2/3"
		val yPosition = translationY//centerYPosition //right bottom part: "ih*2/3"
		"scale=${zoomFactor}*iw:-1, setsar=1, crop=w=iw/${zoomFactor}:h=ih/${zoomFactor}:x=${xPosition}:y=${yPosition}"
	} else {
		""
	}
}


