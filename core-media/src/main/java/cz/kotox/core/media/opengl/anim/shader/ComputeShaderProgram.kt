package cz.kotox.core.media.opengl.anim.shader

import cz.kotox.core.media.opengl.OpenGl.INVALID_LOCATION
import cz.kotox.core.media.opengl.OpenGl.loadComputeShader

abstract class ComputeShaderProgram private constructor(programId: Int) : ShaderProgram(programId) {
	constructor(computeShader: String) : this(loadComputeShader(computeShader))

	override val positionAttributeLocation = INVALID_LOCATION

	abstract val gIndexBufferBinding: Int
	abstract var deltaTime: Float
}