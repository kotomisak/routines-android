package cz.kotox.core.media.opengl.anim

import android.opengl.GLES


class VertexArrayObject {
	private val mLocation = IntArray(1)

	enum class DataType private constructor(val type: Int) {
		FLOAT(GLES.GL_FLOAT)
	}

	init {
		GLES.glGenVertexArrays(1, mLocation, 0)
	}

	fun bind() {
		GLES.glBindVertexArray(mLocation[0])
	}

	fun unbind() {
		GLES.glBindVertexArray(0)
	}

	@JvmOverloads
	fun vertexAttribPointer(location: Int, size: Int, stride: Int = 0, offset: Int = 0) {
		vertexAttribPointer(location, size, DataType.FLOAT, stride, offset)
	}

	fun vertexAttribPointer(location: Int, size: Int, dataType: DataType, stride: Int, offset: Int) {
		GLES.glVertexAttribPointer(location, size, dataType.type, false, stride, offset)
	}

	fun enableVertexAttribArray(location: Int) {
		GLES.glEnableVertexAttribArray(location)
	}

	fun disableVertexAttribArray(location: Int) {
		GLES.glDisableVertexAttribArray(location)
	}

	fun onDestroy() {
		GLES.glDeleteVertexArrays(1, mLocation, 0)
	}

}