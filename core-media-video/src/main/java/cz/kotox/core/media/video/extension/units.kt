package cz.kotox.core.media.video.extension

import android.content.res.Resources

private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()