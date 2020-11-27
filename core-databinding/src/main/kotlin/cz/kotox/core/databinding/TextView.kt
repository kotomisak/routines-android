package cz.kotox.core.databinding

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("textRes")
fun TextView.setTextRes(stringRes: StringRes?) {
	val newText = when (stringRes) {
		is StringRes.Regular -> context.getString(stringRes.resId)
		is StringRes.Formatted -> context.getString(stringRes.resId, *stringRes.args)
		is StringRes.Quantity -> context.resources.getQuantityString(stringRes.resId, stringRes.quantity, *stringRes.args)
		null -> ""
	}
	val oldText = text

	// save layout passes, should apply to all other
	if (haveContentsChanged(newText, oldText)) {
		text = newText
	}
}

@BindingAdapter("textRes")
fun TextView.setTextRes(stringRes: Int?) {
	val newText = if (stringRes != null && stringRes != 0) context.getString(stringRes) else ""
	val oldText = text

	// save layout passes, should apply to all other
	if (haveContentsChanged(newText, oldText)) {
		text = newText
	}
}

@BindingAdapter(value = ["spannableText", "links"], requireAll = true)
fun TextView.setSpannableLinks(spannable: String, links: HashMap<Int, () -> Unit>) {
	val span = SpannableStringBuilder(spannable)

	for ((link, callback) in links) {
		val customSpan = CustomClickableSpan(context, callback, currentTextColor)
		val linkSpan = resources.getString(link)
		val startLink = spannable.indexOf(linkSpan)
		val endLink = startLink + linkSpan.length

		if (startLink >= 0) span.setSpan(customSpan, startLink, endLink, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
	}

	text = span
}

@BindingAdapter(value = ["htmlRes"], requireAll = true)
fun TextView.setHtmlText(@androidx.annotation.StringRes resourceText: Int) {
	text = HtmlCompat.fromHtml(this.context.getString(resourceText), HtmlCompat.FROM_HTML_MODE_LEGACY)
}

@BindingAdapter("textColor")
fun TextView.setTextColor(colorHexCode: String) {
	setTextColor(Color.parseColor("#${colorHexCode}"))
}

private fun haveContentsChanged(str1: CharSequence?, str2: CharSequence?): Boolean {
	if (str1 == null != (str2 == null)) {
		return true
	} else if (str1 == null) {
		return false
	}
	val length = str1.length
	if (length != str2!!.length) {
		return true
	}
	for (i in 0 until length) {
		if (str1[i] != str2[i]) {
			return true
		}
	}
	return false
}

sealed class StringRes(val resId: Int) {

	class Regular(resId: Int) : StringRes(resId) {
		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Regular) return false
			if (resId != other.resId) return false
			return true
		}

		override fun hashCode(): Int {
			return resId.hashCode()
		}
	}

	class Quantity(resId: Int, val quantity: Int, vararg values: Any?) : StringRes(resId) {
		val args = values

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Quantity) return false
			if (resId != other.resId) return false
			if (quantity != other.quantity) return false
			if (!args.contentEquals(other.args)) return false
			return true
		}

		override fun hashCode(): Int {
			var result = 1
			result = 31 * result + resId
			result = 31 * result + quantity
			result = 31 * result + args.contentHashCode()
			return result
		}
	}

	class Formatted(resId: Int, vararg values: Any?) : StringRes(resId) {
		val args = values

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Formatted) return false
			if (resId != other.resId) return false
			if (!args.contentEquals(other.args)) return false
			return true
		}

		override fun hashCode(): Int {
			var result = 1
			result = 31 * result + resId
			result = 31 * result + args.contentHashCode()
			return result
		}
	}
}

class CustomClickableSpan(val context: Context, val onClick: () -> Unit, val textColor: Int? = null) : ClickableSpan() {

	override fun onClick(p0: View) {
		onClick.invoke()
	}

	override fun updateDrawState(ds: TextPaint) {
		super.updateDrawState(ds)
		ds.apply {
			textColor?.let { textColor ->
				color = textColor
			}

			isUnderlineText = false

			typeface = ResourcesCompat.getFont(context, R.font.karla_bold)
		}

	}
}