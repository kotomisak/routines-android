package cz.kotox.core.utility

fun fixedPartOfDynamicUrl(url: String, fixedPartDelimiter: String = "?"): String = url.split(fixedPartDelimiter).firstOrNull() ?: url
fun fixedPartOfDynamicUrlNullable(url: String?, fixedPartDelimiter: String = "?"): String? = url?.split(fixedPartDelimiter)?.firstOrNull() ?: url