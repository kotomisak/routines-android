package cz.kotox.core.media.opengl.anim.shader

object Shader {
	const val GL_VERSION = "#version 310 es"
	const val LOW_PRECISION = "precision lowp float;"
	const val HIGH_PRECISION = "precision highp float;"

	const val FRAGMENT_COLOR = 0

	fun layoutLocation(location: Int) = "layout (location = $location)"
}
