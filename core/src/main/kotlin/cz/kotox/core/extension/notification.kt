package cz.kotox.core.extension

import android.app.PendingIntent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.app.NotificationCompat

inline var NotificationCompat.Builder.largeIcon: Bitmap
	get() = throw UnsupportedOperationException("")
	set(value) {
		setLargeIcon(value)
	}

inline var NotificationCompat.Builder.smallIcon: Int
	get() = throw UnsupportedOperationException("")
	set(value) {
		setSmallIcon(value)
	}

inline var NotificationCompat.Builder.contentTitle: String
	get() = throw UnsupportedOperationException("")
	set(value) {
		setContentTitle(value)
	}

inline var NotificationCompat.Builder.contentText: String
	get() = throw UnsupportedOperationException("")
	set(value) {
		setContentText(value)
	}

inline var NotificationCompat.Builder.contentIntent: PendingIntent
	get() = throw UnsupportedOperationException("")
	set(value) {
		setContentIntent(value)
	}

inline var NotificationCompat.Builder.autoCancel: Boolean
	get() = throw UnsupportedOperationException("")
	set(value) {
		setAutoCancel(value)
	}

inline var NotificationCompat.Builder.ongoing: Boolean
	get() = throw UnsupportedOperationException("")
	set(value) {
		setOngoing(value)
	}

inline var NotificationCompat.Builder.ticker: String?
	get() = throw UnsupportedOperationException("")
	set(value) {
		setTicker(value)
	}

inline var NotificationCompat.Builder.time: Long
	get() = throw UnsupportedOperationException("")
	set(value) {
		setWhen(value)
	}

inline var NotificationCompat.Builder.style: NotificationCompat.Style?
	get() = throw UnsupportedOperationException("")
	set(value) {
		if (value != null) setStyle(value)
	}

inline var NotificationCompat.Builder.defaults: Int
	get() = throw UnsupportedOperationException("")
	set(value) {
		setDefaults(value)
	}

inline var NotificationCompat.Builder.sound: Uri?
	get() = throw UnsupportedOperationException("")
	set(value) {
		setSound(value)
	}

inline var NotificationCompat.Builder.group: String
	get() = throw UnsupportedOperationException("")
	set(value) {
		setGroup(value)
	}

fun NotificationCompat.Builder.progress(setup: Progress.() -> Unit) {
	val progress = Progress().apply(setup)
	setProgress(progress.max, progress.progress, progress.indeterminate)
}

class Progress() {
	var max: Int = 0
	var progress: Int = 0
	var indeterminate = false
}