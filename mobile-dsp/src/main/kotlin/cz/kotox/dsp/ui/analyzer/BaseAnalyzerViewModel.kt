package cz.kotox.dsp.ui.analyzer

import androidx.lifecycle.LifecycleObserver
import cz.kotox.core.arch.BaseViewModel

abstract class BaseAnalyzerViewModel : BaseViewModel(), LifecycleObserver {
	lateinit var mainViewModel: AnalyzerViewModel // is inserted after constructor
		internal set

}