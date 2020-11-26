package cz.kotox.core.extension

import android.content.Context
import android.util.TypedValue
import androidx.core.app.NotificationCompat

fun Context.notification(channelId: String, setup: NotificationCompat.Builder.() -> Unit) =
	NotificationCompat.Builder(this, channelId).apply(setup).build()

fun Context.convertDpToPx(dp: Float): Float {
	return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
}
