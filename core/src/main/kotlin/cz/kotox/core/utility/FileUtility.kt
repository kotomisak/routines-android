package cz.kotox.core.utility

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun readTextFileFromAssets(application: Application, assetFilePath: String): String {
	return withContext(Dispatchers.IO) {
		val inputStream = application.assets.open(assetFilePath)
		inputStream.bufferedReader().use { it.readText() }
	}
}

fun folderSize(directory: File): Long {
	var length: Long = 0
	directory.listFiles()?.let {
		for (file in it) {
			length += if (file.isFile) file.length() else folderSize(file)
		}
	}
	return length
}