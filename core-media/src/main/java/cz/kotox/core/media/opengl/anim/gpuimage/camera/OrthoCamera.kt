package cz.kotox.core.media.opengl.anim.gpuimage.camera

import android.graphics.RectF
import android.renderscript.Matrix4f

class OrthoCamera(private val rectF: RectF, name: String = "OrthoCamera") : Camera(name) {
	override var projectionMatrix: Matrix4f = Matrix4f()
		get() {
			field.loadOrtho(rectF.left, rectF.right, rectF.bottom, rectF.top, near, far)
			return field
		}
}