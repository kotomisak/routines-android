package com.opkix.base.ffmpeg

import com.opkix.base.ffmpeg.model.VideoEffectType

internal fun getEffectFilterParams(effectType: VideoEffectType): String {
	return when (effectType) {
		VideoEffectType.NONE -> ""
		VideoEffectType.BLACK_WHITE -> getEffectMonochromeFilter()
		VideoEffectType.VIGNETTE -> getEffectVignetteFilter()
		VideoEffectType.SEPIA -> getEffectSepiaFilter()
		VideoEffectType.INVERT -> getEffectNegativeFilter()
		VideoEffectType.GAMMA -> getEffectGammaFilter()
	}
}

/**
 * Applies effects that imitate black-and-white photography film with low contrast.
 * It uses hue to desaturate.
 *
 * Equivalent iOs:CIPhotoEffectMono
 * https://developer.apple.com/library/archive/documentation/GraphicsImaging/Reference/CoreImageFilterReference/index.html#//apple_ref/doc/filter/ci/CIPhotoEffectMono
 *
 * Equivalent ExoPlayer:
 * https://github.com/MasayukiSuda/ExoPlayerFilter/blob/master/epf/src/main/java/com/daasuu/epf/filter/GlGrayScaleFilter.java
 *
 * Standalone command: ffmpeg -i input -vf hue=s=0 -c:a copy output
 */
private fun getEffectMonochromeFilter() = "hue=s=0"

/**
 * Applies a preconfigured set of effects that imitate black-and-white photography film with exaggerated contrast.
 * It uses hue to desaturate and increased contrast.
 *
 * Equivalent iOs:CIPhotoEffectNoir - but!!! iOs alternative has implemented some special curve and is not the same filter!!!
 * https://developer.apple.com/library/archive/documentation/GraphicsImaging/Reference/CoreImageFilterReference/index.html#//apple_ref/doc/filter/ci/CIPhotoEffectNoir
 *
 * Equivalent ExoPlayer:
 * Maybe https://github.com/MasayukiSuda/ExoPlayerFilter/blob/master/epf/src/main/java/com/daasuu/epf/filter/GlLuminanceFilter.java
 *
 * Standalone command: ffmpeg -i no_effect.png -vf hue=s=0,eq=contrast=1.5 -c:a copy monochromenoir.png
 */
private fun getEffectMonochromeNoirFilter() = "hue=s=0, eq=contrast=1.5"

/**
 * Applies a preconfigured set of effects that imitate black-and-white photography film without significantly altering contrast.
 * It uses hue to desaturate and decreased contrast and decreased brightness.
 *
 * Equivalent iOs:CIPhotoEffectTonal
 * https://developer.apple.com/library/archive/documentation/GraphicsImaging/Reference/CoreImageFilterReference/index.html#//apple_ref/doc/filter/ci/CIPhotoEffectTonal
 *
 * Equivalent ExoPlayer:
 *
 *
 * Standalone command: ffmpeg -i no_effect.png -vf hue=s=0,eq=contrast=0.9:brightness=0.1  -c:a copy monochrometonal.png
 */
private fun getEffectMonochromeTonalFilter() = "hue=s=0,eq=contrast=0.9:brightness=0.1"

/**
 * Usage of colorchannelmixer requires "-pix_fmt", "yuv420p" for output to be playable everywhere.
 */
private fun getEffectSepiaFilter() = "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131"

/**
 * The lut filter family has a bespoke mode for doing this, when you don't know whether the input is YUV or RGB.
 */
private fun getEffectNegativeFilter() = "negate"

private fun getEffectVignetteFilter() = "vignette=PI/4"

private fun getEffectGammaFilter() = "eq=gamma=5.0"

/**
 *  Usage of colorchannelmixer requires "-pix_fmt", "yuv420p" for output to be playable everywhere.
 */
private fun getEffectGrayScaleByColorMix() = "colorchannelmixer=.3:.4:.3:0:.3:.4:.3:0:.3:.4:.3"
