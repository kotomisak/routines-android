package cz.kotox.core.media.glide

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import cz.kotox.core.utility.fixedPartOfDynamicUrl

@GlideModule
class SoulGlideModule : AppGlideModule() {

	override fun applyOptions(context: Context, builder: GlideBuilder) {
		builder.setDefaultRequestOptions(RequestOptions()
			.dontAnimate()
			.diskCacheStrategy(DiskCacheStrategy.RESOURCE))
	}

//	override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//		registry.append(EggVideoThumbnail::class.java, ByteBuffer::class.java, EggVideoThumbnailModelLoader.Factory())
//		registry.append(VideoThumbnail::class.java, ByteBuffer::class.java, VideoThumbnailModelLoader.Factory())
//	}

//	override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
//		val clientBuilder = OkHttpClient.Builder()
//			.followRedirects(true)
//			.followSslRedirects(true)
//			.retryOnConnectionFailure(true)
//			.cache(null)
//			.connectTimeout(5, TimeUnit.SECONDS)
//			.writeTimeout(5, TimeUnit.SECONDS)
//			.readTimeout(5, TimeUnit.SECONDS)
//
//		val factory = OkHttpUrlLoader.Factory(enableTls12OnPreLollipop(clientBuilder).build())
//
//		registry
//			.register(SVG::class.java, PictureDrawable::class.java, SvgDrawableTranscoder())
//			.append(InputStream::class.java, SVG::class.java, SvgDecoder())
//		registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
//
//	}

	// Disable manifest parsing to avoid adding similar modules twice.
	override fun isManifestParsingEnabled(): Boolean {
		return false
	}
}

/**
 * Use this for dynamic (usually signed) url's in order to cache just the image name, not the whole url.
 */
class GlideDynamicUrl(url: String, delimiter: String) :
	GlideUrl(StringBuilder(url).toString()) {
	private val sourceUrl: String = fixedPartOfDynamicUrl(url, delimiter)
	override fun getCacheKey(): String {
		return sourceUrl
	}

	override fun toString(): String {
		return sourceUrl//super.getCacheKey()
	}
}