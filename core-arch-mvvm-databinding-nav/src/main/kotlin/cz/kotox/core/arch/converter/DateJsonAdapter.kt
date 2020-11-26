package cz.kotox.core.arch.converter

import android.annotation.SuppressLint
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

private const val DATE_PATTERN_TO_JSON = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
const val DATE_PATTERN_FROM_JSON = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

@SuppressLint("SimpleDateFormat")
class DateJsonAdapter : JsonAdapter<Date>() {

	@Synchronized
	@Throws(IOException::class)
	override fun fromJson(reader: JsonReader): Date? {
		return parse(reader.nextString())
	}

	@Synchronized
	@Throws(IOException::class)
	override fun toJson(writer: JsonWriter, value: Date?) {
		writer.value(format(value))
	}

	@Throws(IOException::class)
	private fun format(date: Date?): String? =
		if (date != null) {
			SimpleDateFormat(DATE_PATTERN_TO_JSON).run { format(date) }
		} else {
			null
		}

	@Throws(IOException::class)
	private fun parse(date: String?): Date? {
		return if (date != null) {
			try {
				SimpleDateFormat(DATE_PATTERN_FROM_JSON)
					.apply { timeZone = TimeZone.getTimeZone("UTC") }
					.run { parse(date) }
			} catch (e: Exception) {
				Timber.e(e, "Unable to parse date from string: $date")
				null
			}
		} else {
			null
		}
	}

}