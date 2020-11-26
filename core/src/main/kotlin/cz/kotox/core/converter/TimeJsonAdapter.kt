package cz.kotox.core.converter

import cz.kotox.core.entity.SimpleTime
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import timber.log.Timber
import java.io.IOException

private const val SIMPLE_TIME_DELIMITER = ":"

class TimeJsonAdapter : JsonAdapter<SimpleTime>() {
	@Synchronized
	@Throws(IOException::class)
	override fun fromJson(reader: JsonReader): SimpleTime? {
		return parse(reader.nextString())
	}

	@Synchronized
	@Throws(IOException::class)
	override fun toJson(writer: JsonWriter, value: SimpleTime?) {
		writer.value(format(value))
	}

	@Throws(IOException::class)
	private fun format(time: SimpleTime?): String? =
		if (time != null) {
			"${"%02d".format(time.hours)}$SIMPLE_TIME_DELIMITER${"%02d".format(time.minutes)}$SIMPLE_TIME_DELIMITER${"%02d".format(time.seconds)}"
		} else {
			null
		}

	@Throws(IOException::class)
	private fun parse(time: String?): SimpleTime? {
		return if (time != null) {
			try {
				val parts = time.split(SIMPLE_TIME_DELIMITER)
				SimpleTime(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
			} catch (e: Exception) {
				Timber.e(e,"Unable to parse string $time to SimpleTime, expecting format 12${SIMPLE_TIME_DELIMITER}30${SIMPLE_TIME_DELIMITER}00")
				null
			}
		} else {
			null
		}
	}
}