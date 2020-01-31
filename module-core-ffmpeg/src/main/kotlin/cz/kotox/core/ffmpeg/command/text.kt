package cz.kotox.core.ffmpeg.command

import com.opkix.base.ffmpeg.model.VideoTextAlignment
import com.opkix.base.ffmpeg.model.VideoTextItem

internal fun getTextFilterParams(textItemList: List<VideoTextItem>): String {

	if (textItemList.isEmpty()) return ""

	val drawTextFilterCommands = StringBuilder()
	textItemList.forEach { textItem ->
		getLinesBySeparator(textItem.text).forEachIndexed { lineIndex, line ->
			if (drawTextFilterCommands.isNotBlank()) {
				drawTextFilterCommands.append(",")
			}
			drawTextFilterCommands.append("drawtext=fontfile=${textItem.fontPath}:fontsize=${textItem.fontSize}:fontcolor=${textItem.fontColorHex}:${getTranslationX(textItem)}:${getBaselineTranslationY(textItem, lineIndex)}:text=${line}")
		}
	}
	return drawTextFilterCommands.toString()
}

private fun getBaselineTranslationY(textItem: VideoTextItem, textLineIndex: Int): String =
	"y=(${textItem.translationY}+${textItem.textMeasurement.lineBaselineList[textLineIndex]}-ascent)"

private fun getTranslationX(textItem: VideoTextItem): String =
	when (textItem.textAlignment) {
		VideoTextAlignment.LEFT -> "x=(${textItem.translationX})"
		VideoTextAlignment.RIGHT -> "x=(${textItem.translationX}+${textItem.textMeasurement.textBoxWidth}-tw)"
		VideoTextAlignment.CENTER -> "x=(${textItem.translationX}+(${textItem.textMeasurement.textBoxWidth}-tw)/2)"
	}

private fun getLinesBySeparator(text: String): List<String> =
	text.split(System.getProperty("line.separator")
		?: throw java.lang.IllegalStateException("Unable to get system property: line.separator"))

