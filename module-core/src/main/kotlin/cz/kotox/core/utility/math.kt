package cz.kotox.core.utility

fun median(list: List<Float>) = list.sorted().let {
	(it[it.size / 2] + it[(it.size - 1) / 2]) / 2
}