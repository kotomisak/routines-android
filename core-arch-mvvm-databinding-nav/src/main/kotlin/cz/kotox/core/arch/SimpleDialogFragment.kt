package cz.kotox.core.arch

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import cz.kotox.core.arch.extension.materialDialog
import cz.kotox.core.arch.extension.message
import cz.kotox.core.arch.extension.negativeButton
import cz.kotox.core.arch.extension.neutralButton
import cz.kotox.core.arch.extension.positiveButton
import cz.kotox.core.arch.extension.title

private const val ARG_MESSAGE_RES = "message_res"
private const val ARG_MESSAGE_TEXT = "message_text"
private const val ARG_TITLE = "title"
private const val ARG_POSITIVE_BUTTON_TEXT = "positive_button_text"
private const val ARG_NEUTRAL_BUTTON_TEXT = "neutral_button_text"
private const val ARG_NEGATIVE_BUTTON_TEXT = "negative_button_text"

class SimpleDialogFragment : BaseDialogFragment() {

	companion object {

		fun newInstance(@StringRes messageRes: Int, @StringRes title: Int?, @StringRes positiveButtonText: Int?, @StringRes neutralButtonText: Int?, @StringRes negativeButtonText: Int?) =
			SimpleDialogFragment().apply {
				arguments = Bundle().apply {
					putInt(ARG_MESSAGE_RES, messageRes)
					if (title != null) putInt(ARG_TITLE, title) //Title is optional according to material.io
					if (neutralButtonText != null) putInt(ARG_NEUTRAL_BUTTON_TEXT, neutralButtonText)
					if (positiveButtonText != null) putInt(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText)
					if (negativeButtonText != null) putInt(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText)
				}
			}

		fun newInstance(messageText: String?, @StringRes title: Int?, @StringRes positiveButtonText: Int?, @StringRes neutralButtonText: Int?, @StringRes negativeButtonText: Int?) =
			SimpleDialogFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_MESSAGE_TEXT, messageText)
					if (title != null) putInt(ARG_TITLE, title) //Title is optional according to material.io
					if (neutralButtonText != null) putInt(ARG_NEUTRAL_BUTTON_TEXT, neutralButtonText)
					if (positiveButtonText != null) putInt(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText)
					if (negativeButtonText != null) putInt(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText)
				}
			}
	}

	var onPositiveClick: (() -> Unit)? = null

	var onNeutralClick: (() -> Unit)? = null

	var onNegativeClick: (() -> Unit)? = null

	var onCancel: (() -> Unit)? = null

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
		requireContext().materialDialog {

			if (arguments?.containsKey(ARG_TITLE) == true) {
				title = getString(requireNotNull(arguments).getInt(ARG_TITLE))
			}

			if (arguments?.containsKey(ARG_MESSAGE_TEXT) == true) {
				message = requireNotNull(requireNotNull(arguments).getString(ARG_MESSAGE_TEXT))
			} else {
				message = getString(requireNotNull(arguments).getInt(ARG_MESSAGE_RES))
			}

			positiveButton(arguments?.containsKey(ARG_POSITIVE_BUTTON_TEXT) == true) {
				text = requireNotNull(arguments).getInt(ARG_POSITIVE_BUTTON_TEXT)
				listener = DialogInterface.OnClickListener { dialog, _ ->
					onPositiveClick?.invoke()
					dialog.dismiss()
				}
			}
			negativeButton(arguments?.containsKey(ARG_NEGATIVE_BUTTON_TEXT) == true) {
				text = requireNotNull(arguments).getInt(ARG_NEGATIVE_BUTTON_TEXT)
				listener = DialogInterface.OnClickListener { dialog, _ ->
					onNegativeClick?.invoke()
					dialog.dismiss()
				}
			}
			neutralButton(arguments?.containsKey(ARG_NEUTRAL_BUTTON_TEXT) == true) {
				text = requireNotNull(arguments).getInt(ARG_NEUTRAL_BUTTON_TEXT)
				listener = DialogInterface.OnClickListener { dialog, _ ->
					onNeutralClick?.invoke()
					dialog.dismiss()
				}
			}

		}

	override fun onCancel(dialog: DialogInterface) {
		onCancel?.invoke()
		super.onCancel(dialog)
	}
}