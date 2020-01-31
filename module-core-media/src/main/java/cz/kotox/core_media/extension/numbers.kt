package cz.kotox.core_media.extension

import android.content.res.Resources

private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()