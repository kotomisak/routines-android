package cz.kotox.core.utility

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cz.kotox.core.database.AppPreferences

abstract class PermissionManager(open val appPreferences: AppPreferences) {
	private var lastRequestId = 0
	private val permissionRequests = HashMap<Int, PermissionRequest>()
	fun requestPermission(permissionRequest: PermissionRequest) {
		val requestId = ++lastRequestId
		permissionRequests[requestId] = permissionRequest
		askForPermissions(permissionRequest.permissions, requestId)
	}

	fun onPermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		val permissionRequest = permissionRequests[requestCode]
		permissionRequest?.let {
			val granted = ArrayList<String>()
			val denied = ArrayList<String>()

			permissions.indices.forEach {
				if (grantResults[it] == PackageManager.PERMISSION_GRANTED)
					granted.add(permissions[it])
				else
					denied.add(permissions[it])
			}

			if (granted.isNotEmpty())
				permissionRequest.grantedCallback.invoke(granted)
			if (denied.isNotEmpty())
				permissionRequest.deniedCallback.invoke(denied)

			permissionRequests.remove(requestCode)
		}
	}

	fun checkPermission(permission: String) = performPermissionCheck(permission) == PackageManager.PERMISSION_GRANTED

	abstract fun performPermissionCheck(permission: String): Int

	abstract fun askForPermissions(permissions: List<String>, requestId: Int)

	fun checkLocationPermission() = checkPermission(ACCESS_FINE_LOCATION)

	fun checkStoragePermission() = checkPermission(WRITE_EXTERNAL_STORAGE)

	fun checkCameraPermission() = checkPermission(CAMERA)

	fun checkRecordAudioPermission() = checkPermission(RECORD_AUDIO)


	fun requestLocationPermissions(grantedCallback: (grantedPermissions: String) -> Unit = {}, deniedCallback: (deniedPermissions: String) -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!checkPermission(ACCESS_FINE_LOCATION)) {
			//Permission not granted
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), ACCESS_FINE_LOCATION)) {
				//Can ask user for permission
				requestPermission(SinglePermissionRequest(ACCESS_FINE_LOCATION, grantedCallback, deniedCallback))
			} else {
				if (appPreferences.wasPermissionRequested(ACCESS_FINE_LOCATION)) {
					//If User was asked permission before and denied
					showAppSettingsCallback.invoke()
				} else {
					//If user is asked permission for first time
					requestPermission(SinglePermissionRequest(ACCESS_FINE_LOCATION, grantedCallback, deniedCallback))
					appPreferences.markPermissionRequested(ACCESS_FINE_LOCATION)
				}
			}
		} else {
			appPreferences.permissionAccepted(ACCESS_FINE_LOCATION)
			grantedCallback.invoke(ACCESS_FINE_LOCATION)
		}
	}

	fun checkLocationPermissions(grantedCallback: () -> Unit = {}, notGrantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}) {
		if (!checkPermission(ACCESS_FINE_LOCATION)) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), ACCESS_FINE_LOCATION)) {
				notGrantedCallback.invoke()
			} else {
				if (appPreferences.wasPermissionRequested(ACCESS_FINE_LOCATION)) {
					deniedCallback.invoke()
				} else {
					notGrantedCallback.invoke()
				}
			}
		} else {
			grantedCallback.invoke()
		}
	}

	fun checkStoragePermission(grantedCallback: () -> Unit = {}, notGrantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}) {
		checkPermissions(WRITE_EXTERNAL_STORAGE, grantedCallback, notGrantedCallback, deniedCallback)
	}

	fun checkCameraPermission(grantedCallback: () -> Unit = {}, notGrantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}) {
		checkPermissions(CAMERA, grantedCallback, notGrantedCallback, deniedCallback)
	}

	private fun checkPermissions(permission: String, grantedCallback: () -> Unit = {}, notGrantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}) {
		if (!checkPermission(permission)) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
				notGrantedCallback.invoke()
			} else {
				if (appPreferences.wasPermissionRequested(permission)) {
					deniedCallback.invoke()
				} else {
					notGrantedCallback.invoke()
				}
			}
		} else {
			grantedCallback.invoke()
		}
	}

	fun requestStoragePermissions(grantedCallback: (grantedPermissions: String) -> Unit = {}, deniedCallback: (deniedPermissions: String) -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!checkPermission(WRITE_EXTERNAL_STORAGE)) {
			//Permission not granted
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				//Can ask user for permission
				requestPermission(SinglePermissionRequest(WRITE_EXTERNAL_STORAGE, grantedCallback, deniedCallback))
			} else {
				if (appPreferences.wasPermissionRequested(WRITE_EXTERNAL_STORAGE)) {
					//If User was asked permission before and denied
					showAppSettingsCallback.invoke()
				} else {
					//If user is asked permission for first time
					requestPermission(SinglePermissionRequest(WRITE_EXTERNAL_STORAGE, grantedCallback, deniedCallback))
					appPreferences.markPermissionRequested(WRITE_EXTERNAL_STORAGE)
				}
			}
		} else {
			appPreferences.permissionAccepted(WRITE_EXTERNAL_STORAGE)
			grantedCallback.invoke(WRITE_EXTERNAL_STORAGE)
		}
	}

	fun requestRecordAudioPermissions(grantedCallback: (grantedPermissions: String) -> Unit = {}, deniedCallback: (deniedPermissions: String) -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!checkPermission(RECORD_AUDIO)) {
			//Permission not granted
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.RECORD_AUDIO)) {
				//Can ask user for permission
				requestPermission(SinglePermissionRequest(RECORD_AUDIO, grantedCallback, deniedCallback))
			} else {
				if (appPreferences.wasPermissionRequested(RECORD_AUDIO)) {
					//If User was asked permission before and denied
					showAppSettingsCallback.invoke()
				} else {
					//If user is asked permission for first time
					requestPermission(SinglePermissionRequest(RECORD_AUDIO, grantedCallback, deniedCallback))
					appPreferences.markPermissionRequested(RECORD_AUDIO)
				}
			}
		} else {
			appPreferences.permissionAccepted(RECORD_AUDIO)
			grantedCallback.invoke(RECORD_AUDIO)
		}
	}

	fun requestCameraPermissions(grantedCallback: (grantedPermissions: String) -> Unit = {}, deniedCallback: (deniedPermissions: String) -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!checkPermission(CAMERA)) {
			//Permission not granted
			if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
				//Can ask user for permission
				requestPermission(SinglePermissionRequest(CAMERA, grantedCallback, deniedCallback))
			} else {
				if (appPreferences.wasPermissionRequested(CAMERA)) {
					//If User was asked permission before and denied
					showAppSettingsCallback.invoke()
				} else {
					//If user is asked permission for first time
					requestPermission(SinglePermissionRequest(CAMERA, grantedCallback, deniedCallback))
					appPreferences.markPermissionRequested(CAMERA)
				}
			}
		} else {
			appPreferences.permissionAccepted(CAMERA)
			grantedCallback.invoke(CAMERA)
		}
	}


	abstract fun getActivity(): Activity
}

