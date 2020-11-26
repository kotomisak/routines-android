package cz.kotox.core.arch.extension

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.showKeyboard() {
	inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
}

fun Activity.hideKeyboard() {
	inputMethodManager.hideSoftInputFromWindow((currentFocus ?: View(this)).windowToken, 0)
}