package cz.kotox.core.media.opengl.anim.shader

import android.renderscript.Matrix4f
import cz.kotox.core.media.opengl.anim.shader.Shader.GL_VERSION
import cz.kotox.core.media.opengl.anim.shader.Shader.HIGH_PRECISION
import cz.kotox.core.media.opengl.anim.shader.Shader.layoutLocation
import org.intellij.lang.annotations.Language

abstract class DefaultVertexShaderProgram(fragmentShader: String) : ShaderProgram(noFilterVertexShader, fragmentShader) {

	companion object {
		const val POSITION_LOCATION = 0
		const val UV_ATTR_LOCATION = 1

		@Language("GLSL")
		val noFilterVertexShader = """
			$GL_VERSION
			$HIGH_PRECISION

			${layoutLocation(POSITION_LOCATION)}
			in vec3 position;
			${layoutLocation(UV_ATTR_LOCATION)}
			in vec2 uvAttr;

			uniform mat4 mvp;

			out vec2 uv;

			void main() {
				gl_Position = mvp * vec4(position, 1.0);
				uv = uvAttr;
			}
		""".trimIndent()
	}

	override val positionAttributeLocation: Int = POSITION_LOCATION
	override val uvAttributeLocation: Int = UV_ATTR_LOCATION

	var mvp: Matrix4f = Matrix4f()
		set(value) {
			uniformSettings["mvp"] = value
			field = value
		}
}