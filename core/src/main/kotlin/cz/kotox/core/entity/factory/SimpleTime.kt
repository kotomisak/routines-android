package cz.kotox.core.entity.factory

import cz.kotox.core.entity.SimpleTime

fun simpleTime(seconds: Int): SimpleTime {
	val hours = seconds.div(3600)
	val minutesMiddle = seconds.rem(3600)
	val minutes = minutesMiddle.div(60)
	val seconds = minutesMiddle.rem(60)
	return SimpleTime(hours, minutes, seconds)
}