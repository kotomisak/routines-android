package cz.kotox.core.media.opengl

import android.graphics.Color
import android.renderscript.Float2
import android.renderscript.Float3
import android.renderscript.Float4
import android.renderscript.Matrix3f
import android.renderscript.Matrix4f
import android.util.SizeF
import androidx.annotation.ColorInt
import kotlin.math.sqrt

fun Matrix3f.inverseM(): Matrix3f {
	val a = this[0, 0]
	val b = this[0, 1]
	val c = this[0, 2]
	val d = this[1, 0]
	val e = this[1, 1]
	val f = this[1, 2]
	val g = this[2, 0]
	val h = this[2, 1]
	val i = this[2, 2]

	val A = e * i - f * h
	val B = f * g - d * i
	val C = d * h - e * g

	val det = a * A + b * B + c * C

	val values = floatArrayOf(
		A / det, B / det, C / det,
		(c * h - b * i) / det, (a * i - c * g) / det, (b * g - a * h) / det,
		(b * f - c * e) / det, (c * d - a * f) / det, (a * e - b * d) / det
	)
	return Matrix3f(values)
}

fun Matrix3f.transposeM(): Matrix3f {
	this.transpose()
	return this
}

fun Matrix4f.rotate(angle: Float3): Matrix4f {
	this.rotate(angle.x.radiansToDegrees(), 1f, 0f, 0f)
	this.rotate(angle.y.radiansToDegrees(), 0f, 1f, 0f)
	this.rotate(angle.z.radiansToDegrees(), 0f, 0f, 1f)
	return this
}

fun Matrix4f.translate(translation: Float3): Matrix4f {
	this.translate(translation.x, translation.y, translation.z)
	return this
}

fun Matrix4f.scale(scaling: Float3): Matrix4f {
	this.scale(scaling.x, scaling.y, scaling.z)
	return this
}

fun Matrix4f.inverseM(): Matrix4f {
	this.inverse()
	return this
}

fun Matrix4f.perspective(fovDegrees: Float, aspect: Float, near: Float, far: Float): Matrix4f {
	this.loadPerspective(fovDegrees, aspect, near, far)
	return this
}

fun Matrix4f.upperLeft(): Matrix3f {
	val values = floatArrayOf(
		this[0, 0], this[0, 1], this[0, 2],
		this[1, 0], this[1, 1], this[1, 2],
		this[2, 0], this[2, 1], this[2, 2])

	return Matrix3f(values)
}

fun Matrix4f.normalMatrix(): Matrix3f = this.upperLeft().inverseM().transposeM()

const val PI: Float = kotlin.math.PI.toFloat()

fun radians(degrees: Float): Float {
	return (degrees / 180) * PI
}

fun degrees(radians: Float): Float {
	return (radians / PI) * 180
}

fun Float.radiansToDegrees(): Float = ((this / PI) * 180)
fun Float.degreesToRadians(): Float = ((this / 180) * PI)

fun Matrix4f.niceOutput(): String {
	val sb = StringBuilder()
	sb.appendln()
	sb.appendln("columns: ")
	for (column in 0..3) {
		sb.append("- .$column : float4(")
		for (row in 0..3) {
			sb.append("${this.get(column, row)}, ")
		}
		sb.appendln()
	}
	return sb.toString()
}

fun Float2.niceOutput(): String {
	return "float2(${this.x}, ${this.y})"
}

fun Float3.niceOutput(): String {
	return "float3(${this.x}, ${this.y}, ${this.z})"
}

fun Float3.toFloat4(): Float4 = Float4(this.x, this.y, this.z, 0f)

fun Float4.niceOutput(): String {
	return "float4(${this.x}, ${this.y}, ${this.z}, ${this.w})"
}

operator fun Matrix4f.times(rhs: Matrix4f) = this.also {
	multiply(rhs);
}

fun parseColor(@ColorInt color: Int) = Float3(Color.red(color) / 255f, Color.green(color) / 255f, Color.blue(color) / 255f)

val Float3.stride: Int
	get() = 3 * (java.lang.Float.SIZE / java.lang.Byte.SIZE)

fun List<Float3>.toFloatArray(): FloatArray {
	return FloatArray(
		this.size * 3
	) { i ->
		when (i % 3) {
            0 -> this[(i / 3)].x
            1 -> this[(i / 3)].y
            2 -> this[(i / 3)].z
            else -> throw RuntimeException()
		}
	}
}

fun SizeF.toFloat2() = Float2(this.width, this.height)

fun Float4.toFloatArray(): FloatArray = floatArrayOf(this.x, this.y, this.z, this.w)
fun Float3.toFloatArray(): FloatArray = floatArrayOf(this.x, this.y, this.z)
fun Float2.toFloatArray(): FloatArray = floatArrayOf(this.x, this.y)

operator fun Float3.minus(rhs: Float3) = Float3(this.x - rhs.x, this.y - rhs.y, this.z - rhs.z)
operator fun Float3.plus(rhs: Float3) = Float3(this.x + rhs.x, this.y + rhs.y, this.z + rhs.z)
operator fun Float3.times(rhs: Float3) = Float3(this.x * rhs.x, this.y * rhs.y, this.z * rhs.z)
operator fun Float3.div(rhs: Float3) = Float3(this.x / rhs.x, this.y / rhs.y, this.z / rhs.z)

operator fun Float3.plus(v: Float) = Float3(x + v, y + v, z + v)
operator fun Float3.minus(v: Float) = Float3(x - v, y - v, z - v)
operator fun Float3.times(v: Float) = Float3(x * v, y * v, z * v)
operator fun Float3.div(v: Float) = Float3(x / v, y / v, z / v)

operator fun Float2.plus(rhs: Float2) = Float2(this.x + rhs.x, this.y + rhs.y)
operator fun Float2.times(rhs: Float2) = Float2(this.x * rhs.x, this.y * rhs.y)
operator fun Float2.div(rhs: Float2) = Float2(this.x / rhs.x, this.y / rhs.y)

fun Float3.normalize(): Float3 {
	val l = 1.0f / length()
	return Float3(x * l, y * l, z * l)
}

fun Float3.length() = sqrt(x * x + y * y + z * z)