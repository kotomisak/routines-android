package cz.kotox.core.arch.binding

import android.graphics.Paint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.TypefaceSpan
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout
import cz.kotox.core.arch.R
import java.util.Locale

@BindingAdapter("errorText")
fun TextInputLayout.setErrorMessage(errorMessage: String?) {
	if (errorMessage == null) {
		this.error = null
	} else {
		this.error = errorMessage
	}
}

@BindingAdapter("errorRes")
fun TextInputLayout.setErrorResource(@StringRes errorResource: Int?) {
	if (errorResource == null) {
		this.error = null
	} else {
		this.error = this.resources.getText(errorResource)
	}
}

//// Hint theme doesn't work, known issue: https://github.com/material-components/material-components-android/issues/584
//@Deprecated("Use fontStylingHack instead")
//@BindingAdapter("allCapsAndFontHintHack")
//fun TextInputLayout.allCapsAndFontHintHack(hintText: String) {
//	val typeFace = ResourcesCompat.getFont(context, R.font.karla)
//	typeFace?.let {
//		val hint = SpannableString(hintText.toUpperCase(Locale.getDefault())).apply {
//			setSpan(CustomTypefaceSpan(typeFace), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//		}
//
//		this.hint = hint
//	}
//}

// Hint AllCaps theme doesn't work, known issue: https://github.com/material-components/material-components-android/issues/584
@BindingAdapter("hintAllCapsText")
fun TextInputLayout.fontStylingHack(hintText: String) {
	val typeFace = ResourcesCompat.getFont(context, R.font.karla)
	typeFace?.let {
		val hintTextAllCaps = hintText.toUpperCase(Locale.getDefault())
		val hint = SpannableString(hintTextAllCaps).apply {
			setSpan(CustomTypefaceSpan(typeFace), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
		}

		this.hint = hint
	}
}

class CustomTypefaceSpan(private val newTypeFace: Typeface) : TypefaceSpan("") {

	override fun updateDrawState(ds: TextPaint) {
		applyCustomTypeFace(ds, newTypeFace)
	}

	override fun updateMeasureState(paint: TextPaint) {
		applyCustomTypeFace(paint, newTypeFace)
	}

	private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
		paint.typeface = tf
	}

}
