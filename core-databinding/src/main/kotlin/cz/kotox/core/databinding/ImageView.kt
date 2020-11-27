package cz.kotox.core.databinding

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

@BindingAdapter("src")
fun ImageView.src(@DrawableRes drawableRes: Int?) {
	if (drawableRes != null && drawableRes != 0) {
		try {
			setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}