package cz.kotox.core.media.opengl.anim.gpuimage

import android.graphics.Bitmap
import android.graphics.RectF
import android.renderscript.Float2
import android.renderscript.Float3
import cz.kotox.core.media.opengl.anim.BufferObject
import cz.kotox.core.media.opengl.anim.VertexArrayObject
import cz.kotox.core.media.opengl.anim.gpuimage.camera.OrthoCamera
import cz.kotox.core.media.opengl.anim.shader.ShaderProgram
import cz.kotox.core.media.opengl.stride
import java.nio.FloatBuffer
import java.nio.IntBuffer

fun genVertices(vertexCoord: Float, flipVertical: Boolean) = ArrayList<Vertex>().apply {
	add(Vertex(Float3(-vertexCoord, vertexCoord, 0f), Float2(0f, 0f)))
	add(Vertex(Float3(vertexCoord, vertexCoord, 0f), Float2(1f, 0f)))
	add(Vertex(Float3(-vertexCoord, -vertexCoord, 0f), Float2(0f, 1f)))
	add(Vertex(Float3(vertexCoord, -vertexCoord, 0f), Float2(1f, 1f)))
}

fun prepareVao(shaderProgram: ShaderProgram, vertexCoord: Float = 1f, flipVertical: Boolean = false,
			   vertices: ArrayList<Vertex> = genVertices(vertexCoord, flipVertical),
			   bufferObject: BufferObject? = null, indexBuffer: BufferObject? = null, indices: IntArray? = null,
			   additionalSetup: () -> Unit = {}
): VertexArrayObject? {
	if (shaderProgram.isInvalid) return null //TODO ask Premek about null or throw an exception

	val vao = VertexArrayObject()
	val vbo = bufferObject ?: BufferObject(BufferObject.Usage.STATIC_DRAW, BufferObject.VBOType.ARRAY_BUFFER)

	val vertexPosAttr = shaderProgram.positionAttributeLocation
	val vertexUvAttr = shaderProgram.uvAttributeLocation

	vao.bind()

	vbo.bind()
	val verticesArray = Vertex.toFloatArray(vertices, flipVertical)
	vbo.bufferData(FloatBuffer.wrap(verticesArray), Vertex.stride * vertices.size)

	vao.vertexAttribPointer(vertexPosAttr, 3, Vertex.stride)
	vao.enableVertexAttribArray(vertexPosAttr)

	val offset = Float3().stride
	vao.vertexAttribPointer(vertexUvAttr, 2, Vertex.stride, offset)
	vao.enableVertexAttribArray(vertexUvAttr)

	indices?.let {
		indexBuffer?.bind()
		indexBuffer?.bufferData(IntBuffer.wrap(it), (Integer.SIZE / java.lang.Byte.SIZE) * it.size)
	}

	additionalSetup()

	vao.unbind()
	vbo.unbind()
	indexBuffer?.unbind()

	return vao
}

fun GPUScene.setupOrthoCamera(rect: RectF = RectF(0f, 1f, 1f, 0f)) {
	cameras.clear()
	OrthoCamera(rect).apply {
		position = Float3(0f, 0f, 1f)
		cameras.add(this)
	}
}

fun Bitmap.cropCenter(): Bitmap {
	val width = this.width
	val height = this.height
	val outputSize = Math.min(width, height)
	val newSize = getScaleSize(width, height, outputSize)
	// Crop it
	val diffWidth = newSize[0] - outputSize
	val diffHeight = newSize[1] - outputSize
	val result = Bitmap.createBitmap(this, diffWidth / 2, diffHeight / 2,
		newSize[0] - diffWidth, newSize[1] - diffHeight)

	return result
}

private fun getScaleSize(width: Int, height: Int, outputSize: Int): IntArray {
	val newWidth: Float
	val newHeight: Float

	val withRatio = width.toFloat() / outputSize
	val heightRatio = height.toFloat() / outputSize

	val adjustWidth = withRatio > heightRatio

	if (adjustWidth) {
		newHeight = outputSize.toFloat()
		newWidth = newHeight / height * width
	} else {
		newWidth = outputSize.toFloat()
		newHeight = newWidth / width * height
	}
	return intArrayOf(Math.round(newWidth), Math.round(newHeight))
}