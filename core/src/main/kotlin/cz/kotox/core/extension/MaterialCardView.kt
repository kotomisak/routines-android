package cz.kotox.core.extension

import androidx.databinding.BindingAdapter
import com.google.android.material.card.MaterialCardView

@BindingAdapter("strokeWidth")
fun MaterialCardView.setStrokeStyle(
	strokeWidth: Float
) {
	setStrokeWidth(strokeWidth.toInt())
	//invalidate() ////https://github.com/material-components/material-components-android/issues/1299
}