package cz.kotox.core.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

private const val SIMPLE_TIME_DELIMITER = ":"

@Parcelize
data class SimpleTime(
	val hours: Int,
	val minutes: Int,
	val seconds: Int = 0
) : Parcelable, Comparable<SimpleTime> {
	override fun compareTo(other: SimpleTime): Int {

		if (this.hours < other.hours) {
			return -1
		} else {
			if (this.minutes < other.minutes) {
				return -1
			} else {
				if (this.seconds < other.seconds) {
					return -1
				}
			}
		}

		if ((this.hours == other.hours) &&
			(this.minutes == other.minutes) &&
			(this.seconds == other.seconds)) {
			return 0
		}
		return 1
	}

	fun getTimeInMinutes(): Int = this.hours * 60 + this.minutes
	fun getTimeInSeconds(): Int = this.hours * 3600 + this.minutes * 60 + this.seconds

	override fun toString(): String {
		return "${"%02d".format(hours)}$SIMPLE_TIME_DELIMITER${"%02d".format(minutes)}$SIMPLE_TIME_DELIMITER${"%02d".format(seconds)}"
	}

}