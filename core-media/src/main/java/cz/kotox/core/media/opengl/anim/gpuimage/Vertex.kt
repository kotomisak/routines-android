package cz.kotox.core.media.opengl.anim.gpuimage

import android.renderscript.Float2
import android.renderscript.Float3
import java.io.Serializable

class Vertex(private var position: Float3, private var uv: Float2) : Serializable {

	companion object {
		const val stride = 5 * (java.lang.Float.SIZE / java.lang.Byte.SIZE)

		@JvmStatic
		@JvmOverloads
		fun toFloatArray(vertices: List<Vertex>, flipVertical: Boolean = false): FloatArray {
			val result = FloatArray(5 * vertices.size)

			val offset = 5
			for (i in vertices.indices) {
				result[i * offset] = vertices[i].position.x
				result[i * offset + 1] = vertices[i].position.y
				result[i * offset + 2] = vertices[i].position.z
				result[i * offset + 3] = vertices[i].uv.x
				result[i * offset + 4] = if (flipVertical) flip(vertices[i].uv.y) else vertices[i].uv.y
			}

			return result
		}

		private fun flip(i: Float): Float {
			return if (i == 0.0f) {
				1.0f
			} else 0.0f
		}
	}
}