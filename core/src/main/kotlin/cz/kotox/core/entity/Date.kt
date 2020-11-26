package cz.kotox.core.entity

import android.os.Parcelable
import androidx.annotation.Keep
import cz.kotox.core.entity.factory.getCalendarInstanceDateOnly
import kotlinx.android.parcel.Parcelize
import java.util.Date

private const val SIMPLE_DATE_DELIMITER = "-"

/**
 * Year,
 * Month, 1-12
 * Day,
 */
@Keep
@Parcelize
data class SimpleDate(
	val year: Int,
	val month: Int,
	val day: Int = 0
) : Parcelable {

	override fun toString(): String {
		return "${"%04d".format(year)}$SIMPLE_DATE_DELIMITER${"%02d".format(month)}$SIMPLE_DATE_DELIMITER${"%02d".format(day)}"
	}

	/**
	 * Compare Year/Month/Day only.
	 *
	 * same day -> 0
	 * before otherDate -> -
	 * after otherDate -> +
	 */
	fun compareTo(otherDate: SimpleDate): Int {
		val otherInstance = getCalendarInstanceDateOnly(otherDate)
		val currentInstance = getCalendarInstanceDateOnly(this)
		return currentInstance.compareTo(otherInstance)
	}

	/**
	 * Compare Year/Month/Day only.
	 *
	 * same day -> 0
	 * before otherDate -> -
	 * after otherDate -> +
	 */
	fun compareTo(otherDate: Date): Int {
		val otherInstance = getCalendarInstanceDateOnly(otherDate)
		val currentInstance = getCalendarInstanceDateOnly(this)
		return currentInstance.compareTo(otherInstance)
	}
}