package cz.kotox.core.extension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.app.ShareCompat

fun Activity.shareImage(uri: Uri, message: String, chooserTitle: String) {
	ShareCompat.IntentBuilder
		.from(this)
		.setType("text/html")
		.setText(message)
		.setStream(uri)
		.intent
		.setAction(Intent.ACTION_SEND)
		.setDataAndType(uri, "image/png")
		.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		.run {
			startActivity(Intent.createChooser(this, chooserTitle))
		}
}

fun Activity.shareText(text: String, chooserTitle: String) {
	ShareCompat.IntentBuilder
		.from(this)
		.setType("text/plain")
		.setText(text)
		.setChooserTitle(chooserTitle)
		.startChooser()
}