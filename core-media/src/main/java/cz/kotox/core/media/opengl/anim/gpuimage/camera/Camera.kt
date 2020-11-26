package cz.kotox.core.media.opengl.anim.gpuimage.camera

import android.renderscript.Matrix4f
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.GPUNode
import cz.kotox.core.media.opengl.inverseM
import cz.kotox.core.media.opengl.perspective
import cz.kotox.core.media.opengl.radians
import cz.kotox.core.media.opengl.rotate
import cz.kotox.core.media.opengl.scale
import cz.kotox.core.media.opengl.times
import cz.kotox.core.media.opengl.translate

open class Camera(name: String = "Camera") : GPUNode(name) {
	var fovDegrees: Float = 70f
	var fovRadians: Float = radians(fovDegrees)

	var aspect: Float = 1f
	var near: Float = 0.1f
	var far: Float = 100f

	private val revertMatrix = Matrix4f().apply {
		set(2, 2, -1f)
	}

	open var projectionMatrix = Matrix4f()
		get() {
			return field.perspective(fovDegrees, aspect, near, far) * revertMatrix
		}

	private val viewMatrix = Matrix4f()
		get() {
			field.loadIdentity()
			field.translate(position)
			field.rotate(rotation)
			field.scale(scale)
			return field.inverseM()
		}

	val viewProjectionMatrix
		get() = projectionMatrix * viewMatrix
}