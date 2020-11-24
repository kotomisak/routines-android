package cz.kotox.core.dsp.model

data class VoiceSample(val pitch: Float, val time: Double, val amplitude: Float, val frequency: Float) {
	fun isItemTheSame(item: VoiceSample): Boolean = item.time == this.time
	fun getTimeFormatted(): String = android.text.format.DateFormat.format("mm:ss.SSS", time.toLong()).toString()
}