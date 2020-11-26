package cz.kotox.core.utility

import android.content.Context
import cz.kotox.core.R
import cz.kotox.core.entity.SimpleTime

fun getAmPmSuffixString(hours: Int, amPlaceholder: String, pmPlaceholder: String): String =
	if (hours < 12) amPlaceholder else pmPlaceholder

fun getTimeString(context: Context, time: SimpleTime?, use24hourFormat: Boolean, useAmPmSuffix: Boolean): String =
	if (time == null) {
		context.getString(R.string.field_time_placeholder)
	} else {

		val hours = if (use24hourFormat || time.hours <= 12) time.hours else time.hours - 12

		val suffix = if (useAmPmSuffix) {
			getAmPmSuffixString(time.hours, context.getString(R.string.field_time_suffix_am), context.getString(R.string.field_time_suffix_pm))
		} else ""

		"$hours:${"%02d".format(time.minutes.rem(60))}$suffix"
	}