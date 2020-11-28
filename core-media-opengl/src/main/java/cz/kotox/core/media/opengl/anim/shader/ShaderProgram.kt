package cz.kotox.core.media.opengl.anim.shader

import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glUseProgram
import cz.kotox.core.media.opengl.OpenGl.INVALID_LOCATION
import cz.kotox.core.media.opengl.OpenGl.INVALID_PROGRAM_ID
import cz.kotox.core.media.opengl.OpenGl.loadProgram
import cz.kotox.core.media.opengl.UniformSettings

abstract class ShaderProgram protected constructor(private val programId: Int, protected val uniformSettings: UniformSettings = UniformSettings(programId)) {
	constructor(vertexShader: String, fragmentShader: String) : this(loadProgram(vertexShader, fragmentShader))

	abstract val positionAttributeLocation: Int
	open val uvAttributeLocation: Int = INVALID_LOCATION

	val isInvalid = programId == INVALID_PROGRAM_ID

	fun use() = glUseProgram(programId)

	fun restoreUniformSettings() = uniformSettings.restoreShaderSettings()

	fun destroy() {
		uniformSettings.onDestroy()
		glDeleteProgram(programId)
	}
}
