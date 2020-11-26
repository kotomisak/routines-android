package cz.kotox.core.arch

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

object Dialog {

	fun simpleDialog(
		@StringRes
		message: Int,
		@StringRes
		title: Int? = null,
		@StringRes
		positiveButtonText: Int? = null,
		@StringRes
		neutralButtonText: Int? = null,
		@StringRes
		negativeButtonText: Int? = null,
		negativeAction: (() -> Unit)? = null,
		neutralAction: (() -> Unit)? = null,
		positiveAction: (() -> Unit)? = null,
		cancelAction: (() -> Unit)? = null) =
		SimpleDialogFragment.newInstance(message, title, positiveButtonText, neutralButtonText, negativeButtonText).apply {
			onPositiveClick = positiveAction
			onNeutralClick = neutralAction
			onNegativeClick = negativeAction
			onCancel = cancelAction
		}

	fun simpleDialog(
		message: String,
		@StringRes
		title: Int? = null,
		@StringRes
		positiveButtonText: Int? = null,
		@StringRes
		neutralButtonText: Int? = null,
		@StringRes
		negativeButtonText: Int? = null,
		negativeAction: (() -> Unit)? = null,
		neutralAction: (() -> Unit)? = null,
		positiveAction: (() -> Unit)? = null,
		cancelAction: (() -> Unit)? = null) =
		SimpleDialogFragment.newInstance(message, title, positiveButtonText, neutralButtonText, negativeButtonText).apply {
			onPositiveClick = positiveAction
			onNeutralClick = neutralAction
			onNegativeClick = negativeAction
			onCancel = cancelAction
		}

	fun progressDialog(@StringRes title: Int) = ProgressDialogFragment.newInstance(title)

	fun resultDialog(@DrawableRes icon: Int, @StringRes title: Int, @StringRes description: Int? = null) = ResultDialogFragment.newInstance(icon, title, description)
}