package cz.kotox.core.dsp


import cz.kotox.core.analytics.BaseEventTracker
import cz.kotox.core.analytics.FirebaseEventsConsumer
import cz.kotox.core.analytics.PARAMETER_VALUE_LIMIT
import javax.inject.Inject
import javax.inject.Singleton

private const val DSP_INFO = "dsp_info"
private const val DSP_MIC_INFO = "microphone_info"

@Singleton
class DspEventTracker @Inject constructor(
	firebase: FirebaseEventsConsumer
) : BaseEventTracker(firebase) {

	fun logMicInfo(micInfo: String) = trackActionWithParams(DSP_INFO, mapOf(DSP_MIC_INFO to micInfo.take(PARAMETER_VALUE_LIMIT)))

}