package cz.kotox.core.media.opengl.anim.gpuimage.camera

import android.renderscript.Float3
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.GPUNode
import cz.kotox.core.media.opengl.minus
import cz.kotox.core.media.opengl.plus
import cz.kotox.core.media.opengl.times

open class ThirdPersonCamera(focus: GPUNode, name: String = "ThirdPersonCamera") : Camera(name) {

	var focus = focus
		set(value) {
			field = value
			updatePosition()
		}

	var focusDistance = -3f
		set(value) {
			field = value
			updatePosition()
		}

	var focusHeight = 1.2f
		set(value) {
			field = value
			updatePosition()
		}

	var currentOffset = Float3()
		set(value) {
			field = value
			updatePosition()
		}

	init {
		updatePosition()
	}

	private fun updatePosition() {
		position = focus.position - focus.forwardVector * focusDistance

		position.y = focusHeight
		position += currentOffset
		rotation.y = focus.rotation.y
	}
}