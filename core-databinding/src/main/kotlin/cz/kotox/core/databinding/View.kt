package cz.kotox.core.databinding

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.databinding.BindingAdapter
import androidx.transition.TransitionManager
import cz.kotox.core.extension.doOnApplyWindowInsets

@BindingAdapter("show", "animate", requireAll = false)
fun View.setShow(show: Boolean, animate: Boolean?) {
	if (animate != null && animate && parent is ViewGroup)
		TransitionManager.beginDelayedTransition(parent as ViewGroup)
	visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("visible", "animate", requireAll = false)
fun View.setVisible(show: Boolean, animate: Boolean?) {
	if (animate != null && animate && parent is ViewGroup)
		TransitionManager.beginDelayedTransition(parent as ViewGroup)
	visibility = if (show) View.VISIBLE else View.INVISIBLE
}

@BindingAdapter("invisible", "animate", requireAll = false)
fun View.setInvisible(invisible: Boolean, animate: Boolean?) {
	if (animate != null && animate && parent is ViewGroup)
		TransitionManager.beginDelayedTransition(parent as ViewGroup)
	visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter(
	"leftSystemWindowInsets",
	"topSystemWindowInsets",
	"rightSystemWindowInsets",
	"bottomSystemWindowInsets",
	requireAll = false
)
fun View.fitSystemWindowInset(applyLeft: Boolean, applyTop: Boolean, applyRight: Boolean, applyBottom: Boolean) {
	doOnApplyWindowInsets { view, insets, padding ->
		val left = if (applyLeft) insets.systemWindowInsetLeft else 0
		val top = if (applyTop) insets.systemWindowInsetTop else 0
		val right = if (applyRight) insets.systemWindowInsetRight else 0
		val bottom = if (applyBottom) insets.systemWindowInsetBottom else 0

		view.setPadding(padding.left + left, padding.top + top, padding.right + right, padding.bottom + bottom)
	}
}

@BindingAdapter("fitSystemWindowInsets")
fun View.fitSystemWindowInset(fit: Boolean) {
	fitSystemWindowInset(fit, fit, fit, fit)
}

@BindingAdapter("layout_marginBottom")
fun setBottomMargin(view: View, bottomMargin: Float) {
	val layoutParams = view.layoutParams as MarginLayoutParams
	layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin,
		layoutParams.rightMargin, Math.round(bottomMargin))
	view.layoutParams = layoutParams
}

@BindingAdapter("layoutHeightPercent", "layoutHeightMax", requireAll = true)
fun View.setLayoutHeight(heightPercent: Float, heightMax: Float) {
	layoutParams.apply { this.height = (heightMax * heightPercent).toInt() / 100 }
}

@BindingAdapter("layoutWidthPercent", "layoutWidthMax", requireAll = true)
fun View.setLayoutWidth(widthPercent: Float, widthMax: Float) {
	layoutParams.apply { this.width = (widthMax * widthPercent).toInt() / 100 }
}

@BindingAdapter("layout_height")
fun View.setLayoutDynamicHeight(newHeight: Float) {
	layoutParams.apply { this.height = newHeight.toInt() }
}

@BindingAdapter("layout_width")
fun View.setLayoutDynamicWidth(newWidth: Float) {
	layoutParams.apply { this.width = newWidth.toInt() }
}