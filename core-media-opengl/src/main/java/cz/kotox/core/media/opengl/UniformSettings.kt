package cz.kotox.core.media.opengl

import android.renderscript.Float2
import android.renderscript.Float3
import android.renderscript.Float4
import android.renderscript.Matrix3f
import android.renderscript.Matrix4f
import android.util.Log
import android.opengl.GLES

class UniformSettings(private val programId: Int) {

	private val uniformLocationMap: MutableMap<String, Int> = hashMapOf()
	private val uniformValueMap: MutableMap<Int, Any> = hashMapOf()

	operator fun set(uniformName: String, i: Int) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = i
	}

	operator fun set(uniformName: String, f: Float) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = f
	}

	operator fun set(uniformName: String, vec2: Float2) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = vec2
	}

	operator fun set(uniformName: String, vec3: Float3) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = vec3
	}

	operator fun set(uniformName: String, vec4: Float4) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = vec4
	}

	operator fun set(uniformName: String, matrix: Matrix3f) {
		val uniformLocation = uniformLocation(uniformName)

		uniformValueMap[uniformLocation] = matrix
	}

	operator fun set(uniformName: String, matrix: Matrix4f) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = matrix
	}

	operator fun set(uniformName: String, matrix: Matrix4f, transposed: Boolean) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = Pair(matrix, transposed)
	}

	operator fun set(uniformName: String, bool: Boolean) {
		val uniformLocation = uniformLocation(uniformName)
		uniformValueMap[uniformLocation] = bool
	}

	fun restoreShaderSettings() {
		uniformValueMap.forEach { it.value.setUniform(it.key) }
	}

	fun onDestroy() {
		uniformLocationMap.clear()
		uniformValueMap.clear()
	}

	private fun uniformLocation(uniformName: String): Int {
		return uniformLocationMap[uniformName] ?: GLES.glGetUniformLocation(programId, uniformName).also {
			if (it == -1) Log.e(this.javaClass.simpleName, "$uniformName doesn't exist in program: $programId")
			uniformLocationMap[uniformName] = it
		}
	}
}

private fun Any.setUniform(location: Int) {
	when (this) {
		is Int -> setUniform(location)
		is Float -> setUniform(location)
		is Boolean -> setUniform(location)
		is Float2 -> setUniform(location)
		is Float3 -> setUniform(location)
		is Float4 -> setUniform(location)
		is Matrix3f -> setUniform(location)
		is Matrix4f -> setUniform(location)
		is Pair<*, *> -> if (first is Matrix4f && second is Boolean) (first as Matrix4f).setUniform(location, (second as Boolean))
		else -> throw RuntimeException("Cannot set uniform! Unknown type: ${this::class.java.simpleName}")
	}
}

private fun Int.setUniform(location: Int) = GLES.glUniform1i(location, this)
private fun Float.setUniform(location: Int) = GLES.glUniform1f(location, this)
private fun Boolean.setUniform(location: Int) = GLES.glUniform1i(location, if (this) 1 else 0)
private fun Float2.setUniform(location: Int) = GLES.glUniform2fv(location, 1, this.toFloatArray(), 0)
private fun Float3.setUniform(location: Int) = GLES.glUniform3fv(location, 1, this.toFloatArray(), 0)
private fun Float4.setUniform(location: Int) = GLES.glUniform4fv(location, 1, this.toFloatArray(), 0)
private fun Matrix3f.setUniform(location: Int, transpose: Boolean = false) = GLES.glUniformMatrix3fv(location, 1, transpose, this.array, 0)
private fun Matrix4f.setUniform(location: Int, transpose: Boolean = false) = GLES.glUniformMatrix4fv(location, 1, transpose, this.array, 0)