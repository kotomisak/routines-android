package cz.kotox.template.model

data class ValueSample(val pitch: Float, val time: Double, val amplitude: Float, val frequency: Float) {
	fun isItemTheSame(item: ValueSample): Boolean = item.time == this.time
	fun getTimeFormatted(): String = android.text.format.DateFormat.format("mm:ss.SSS", time.toLong()).toString()
}