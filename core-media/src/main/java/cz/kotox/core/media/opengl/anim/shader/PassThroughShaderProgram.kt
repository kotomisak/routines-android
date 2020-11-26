package cz.kotox.core.media.opengl.anim.shader

import android.renderscript.Matrix4f
import cz.kotox.core.media.opengl.OpenGl.NO_TEXTURE
import cz.kotox.core.media.opengl.OpenGl.setTexture
import cz.kotox.core.media.opengl.anim.shader.Shader.FRAGMENT_COLOR
import cz.kotox.core.media.opengl.anim.shader.Shader.GL_VERSION
import cz.kotox.core.media.opengl.anim.shader.Shader.HIGH_PRECISION
import cz.kotox.core.media.opengl.anim.shader.Shader.layoutLocation
import org.intellij.lang.annotations.Language

class PassThroughShaderProgram : DefaultVertexShaderProgram(noFilterFragmentShader), TextureShaderProgram {

	companion object {
		@Language("GLSL")
		private val noFilterFragmentShader = """
			$GL_VERSION
			$HIGH_PRECISION

			in vec2 uv;

			uniform sampler2D inputTexture;

			${layoutLocation(FRAGMENT_COLOR)}
			out vec4 color;

			void main() {
				color = texture(inputTexture, uv);
			}
			""".trimIndent()
	}

	init {
		mvp = Matrix4f()
	}

	override var textureId = NO_TEXTURE
		set(value) {
			setTexture(value, uniformSettings, "inputTexture")
			field = value
		}
}