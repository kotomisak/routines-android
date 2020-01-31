package cz.kotox.core.arch

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.utility.applySystemWindows

const val SNACK_BAR_MAX_LINES_DEFAULT = 4
const val TAG_NO_INTERNET_CONNECTION_DIALOG = "NO_INTERNET_CONNECTION_DIALOG"

interface BaseUIScreen : BaseView {
	val baseActivity: BaseActivity
	fun getResources(): Resources
	fun getExtras(): Bundle? = baseActivity.intent.extras
	fun finish() = baseActivity.finish()
	var lastSnackbar: Snackbar?

	//TODO showNoConnectionDialog

	fun showToast(@StringRes stringRes: Int, withOffset: Boolean = true) {
		showToast(baseActivity.getString(stringRes), withOffset)
	}

	fun showToast(message: String, withOffset: Boolean = true) {
		Toast.makeText(baseActivity, message, Toast.LENGTH_LONG).apply {
			if (withOffset) {
				setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 2 * getResources().getDimensionPixelOffset(R.dimen.global_bottom_bar_height))
			}
		}.show()
	}

	fun showSnackbar(view: View, @StringRes stringRes: Int, length: Int = Snackbar.LENGTH_LONG, maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT, config: (Snackbar.() -> Unit)? = null) {
		showSnackbar(view, view.context.getString(stringRes), length, maxLines, config)
	}

	fun showSnackbar(view: View, message: String, length: Int = Snackbar.LENGTH_LONG, maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT, config: (Snackbar.() -> Unit)? = null) {
		val newSnackbar = Snackbar.make(view, message, length).apply { config?.invoke(this) }
		// Fix issues with system insets on Snackbar
		// @see https://stackoverflow.com/questions/41300937/how-to-fix-the-snackbar-height-and-position/56893577#56893577
		applySystemWindows(newSnackbar.view, applyBottom = false, applyLeft = false, applyRight = false, applyTop = false)
		newSnackbar.view.findViewById<TextView>(R.id.snackbar_text)?.maxLines = maxLines
		lastSnackbar?.dismiss()
		lastSnackbar = newSnackbar
		newSnackbar.show()
	}

	fun dismissLastSnackbar() {
		lastSnackbar?.dismiss()
		lastSnackbar = null
	}

	fun onErrorFinish(@StringRes stringRes: Int) {
		showToast(stringRes)
		finish()
	}

	fun onErrorFinish(message: String) {
		showToast(message)
		finish()
	}
}
