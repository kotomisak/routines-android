package cz.kotox.core.extension

import kotlin.math.abs

fun List<Float>.closestValue(value: Float) = minBy { abs(value - it) }