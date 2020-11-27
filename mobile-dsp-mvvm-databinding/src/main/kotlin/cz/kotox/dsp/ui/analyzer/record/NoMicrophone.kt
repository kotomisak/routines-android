package cz.kotox.dsp.ui.analyzer.record

import androidx.fragment.app.viewModels
import cz.kotox.core.arch.NavigationType
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.utility.FragmentPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import cz.kotox.dsp.R
import cz.kotox.dsp.databinding.NoMicrophoneFragmentBinding
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerFragment
import cz.kotox.dsp.ui.analyzer.BaseAnalyzerViewModel
import javax.inject.Inject

interface NoMicrophoneView {
	fun allowMicAccess()
}

class NoMicFragment @Inject constructor() : BaseAnalyzerFragment<NoMicrophoneViewModel, NoMicrophoneFragmentBinding>(
		R.layout.no_microphone_fragment,
		NavigationType.CLOSE
), NoMicrophoneView {

	private val permissionManager: FragmentPermissionManager by lazyUnsafe { FragmentPermissionManager(this, appPreferences) }

	override val viewModel: NoMicrophoneViewModel by viewModels()

	override fun allowMicAccess() {

		// No need to handle granted/denied result here, because onResume in Activity is called. It's handled there.
		permissionManager.requestRecordAudioPermissions({ }, { }, {
			showPermissionSettingsDialog(this, parentFragmentManager, R.string.no_mic_permission_denied_message)
		})

	}
}

class NoMicrophoneViewModel @Inject constructor() : BaseAnalyzerViewModel()