package cz.kotox.core.media.opengl.anim.gpuimage.nodes

import android.renderscript.Float3
import android.renderscript.Matrix4f
import androidx.annotation.CallSuper
import cz.kotox.core.media.opengl.niceOutput
import cz.kotox.core.media.opengl.normalize
import cz.kotox.core.media.opengl.rotate
import cz.kotox.core.media.opengl.scale
import cz.kotox.core.media.opengl.times
import cz.kotox.core.media.opengl.translate
import java.lang.ref.WeakReference
import kotlin.math.cos
import kotlin.math.sin

open class GPUNode(val name: String = "GPUNode",
	open var position: Float3 = Float3(0f, 0f, 0f),
	var scale: Float3 = Float3(1f, 1f, 1f),
	var rotation: Float3 = Float3(0f, 0f, 0f)
) {

	val vertexCoord: Float = 0.5f

	var parent: WeakReference<GPUNode>? = null
	var children = arrayListOf<GPUNode>()

	open var modelMatrix = Matrix4f()
		get() {
			field.loadIdentity()
			field.translate(position)
			field.rotate(rotation)
			val scale1 = Float3(scale.x, scale.y, -scale.z)
			field.scale(scale1)
			return field
		}

	var worldTransform = Matrix4f()
		get() {
			parent?.get()?.let {
				return it.worldTransform * this.modelMatrix
			}

			return modelMatrix
		}

	fun add(childNode: GPUNode, atIndex: Int) {
		if (atIndex != -1) {
			children.add(atIndex, childNode)
		} else {
			children.add(childNode)
		}
		childNode.parent = WeakReference(this)
	}

	fun remove(childNode: GPUNode) {
		childNode.children.forEach {
			it.parent = WeakReference(this)
			children.add(it)
		}
		childNode.children = arrayListOf()
		val index = children.indexOf(childNode)
		if (index != -1) {
			children.removeAt(index)
			childNode.parent?.clear()
			childNode.parent = null
		}
	}

	fun nodeNamedRecursive(name: String): GPUNode? {
		children.forEach {
			if (it.name == name) {
				return it
			} else {
				val grandChild = it.nodeNamedRecursive(name)
				if (grandChild != null) {
					return grandChild
				}
			}
		}
		return null
	}

	val forwardVector: Float3
		get() = Float3(sin(rotation.y), 0f, cos(rotation.y)).normalize()// * Float3(1f, 1f, -1f)

	val rightVector: Float3
		get() = Float3(forwardVector.z, forwardVector.y, -forwardVector.x)

	override fun toString(): String {
		return "GPUNode(name='$name', position=${position.niceOutput()}, scale=${scale.niceOutput()})"
	}

	@CallSuper
	open fun destroy() {
		// TODO
	}
}