class ActivityPermissionManager(val activity: FragmentActivity, override val appPreferences: AppPreferences) : PermissionManager(appPreferences) {
	override fun performPermissionCheck(permission: String) = ContextCompat.checkSelfPermission(activity, permission)

	override fun askForPermissions(permissions: List<String>, requestId: Int) {
		ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestId)
	}

	override fun getActivity(): Activity = activity
}

class FragmentPermissionManager(val fragment: Fragment, override val appPreferences: AppPreferences) : PermissionManager(appPreferences) {
	override fun performPermissionCheck(permission: String) = ContextCompat.checkSelfPermission(fragment.requireActivity(), permission)

	override fun askForPermissions(permissions: List<String>, requestId: Int) {
		fragment.requestPermissions(permissions.toTypedArray(), requestId)
	}

	override fun getActivity(): Activity = fragment.requireActivity()
}

open class PermissionRequest(
	val permissions: List<String>,
	val grantedCallback: (grantedPermissions: List<String>) -> Unit = {},
	val deniedCallback: (deniedPermissions: List<String>) -> Unit = {})

class SinglePermissionRequest(
	permission: String,
	grantedCallback: (grantedPermission: String) -> Unit = {},
	deniedCallback: (deniedPermission: String) -> Unit = {}) : PermissionRequest(listOf(permission), { grantedCallback(it[0]) }, { deniedCallback(it[0]) })
