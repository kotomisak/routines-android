package cz.kotox.core.extension

import android.graphics.Bitmap
import java.io.File

fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
	outputStream().use { out ->
		bitmap.compress(format, quality, out)
		out.flush()
		out.close()
	}
}