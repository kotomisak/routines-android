package cz.kotox.core.extension

import android.app.Activity
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager

fun View.doOnApplyWindowInsets(f: (View, WindowInsets, InitialPadding) -> Unit) {
	// Create a snapshot of the view's padding state
	val initialPadding = recordInitialPaddingForView(this)
	// Set an actual OnApplyWindowInsetsListener which proxies to the given
	// lambda, also passing in the original padding state
	setOnApplyWindowInsetsListener { v, insets ->
		f(v, insets, initialPadding)
		// Always return the insets, so that children can also use them
		insets
	}
	// request some insets
	requestApplyInsetsWhenAttached()
}

fun View.requestApplyInsetsWhenAttached() {
	if (isAttachedToWindow) {
		// We're already attached, just request as normal
		requestApplyInsets()
	} else {
		// We're not attached to the hierarchy, add a listener to
		// request when we are
		addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
			override fun onViewAttachedToWindow(v: View) {
				v.removeOnAttachStateChangeListener(this)
				v.requestApplyInsets()
			}

			override fun onViewDetachedFromWindow(v: View) = Unit
		})
	}
}

data class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)

private fun recordInitialPaddingForView(view: View) = InitialPadding(view.paddingLeft, view.paddingTop, view.paddingRight, view.paddingBottom)

/**
 * Hiding keyboard from dialogFragment will not work the same way as hiding keyboard from activity.
 * Therefore here is lower-level, more common, and uglier way.
 * https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
 */
fun View.hideKeyboard() {
	val imm: InputMethodManager = this.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager;
	imm.hideSoftInputFromWindow(this.windowToken, 0);
}