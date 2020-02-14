package cz.kotox.template.ui.wizard.first

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleObserver
import androidx.navigation.Navigation
import cz.kotox.core.utility.FragmentPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import cz.kotox.template.R
import cz.kotox.template.databinding.WizardFirstFragmentBinding
import cz.kotox.template.ui.wizard.BaseWizardFragment
import cz.kotox.template.ui.wizard.BaseWizardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class WizardFirstFragment : BaseWizardFragment<WizardFirstViewModel, WizardFirstFragmentBinding>() {

	val permissionManager: FragmentPermissionManager by lazyUnsafe {
		FragmentPermissionManager(this)
	}

	override fun inflateBindingLayout(inflater: LayoutInflater) = WizardFirstFragmentBinding.inflate(inflater)

	override fun setupWizardViewModel() = findViewModel<WizardFirstViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
		verifyPermissions()
	}

	override fun onResume() {
		super.onResume()
		verifyPermissions()
	}

	private fun verifyPermissions() {
		if (!permissionManager.checkRecordAudioPermission()) {
			permissionManager.requestRecordAudioPermissions({}, {
				showToast("RecordAudio permissions is required for audio analysis")
				activity?.finish()
			})
		}
	}

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.navigateProcessingBt.setOnClickListener {
			Navigation.findNavController(view).navigate(R.id.navigate_to_processing)
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		permissionManager.onPermissionResult(requestCode, permissions, grantResults)
	}
}

class WizardFirstViewModel @Inject constructor() : BaseWizardViewModel(), LifecycleObserver {

}


