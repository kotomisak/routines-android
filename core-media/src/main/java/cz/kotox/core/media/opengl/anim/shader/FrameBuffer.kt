package cz.kotox.core.media.opengl.anim.shader

import android.graphics.Color
import android.util.Size
import androidx.annotation.ColorInt
import android.opengl.GLES

class FrameBuffer constructor(val size: Size, targetCount: Int = 1) {

	companion object {
		fun clearFrameBuffer(@ColorInt color: Int, mask: Int = 0) {
			if (mask != 0) {
				GLES.glClear(mask)
			}
			GLES.glClearColor(Color.red(color) / 255f, Color.green(color) / 255f, Color.blue(color) / 255f, Color.alpha(color) / 255f)
		}
	}

	private var mFrameBuffer: IntArray? = null
	private var mFrameBufferTexture: IntArray? = null

	val texture: Int
		get() = mFrameBufferTexture!![0]

	init {
		if (targetCount == 0) throw RuntimeException("FrameBuffer target count has to be at least 1")
		mFrameBuffer = IntArray(1)
		mFrameBufferTexture = IntArray(targetCount)
		GLES.glGenFramebuffers(1, mFrameBuffer, 0)
		GLES.glBindFramebuffer(GLES.GL_FRAMEBUFFER, mFrameBuffer!![0])
		GLES.glGenTextures(targetCount, mFrameBufferTexture, 0)

		for (i in 0 until targetCount) {
			GLES.glBindTexture(GLES.GL_TEXTURE_2D, mFrameBufferTexture!![i])
			GLES.glTexImage2D(GLES.GL_TEXTURE_2D, 0, GLES.GL_RGBA, size.width, size.height, 0, GLES.GL_RGBA, GLES.GL_UNSIGNED_BYTE, null)
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D, GLES.GL_TEXTURE_MAG_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D, GLES.GL_TEXTURE_MIN_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D, GLES.GL_TEXTURE_WRAP_S, GLES.GL_CLAMP_TO_EDGE.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D, GLES.GL_TEXTURE_WRAP_T, GLES.GL_CLAMP_TO_EDGE.toFloat())
			GLES.glFramebufferTexture2D(GLES.GL_FRAMEBUFFER, GLES.GL_COLOR_ATTACHMENT0 + i, GLES.GL_TEXTURE_2D, mFrameBufferTexture!![i], 0)
		}

		GLES.glBindTexture(GLES.GL_TEXTURE_2D, 0)
		GLES.glBindFramebuffer(GLES.GL_FRAMEBUFFER, 0)
	}

	fun onDestroy() {
		if (mFrameBufferTexture != null) {
			GLES.glDeleteTextures(mFrameBufferTexture!!.size, mFrameBufferTexture, 0)
			mFrameBufferTexture = null
		}
		if (mFrameBuffer != null) {
			GLES.glDeleteFramebuffers(mFrameBuffer!!.size, mFrameBuffer, 0)
			mFrameBuffer = null
		}
	}

	@JvmOverloads
	fun clearFramebufferWithColor(@ColorInt color: Int, mask: Int = GLES.GL_COLOR_BUFFER_BIT) {
		clearFrameBuffer(color, mask)
	}

	fun activateBufferForRendering() {
		GLES.glBindFramebuffer(GLES.GL_FRAMEBUFFER, mFrameBuffer!![0])
		GLES.glViewport(0, 0, size.width, size.height)
	}

	fun deactivateFrameBuffer() {
		GLES.glBindFramebuffer(GLES.GL_FRAMEBUFFER, 0)
	}

	fun getTexture(i: Int): Int {
		return mFrameBufferTexture!![i]
	}
}