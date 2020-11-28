package cz.kotox.core.media.opengl

import android.graphics.Bitmap
import android.opengl.GLUtils
import android.util.Size
import android.opengl.GLES
import timber.log.Timber
import java.nio.IntBuffer

object OpenGl {

	const val INVALID_PROGRAM_ID = 0
	const val INVALID_LOCATION = -1
	const val NO_TEXTURE = -1

	fun loadTexture(img: Bitmap, usedTexId: Int): Int {
		return loadTexture(img, usedTexId, true)
	}

	fun loadTexture(img: Bitmap, usedTexId: Int, recycle: Boolean): Int {
		val textures = IntArray(1)
		if (usedTexId == NO_TEXTURE) {
			GLES.glGenTextures(1, textures, 0)
			GLES.glBindTexture(GLES.GL_TEXTURE_2D, textures[0])
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_MAG_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_MIN_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_WRAP_S, GLES.GL_CLAMP_TO_EDGE.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_WRAP_T, GLES.GL_CLAMP_TO_EDGE.toFloat())

			GLUtils.texImage2D(GLES.GL_TEXTURE_2D, 0, img, 0)
		} else {
			GLES.glBindTexture(GLES.GL_TEXTURE_2D, usedTexId)
			GLUtils.texSubImage2D(GLES.GL_TEXTURE_2D, 0, 0, 0, img)
			textures[0] = usedTexId
		}
		if (recycle) {
			img.recycle()
		}
		return textures[0]
	}

	fun loadTexture(data: IntBuffer, size: Size, usedTexId: Int): Int {
		val textures = IntArray(1)
		if (usedTexId == NO_TEXTURE) {
			GLES.glGenTextures(1, textures, 0)
			GLES.glBindTexture(GLES.GL_TEXTURE_2D, textures[0])
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_MAG_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_MIN_FILTER, GLES.GL_LINEAR.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_WRAP_S, GLES.GL_CLAMP_TO_EDGE.toFloat())
			GLES.glTexParameterf(GLES.GL_TEXTURE_2D,
				GLES.GL_TEXTURE_WRAP_T, GLES.GL_CLAMP_TO_EDGE.toFloat())
			GLES.glTexImage2D(GLES.GL_TEXTURE_2D, 0, GLES.GL_RGBA, size.width, size.height,
				0, GLES.GL_RGBA, GLES.GL_UNSIGNED_BYTE, data)
		} else {
			GLES.glBindTexture(GLES.GL_TEXTURE_2D, usedTexId)
			GLES.glTexSubImage2D(GLES.GL_TEXTURE_2D, 0, 0, 0, size.width,
				size.height, GLES.GL_RGBA, GLES.GL_UNSIGNED_BYTE, data)
			textures[0] = usedTexId
		}
		return textures[0]
	}

	fun loadTextureAsBitmap(data: IntBuffer, size: Size, usedTexId: Int): Int {
		val bitmap = Bitmap
			.createBitmap(data.array(), size.width, size.height, Bitmap.Config.ARGB_8888)
		return loadTexture(bitmap, usedTexId)
	}

	fun loadShader(strSource: String, iType: Int): Int {
		val compiled = IntArray(1)
		val iShader = GLES.glCreateShader(iType)
		GLES.glShaderSource(iShader, strSource)
		GLES.glCompileShader(iShader)
		GLES.glGetShaderiv(iShader, GLES.GL_COMPILE_STATUS, compiled, 0)
		if (compiled[0] == INVALID_PROGRAM_ID) {
			Timber.e("Load Shader Failed: Compilation\n ${GLES.glGetShaderInfoLog(iShader)}")
			return INVALID_PROGRAM_ID
		}
		return iShader
	}

	fun loadComputeShader(computeShader: String): Int {
		val link = IntArray(1)
		val programId = GLES.glCreateProgram()
		val shader = loadShader(computeShader, GLES.GL_COMPUTE_SHADER)
		GLES.glAttachShader(programId, shader)
		GLES.glLinkProgram(programId)
		GLES.glGetProgramiv(programId, GLES.GL_LINK_STATUS, link, 0)
		if (link[0] <= INVALID_PROGRAM_ID) {
			Timber.e("Load Program: Error compiling program: ${GLES.glGetProgramInfoLog(programId)}")
			Timber.e("Load Program: Linking Failed")
			return INVALID_PROGRAM_ID
		}
		GLES.glDeleteShader(shader)
		return programId
	}

	fun loadProgram(strVSource: String, strFSource: String): Int {
		val link = IntArray(1)
		val iVShader = loadShader(strVSource, GLES.GL_VERTEX_SHADER)
		if (iVShader == INVALID_PROGRAM_ID) {
			Timber.e("Load Program: Vertex Shader Failed")
			return INVALID_PROGRAM_ID
		}
		val iFShader = loadShader(strFSource, GLES.GL_FRAGMENT_SHADER)
		if (iFShader == INVALID_PROGRAM_ID) {
			Timber.e("Load Program: Fragment Shader Failed")
			return INVALID_PROGRAM_ID
		}

		val iProgId = GLES.glCreateProgram()

		GLES.glAttachShader(iProgId, iVShader)
		GLES.glAttachShader(iProgId, iFShader)

		GLES.glLinkProgram(iProgId)

		GLES.glGetProgramiv(iProgId, GLES.GL_LINK_STATUS, link, 0)
		if (link[0] <= INVALID_PROGRAM_ID) {
			Timber.e("Load Program: Error compiling program: ${GLES.glGetProgramInfoLog(iProgId)}")
			Timber.e("Load Program: Linking Failed")
			return INVALID_PROGRAM_ID
		}
		GLES.glDeleteShader(iVShader)
		GLES.glDeleteShader(iFShader)
		return iProgId
	}

	fun drawArrays(mode: Int = GLES.GL_TRIANGLE_STRIP, first: Int = 0, count: Int = 4) {
		GLES.glDrawArrays(mode, first, count)
	}

	fun drawBuffers(buffers: IntArray, offset: Int = 0) {
		GLES.glDrawBuffers(buffers.size, buffers, offset)
	}

	fun setTexture(textureId: Int, uniformSettings: UniformSettings, s: String = "inputTexture", textureLocation: Int = 0) {
		GLES.glActiveTexture(GLES.GL_TEXTURE0 + textureLocation)
		GLES.glBindTexture(GLES.GL_TEXTURE_2D, textureId)
		uniformSettings[s] = textureLocation
	}

	fun enableFaceCulling(mode: Int) {
		GLES.glEnable(GLES.GL_CULL_FACE)
		GLES.glFrontFace(GLES.GL_CCW)
		GLES.glCullFace(mode)
	}

	fun disableFaceCulling() {
		GLES.glDisable(GLES.GL_CULL_FACE)
	}

	fun enableDepth(mode: Int) {
		// Enable depth test
		GLES.glEnable(GLES.GL_DEPTH_TEST)
		GLES.glDepthFunc(mode)
	}

	fun disableDepth() {
		GLES.glDisable(GLES.GL_DEPTH_TEST)
	}

	fun enableBlend(sFactor: Int = GLES.GL_ONE, dFactor: Int = GLES.GL_ONE) {
		GLES.glEnable(GLES.GL_BLEND)
		GLES.glBlendFunc(sFactor, dFactor)
	}

	fun enableAlphaBlending() = enableBlend(GLES.GL_SRC_ALPHA, GLES.GL_ONE_MINUS_SRC_ALPHA)

	fun disableBlend() {
		GLES.glDisable(GLES.GL_BLEND)
	}
}