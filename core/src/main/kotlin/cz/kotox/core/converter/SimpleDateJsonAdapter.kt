package cz.kotox.core.converter

import cz.kotox.core.entity.SimpleDate
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import timber.log.Timber
import java.io.IOException

const val SIMPLE_DATE_DELIMITER = "-"

class SimpleDateJsonAdapter : JsonAdapter<SimpleDate>() {
	@Synchronized
	@Throws(IOException::class)
	override fun fromJson(reader: JsonReader): SimpleDate? {
		return parse(reader.nextString())
	}

	@Synchronized
	@Throws(IOException::class)
	override fun toJson(writer: JsonWriter, value: SimpleDate?) {
		writer.value(format(value))
	}

	@Throws(IOException::class)
	private fun format(date: SimpleDate?): String? =
		if (date != null) {
			"${"%04d".format(date.year)}$SIMPLE_DATE_DELIMITER${"%02d".format(date.month)}$SIMPLE_DATE_DELIMITER${"%02d".format(date.day)}"
		} else {
			null
		}

	@Throws(IOException::class)
	private fun parse(date: String?): SimpleDate? {
		return if (date != null) {
			try {
				val parts = date.split(SIMPLE_DATE_DELIMITER)
				SimpleDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
			} catch (e: Exception) {
				Timber.e(e,"Unable to parse string $date to SimpleDate, expecting format 2020${SIMPLE_DATE_DELIMITER}06${SIMPLE_DATE_DELIMITER}15")
				null
			}
		} else {
			null
		}
	}
}