package cz.kotox.core.entity.factory

import cz.kotox.core.entity.SimpleDate
import java.util.Calendar
import java.util.Date

fun getCalendarInstanceDateOnly(simpleDate: SimpleDate): Calendar {
	val calendarInstance = Calendar.getInstance()
	calendarInstance.clear()
	//According to SimpleDate.compareTo here can't be any increment for simpleDate.day (DAY_OF_MONTH). Just us it as it is. Do not shift like the month!
	calendarInstance.set(simpleDate.year, simpleDate.month - 1, simpleDate.day)////https://stackoverflow.com/questions/344380/why-is-january-month-0-in-java-calendar
	return calendarInstance
}

fun getSimpleDateInstance(date: Date): SimpleDate {
	val calendarInstance = Calendar.getInstance()
	calendarInstance.time = date
	////https://stackoverflow.com/questions/344380/why-is-january-month-0-in-java-calendar
	return SimpleDate(calendarInstance.get(Calendar.YEAR), calendarInstance.get(Calendar.MONTH) + 1, calendarInstance.get(Calendar.DATE))
}

fun getCalendarInstanceDateOnly(date: Date): Calendar {
	val calendarInstance = Calendar.getInstance()
	calendarInstance.time = date
	val otherYear = calendarInstance.get(Calendar.YEAR)
	val otherMonth = calendarInstance.get(Calendar.MONTH)
	val otherDay = calendarInstance.get(Calendar.DATE)
	calendarInstance.clear()
	calendarInstance.set(otherYear, otherMonth, otherDay)
	return calendarInstance
}