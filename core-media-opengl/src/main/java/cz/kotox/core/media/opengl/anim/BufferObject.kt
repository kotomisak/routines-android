package cz.kotox.core.media.opengl.anim

import android.opengl.GLES
import java.nio.Buffer

class BufferObject(private val mUsage: Usage, private val mVBOType: VBOType) {
	private val mLocation = IntArray(1)

	val location: Int
		get() = mLocation[0]

	enum class VBOType private constructor(val type: Int) {
		ARRAY_BUFFER(GLES.GL_ARRAY_BUFFER),
		ELEMENT_BUFFER(GLES.GL_ELEMENT_ARRAY_BUFFER),
		STORAGE_BUFFER(GLES.GL_SHADER_STORAGE_BUFFER)
	}

	enum class Usage private constructor(val usage: Int) {
		STATIC_DRAW(GLES.GL_STATIC_DRAW),
		DYNAMIC_DRAW(GLES.GL_DYNAMIC_DRAW),
		DYNAMIC_COPY(GLES.GL_DYNAMIC_COPY)
	}

	init {
		genBuffer()
	}

	fun bind() {
		GLES.glBindBuffer(mVBOType.type, mLocation[0])
	}

	fun bufferData(buffer: Buffer, size: Int) {
		GLES.glBufferData(mVBOType.type, size, buffer, mUsage.usage)
	}

	fun bufferData(buffer: Buffer, size: Int, offset: Int) {
		GLES.glBufferSubData(mVBOType.type, offset, size, buffer)
	}

	fun unbind() {
		GLES.glBindBuffer(mVBOType.type, 0)
	}

	fun onDestroy() {
		GLES.glDeleteBuffers(1, mLocation, 0)
	}

	private fun genBuffer() {
		GLES.glGenBuffers(1, mLocation, 0)
	}

}