package cz.kotox.dsp.ui.analyzer

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.anand.brose.graphviewlibrary.WaveSample
import cz.kotox.core.PreferencesCore
import cz.kotox.core.arch.BaseViewModel
import cz.kotox.core.dsp.model.VoiceSample
import cz.kotox.core.entity.AppVersion
import timber.log.Timber
import javax.inject.Inject

class AnalyzerViewModel @Inject constructor(appVersion: AppVersion) : BaseViewModel(), LifecycleObserver {

	val pitchList = mutableListOf<VoiceSample>()
}