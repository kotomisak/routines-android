package cz.kotox.core.media.opengl.anim.gpuimage

import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES30
import android.os.Handler
import android.os.Looper
import android.util.SizeF
import cz.kotox.core.media.opengl.OpenGl.loadTexture
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Computable
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Renderable
import cz.kotox.core.media.opengl.anim.gpuimage.nodes.Updateable
import cz.kotox.core.media.opengl.anim.shader.FrameBuffer
import android.opengl.GLTextureView
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

abstract class GPUImageRenderer<T : GPUScene> : GLTextureView.Renderer {

	var logFPS = false
		set(value) {
			field = value
			if (value)
				handler.postDelayed(runnable, 1000)
			else {
				handler.removeCallbacks(runnable)
			}
		}

	var textureId = -1
	var textureData: Bitmap? = null
		set(value) {
			field = value
			value?.let {
				imageSize = SizeF(it.width.toFloat(), it.height.toFloat())
			}
		}
	var imageSize = SizeF(0f, 0f)
		set(value) {
			field = value
			onImageSizeChanged(value)
		}

	private fun onImageSizeChanged(newSize: SizeF) {
		scene?.onImageSizeChanged(newSize)
	}

	protected open var backgroundColor = Color.TRANSPARENT

	var startTime = System.currentTimeMillis()
	private var currentSeconds = 0f
		get() {
			return (System.currentTimeMillis() - startTime).toFloat() / 1000f
		}
	var scene: T? = null
		private set

	private var i = 0

	var fpsCount = 0
	private var currentFps = 0

	private val handler = Handler(Looper.getMainLooper())
	private val runnable: Runnable = object : Runnable {
		override fun run() {
			Timber.d("FPS count: $currentFps")
			fpsCount = currentFps
			currentFps = 0
			handler.postDelayed(this, 1000)
		}
	}

	override fun onDrawFrame(gl: GL10?) {
		currentFps++

		setupTexture()
		FrameBuffer.clearFrameBuffer(backgroundColor, GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

		scene?.runPreDraw()

		render(textureId, currentSeconds)
	}

	protected open fun render(textureId: Int, currentSeconds: Float) {
		i = 0
		scene?.renderables?.let {
			while (i < it.size) {
				if (it[i] is Updateable) (it[i] as Updateable).update(currentSeconds)
				if (it[i] is Computable) (it[i] as Computable).compute(textureId)
				scene?.cameras?.get(0)?.let { camera ->
					if (it[i] is Renderable) (it[i] as Renderable).render(textureId, camera.viewProjectionMatrix)
				}
				i++
			}
		}
	}

	private fun setupTexture() {
		if (textureId != -1) {
			return
		}
		textureData?.let {
			textureId = loadTexture(it.cropCenter(), textureId, false)
		}
		if (textureId != -1) {
			textureData = null
		}
	}

	override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
		GLES30.glViewport(0, 0, width, height)
		val newSize = SizeF(width.toFloat(), height.toFloat())
		if (scene == null) {
			scene = createScene(newSize)
			(scene as T).onInit()
		} else {
			scene?.onSceneSizeChanged(newSize)
		}
	}

	override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
		FrameBuffer.clearFrameBuffer(backgroundColor)
		if (logFPS) handler.postDelayed(runnable, 1000)
	}

	abstract fun createScene(size: SizeF): T

	open fun destroy() {
		if (logFPS) handler.removeCallbacks(runnable)
		scene?.destroy()
	}
}