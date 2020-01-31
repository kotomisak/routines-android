package cz.kotox.core.utility

import android.os.Build
import android.text.Html
import android.text.Spanned

@Suppress("DEPRECATION")
fun fromHtml(text: String): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
	Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
} else {
	Html.fromHtml(text)
}