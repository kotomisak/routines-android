package cz.kotox.core.media.opengl.anim.gpuimage

import android.renderscript.Float3
import android.util.SizeF
import androidx.annotation.CallSuper
import cz.kotox.core.media.opengl.anim.gpuimage.camera.Camera
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Computable
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.GPUNode
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Renderable
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Updateable
import java.util.LinkedList

abstract class GPUScene(var size: SizeF) {

	var rootNode = GPUNode("Root")

	var cameras = ArrayList<Camera>()

	var renderables: ArrayList<Any?> = arrayListOf()
		private set

	private var runOnUIList = LinkedList<() -> Unit>()

	var imageSize = SizeF(0f, 0f)

	init {
		val camera = Camera("camera1")
		camera.position = Float3(0f, 0f, 3f)
		cameras.add(camera)
		setupCameraSize()
	}

	@CallSuper
	open fun onInit() {
		setupScene()
	}

	fun nodeNamed(name: String): GPUNode? {
		return if (rootNode.name == name) {
			rootNode
		} else {
			rootNode.nodeNamedRecursive(name)
		}
	}

	@CallSuper
	open fun onSceneSizeChanged(size: SizeF) {
		this.size = size
		setupCameraSize()
	}

	protected fun setupCameraSize() {
		for (camera in cameras) {
			camera.aspect = size.width / size.height
		}
	}

	// scene setup
	abstract fun setupScene()

	fun add(node: GPUNode, parent: GPUNode? = null, atIndex: Int = -1) {
		if (parent != null) {
			parent.add(node, atIndex)
		} else {
			rootNode.add(node, atIndex)
		}

		if (node is Renderable || node is Computable || node is Updateable) {
			if (atIndex != -1)
				renderables.add(atIndex, node)
			else
				renderables.add(node)
		}
	}

	fun remove(node: GPUNode) {
		val parent = node.parent
		if (parent != null) {
			parent.get()?.remove(node)
		} else {
			for (child in node.children) {
				child.parent = null
			}

			node.children = arrayListOf()
		}

		if (node is Renderable || node is Computable || node is Updateable) {
			renderables.remove(node)
		}
	}

	fun runPreDraw() {
		synchronized(runOnUIList) {
			while (!runOnUIList.isEmpty()) {
				runOnUIList.remove().invoke()
			}
		}
	}

	fun runOnDraw(callable: () -> Unit) {
		synchronized(runOnUIList) {
			runOnUIList.add(callable)
		}
	}

	open fun onImageSizeChanged(newSize: SizeF) {
		imageSize = newSize
	}

	@CallSuper
	open fun destroy() {
		// TODO
	}
}