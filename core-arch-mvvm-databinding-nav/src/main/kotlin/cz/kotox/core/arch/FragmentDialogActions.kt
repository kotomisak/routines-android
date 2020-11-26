package cz.kotox.core.arch

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

private const val DIALOG_PROGRESS = "dialog_progress"
private const val DIALOG_RESULT = "dialog_result"
private const val DIALOG_OFFLINE = "dialog_offline"
private const val DIALOG_INFO_POPUP = "dialog_info_popup"
private const val DIALOG_APP_SETTINGS = "dialog_app_settings"

interface FragmentDialogActions {

	fun showDialog(parentFragmentManager: FragmentManager, tag: String, dialog: () -> DialogFragment) {
		// Remove this dialog if exists
		removeDialog(parentFragmentManager, tag)

		// Show dialog
		dialog().show(parentFragmentManager, tag)
	}

	fun removeDialog(parentFragmentManager: FragmentManager, tag: String) {
		parentFragmentManager.findFragmentByTag(tag)?.run {
			parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
		}
	}

	fun showProgress(parentFragmentManager: FragmentManager, @StringRes title: Int) {
		showDialog(parentFragmentManager, DIALOG_PROGRESS) {
			Dialog.progressDialog(title)
		}
	}

	fun hideProgress(parentFragmentManager: FragmentManager) {
		removeDialog(parentFragmentManager, DIALOG_PROGRESS)
	}

	fun showResult(parentFragmentManager: FragmentManager, @DrawableRes icon: Int, @StringRes title: Int, @StringRes description: Int? = null) {
		showDialog(parentFragmentManager, DIALOG_RESULT) {
			Dialog.resultDialog(icon, title, description)
		}
	}

	fun hideResult(parentFragmentManager: FragmentManager) {
		removeDialog(parentFragmentManager, DIALOG_RESULT)
	}

	fun showOfflineDialog(fragment: Fragment, parentFragmentManager: FragmentManager) {
		showDialog(parentFragmentManager, DIALOG_OFFLINE) {
			OfflineDialog.newInstance(fragment)
		}
	}

	/**
	 * Show generic popup dialog fragment from any feature module without need to have dependency there.
	 */
	fun showExternalPopupDialogFragment(fragment: Fragment, parentFragmentManager: FragmentManager, tag: String, dialog: () -> DialogFragment) {
		showDialog(parentFragmentManager, tag, dialog)
	}

	fun showPermissionSettingsDialog(fragment: Fragment, parentFragmentManager: FragmentManager, @StringRes personalisedMessage: Int? = null) {
		showDialog(parentFragmentManager, DIALOG_APP_SETTINGS) {

			if (personalisedMessage == null) {
				Dialog.simpleDialog(
					message = R.string.settings_dialog_title,
					positiveButtonText = R.string.settings_dialog_bt_settings,
					positiveAction = showSettingsAction(fragment),
					negativeButtonText = R.string.settings_dialog_bt_cancel,
					negativeAction = {},

					cancelAction = {}
				)
			} else {
				Dialog.simpleDialog(
						title = R.string.settings_dialog_title,
						message = personalisedMessage,
						positiveButtonText = R.string.settings_dialog_bt_settings,
						positiveAction = showSettingsAction(fragment),
						negativeButtonText = R.string.settings_dialog_bt_cancel,
						negativeAction = {},

						cancelAction = {}
				)
			}
		}
	}

	private fun showSettingsAction(fragment: Fragment): () -> Unit {
		val showSettingsAction = {
			val appSettingsIntent = Intent()
			appSettingsIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
			val uri: Uri = Uri.fromParts("package", fragment.requireActivity().packageName,
				null)
			appSettingsIntent.data = uri
			fragment.requireActivity().startActivity(appSettingsIntent)
		}
		return showSettingsAction
	}

	fun showApplicationSettings(fragment: Fragment) = showSettingsAction(fragment).invoke()

}
