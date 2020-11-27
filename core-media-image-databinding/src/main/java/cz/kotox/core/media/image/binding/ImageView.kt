package cz.kotox.core.media.image.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.bumptech.glide.signature.ObjectKey
import cz.kotox.core.media.image.glide.GlideApp
import cz.kotox.core.media.image.glide.GlideDynamicUrl
import cz.kotox.core.media.image.R
import timber.log.Timber

var factory: DrawableCrossFadeFactory = DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()

@BindingAdapter(value = ["imgUrl", "changed", "placeholder"], requireAll = false)
fun ImageView.loadImageUrl(imageUrl: String?, changed: Long?, placeholderResource: Drawable?) {
	//Timber.d(">>>_ loadImageUrl: $imageUrl")
	try {
		when {
			imageUrl != null -> {
				val circularProgressDrawable = CircularProgressDrawable(context)
				circularProgressDrawable.strokeWidth = 10f
				circularProgressDrawable.centerRadius = 30f
				circularProgressDrawable.setColorSchemeColors(
					ContextCompat.getColor(context, R.color.color_shape_progress_1),
					ContextCompat.getColor(context, R.color.color_shape_progress_2),
					ContextCompat.getColor(context, R.color.color_shape_progress_3)
				)
				circularProgressDrawable.start()

				val requestOptions = RequestOptions()
				requestOptions.placeholder(circularProgressDrawable)
				//Do not use error placeholder yet, since I am unable to change scaleType for placeholder
				//requestOptions.error(R.drawable.ic_close)

				//Do not use glideDynamicUrl for local paths since it doesn't work for some reason.
				val urlDelimiter = "?"
				val url: Any = if (imageUrl.contains(urlDelimiter)) GlideDynamicUrl(imageUrl, urlDelimiter) else imageUrl
				GlideApp.with(context)
					.load(url)
					.apply(requestOptions)
					.transition(withCrossFade(factory))
					.apply {
						if (placeholderResource != null) placeholder(placeholderResource)
						if (placeholderResource != null) error(placeholderResource)
						if (changed != null) signature(ObjectKey(changed))
					}
					.into(this)
			}
			placeholderResource != null -> {
				setImageDrawable(placeholderResource)
			}
			else -> {
				setImageBitmap(null)
			}
		}
	} catch (e: Exception) {
		Timber.d(e, ">>>_ loadImageUrl failed!")
	}
}

@BindingAdapter(value = ["imgUri"])
fun ImageView.loadImageUrl(imageUri: Uri?) {
	Timber.d(">>>_ loadImageUri: $imageUri")
	when {
		imageUri != null -> {
			val circularProgressDrawable = CircularProgressDrawable(context)
			circularProgressDrawable.strokeWidth = 10f
			circularProgressDrawable.centerRadius = 30f
			circularProgressDrawable.setColorSchemeColors(
				ContextCompat.getColor(context, R.color.color_shape_progress_1),
				ContextCompat.getColor(context, R.color.color_shape_progress_2),
				ContextCompat.getColor(context, R.color.color_shape_progress_3)
			)
			circularProgressDrawable.start()

			val requestOptions = RequestOptions()
			requestOptions.placeholder(circularProgressDrawable)
			//Do not use error placeholder yet, since I am unable to change scaleType for placeholder
			//requestOptions.error(R.drawable.ic_close)

			//Do not use glideDynamicUrl for local paths since it doesn't work for some reason.
			GlideApp.with(context)
				.load(imageUri)
				.apply(requestOptions)
				.transition(withCrossFade(factory))
				.into(this)
		}
		else -> {
			setImageBitmap(null)
		}
	}
}

//@BindingAdapter("src")
//fun ImageView.src(@DrawableRes drawableRes: Int?) {
//	if (drawableRes != null && drawableRes != 0) {
//		try {
//			setImageDrawable(ContextCompat.getDrawable(context, drawableRes))
//		} catch (e: Exception) {
//			e.printStackTrace()
//		}
//	}
//}
//
//@BindingAdapter("src")
//fun ImageView.src(bitmap: Bitmap?) {
//	if (bitmap != null) {
//		setImageBitmap(bitmap)
//	}
//}
//
//@BindingAdapter("src")
//fun AppCompatImageButton.src(drawable: Drawable?) {
//	if (drawable != null) {
//		setImageDrawable(drawable)
//	}
//}
//
//@BindingAdapter(value = ["videoUrl", "authHeader"], requireAll = true)
//fun ImageView.loadThumbnail(videoUrl: String?, authHeader: String?) {
//	if (videoUrl != null && authHeader != null) {
//		GlideApp.with(context)
//			.load(EggVideoThumbnail(videoUrl, authHeader))
//			.into(this)
//	} else {
//		setImageBitmap(null)
//	}
//}
//
//@BindingAdapter(value = ["videoPath", "frameMs", "changed"], requireAll = false)
//fun ImageView.loadThumbnail(videoPath: String?, frameMs: Long?, changed: Long?) {
//	if(videoPath != null) {
//		GlideApp.with(context)
//			.load(VideoThumbnail(videoPath, frameMs ?: 0))
//			.apply {
//				if (changed != null) signature(ObjectKey(changed))
//			}
//			.into(this)
//	} else {
//		setImageBitmap(null)
//	}
//}
