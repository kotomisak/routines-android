package cz.kotox.core.utility

import android.content.res.Resources
import android.text.format.DateUtils
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private const val MINUTE_IN_SECONDS = DateUtils.MINUTE_IN_MILLIS / DateUtils.SECOND_IN_MILLIS

fun Float.roundToOneDecimalPlace(): Float {
	val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH)).apply {
		roundingMode = RoundingMode.HALF_UP
	}
	return df.format(this).toFloat()
}

fun Double.roundToOneDecimalPlace(): Double {
	val df = DecimalFormat("#.#", DecimalFormatSymbols(Locale.ENGLISH)).apply {
		roundingMode = RoundingMode.HALF_UP
	}
	return df.format(this).toDouble()
}

fun median(list: List<Float>) = list.sorted().let {
	(it[it.size / 2] + it[(it.size - 1) / 2]) / 2
}

fun roundSecToMin(seconds: Int) = if (seconds.rem(MINUTE_IN_SECONDS) >= MINUTE_IN_SECONDS / 2) {
	seconds.div(MINUTE_IN_SECONDS) + 1
} else {
	seconds.div(MINUTE_IN_SECONDS)
}

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()