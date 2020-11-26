package cz.kotox.core.media.opengl.anim.gpuimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.CallSuper
import android.opengl.GLTextureView

abstract class GPUImage @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
	: FrameLayout(context, attrs, defStyleAttr) {

	protected val textureView: GLTextureView = GLTextureView(context)
	val renderer by lazy { createRenderer() }
	protected var imageRevealed = false
		private set

	abstract fun createRenderer(): GLTextureView.Renderer

	protected lateinit var clickableOverlay: View

	private var pauseTime = System.currentTimeMillis()
	protected var isRunning = false

	init {
		initSurfaceView()
		setTouches()
	}

	private fun setTouches() {
		clickableOverlay = this // add Clickable area, not whole view
		clickableOverlay.setOnTouchListener { v, event -> isRunning && !imageRevealed && onImageTouch(v, event) }
	}

	open fun onImageTouch(view: View, event: MotionEvent): Boolean = false

	private fun initSurfaceView() {
		addView(textureView)
		textureView.setEGLContextClientVersion(3)
		textureView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
		textureView.setRenderer(renderer)
		textureView.renderMode = GLTextureView.RENDERMODE_CONTINUOUSLY
		additionalSurfaceViewInit()
		textureView.requestRender()
	}

	open protected fun additionalSurfaceViewInit() {

	}

	fun showImage(show: Boolean, animated: Boolean) {
		if (animated) {
			textureView.animate().alpha(if (show) 1f else 0f).setDuration(300).setInterpolator(AccelerateInterpolator()).start()
		} else {
			textureView.alpha = if (show) 1f else 0f
		}
	}

	@CallSuper
	open fun revealImage() {
		imageRevealed = true
	}

	fun loadImage(image: Bitmap) {
		(renderer as GPUImageRenderer<*>).textureData = image
	}

	@CallSuper
	open fun startAnimation(duration: Long = 0) {
		isRunning = true
	}

	@CallSuper
	open fun onPause() {
		if (!isRunning) return
		isRunning = false
		textureView.renderMode = GLTextureView.RENDERMODE_WHEN_DIRTY
		pauseTime = System.currentTimeMillis()
	}

	@CallSuper
	open fun onResume() {
		if (isRunning) return
		isRunning = true
		(renderer as GPUImageRenderer<*>).startTime += (System.currentTimeMillis() - pauseTime)
		textureView.renderMode = GLTextureView.RENDERMODE_CONTINUOUSLY
		textureView.requestRender()
	}

	protected fun getNormalizedPoint(e: MotionEvent): PointF {
		return getNormalizedPoint(e.x, e.y)
	}

	protected fun getNormalizedPoint(x: Float, y: Float): PointF {
		return PointF(x / /*clickableOverlay.*/width, y / /*clickableOverlay.*/height)
	}

	override fun onDetachedFromWindow() {
		(renderer as GPUImageRenderer<*>).destroy()
		super.onDetachedFromWindow()
	}
}