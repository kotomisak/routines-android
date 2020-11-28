package cz.kotox.core.ffmpeg.command

import cz.kotox.core.ffmpeg.model.CanvasItem
import cz.kotox.core.media.video.model.VideoDimensions

enum class ScaleType() {
	FIT,
	CROP
}

data class TranslationParams(val translationRelatedDimensions: VideoDimensions, val translationX: Float, val translationY: Float)

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


