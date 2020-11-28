package cz.kotox.core.media.opengl.anim.gpuimage.nodes

import android.renderscript.Matrix4f
import cz.kotox.core.media.opengl.OpenGl.drawArrays
import cz.kotox.core.media.opengl.anim.gpuimage.prepareVao
import cz.kotox.core.media.opengl.anim.shader.PassThroughShaderProgram
import cz.kotox.core.media.opengl.times

class PassThroughNode : GPUNode("PassThroughNode"), Renderable {

	override val shaderProgram = PassThroughShaderProgram()
	private val vao = prepareVao(shaderProgram, 0.5f)

	override fun render(textureId: Int, viewProjectionMatrix: Matrix4f) {
		if (shaderProgram.isInvalid) return

		vao?.bind()
		shaderProgram.use()

		shaderProgram.textureId = textureId
		shaderProgram.mvp = viewProjectionMatrix * worldTransform
		shaderProgram.restoreUniformSettings()

		drawArrays()

		vao?.unbind()
	}
}