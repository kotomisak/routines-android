package cz.kotox.core.arch

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.utility.FragmentPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import javax.inject.Inject

abstract class BasePermissionFragmentViewModel<V : BaseViewModel, B : ViewDataBinding>(
	@LayoutRes private val layoutResId: Int,
	navigationType: NavigationType = NavigationType.NONE
) : BaseFragmentViewModel<V, B>(layoutResId, navigationType) {

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		permissionManager.onPermissionResult(requestCode, permissions, grantResults)
	}

	//Use member injection to not complicated constructor-parameters/inheritance
	@Inject
	lateinit var permissionAppPreferences: AppPreferences

	private val permissionManager: FragmentPermissionManager by lazyUnsafe { FragmentPermissionManager(this, permissionAppPreferences) }

	fun checkAudioRecordingPermission(run: () -> Unit) {
		if (!permissionManager.checkRecordAudioPermission()) {
			permissionManager.requestRecordAudioPermissions(
				{ run.invoke() },
				{ run.invoke() },
				{ run.invoke() /* Do NOT show app setting dialog here, let NoMicQuestion screen handle this situation */ }
			)
		} else {
			run.invoke()
		}
	}

	fun checkStoragePermission(run: () -> Unit) {
		if (!permissionManager.checkStoragePermission()) {
			permissionManager.requestStoragePermissions(
				{ run.invoke() },
				{ run.invoke() },
				{ run.invoke() }
			)
		} else {
			run.invoke()
		}
	}

	fun checkLocationPermission(run: () -> Unit) {
		if (!permissionManager.checkLocationPermission()) {
			permissionManager.requestLocationPermissions(
				{ run.invoke() },
				{ run.invoke() },
				{ run.invoke() }
			)
		} else {
			run.invoke()
		}
	}
}