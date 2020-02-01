package cz.kotox.dsp.ui

import androidx.lifecycle.MutableLiveData
import cz.kotox.core.PreferencesCore
import cz.kotox.core.arch.ObservableViewModel
import cz.kotox.core.entity.AppVersion
import javax.inject.Inject

class MainViewModel @Inject constructor(appVersion: AppVersion) : ObservableViewModel() {

//	@Inject
//	lateinit var appVersion: AppVersion

	@Inject
	lateinit var preferencesCore: PreferencesCore

	val token: MutableLiveData<String> = MutableLiveData()

	val appVersionString = "${appVersion.versionName} (${appVersion.versionCode})"

//	val appVersionString = preferencesCore.sampleToken

	init {
		token.value = "testicek"
	}

//	val token : String = "ddd"

//	fun getToken(): LiveData<String> {
//		val ret = MutableLiveData<String>()
//		ret.value = "..."
//		return ret
//	}

}