package cz.kotox.core.media.opengl.anim.gpuimage.nodes

import android.renderscript.Matrix4f
import cz.kotox.core.media.opengl.anim.shader.ComputeShaderProgram
import cz.kotox.core.media.opengl.anim.shader.ShaderProgram

interface Renderable {
	val shaderProgram: ShaderProgram
	fun render(textureId: Int, viewProjectionMatrix: Matrix4f)
}

interface Computable {
	val computeShaderProgram: ComputeShaderProgram
	fun compute(textureId: Int)
}

interface Updateable {
	fun update(time: Float)
}