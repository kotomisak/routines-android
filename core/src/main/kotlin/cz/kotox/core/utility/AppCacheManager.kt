package cz.kotox.core.utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import cz.kotox.core.extension.writeBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val APP_CACHE_DIR = "soulvibe_cache"
private const val GLOBAL_CACHE_SPACE = ""

sealed class AppCacheCopyResult {
	data class Success(val file: File) : AppCacheCopyResult()
	data class Error(val error: RuntimeException) : AppCacheCopyResult()
}

class AppCacheManager constructor(
	private val cacheSpace: String,
	private val cacheThreshold: Long = 0
) {

	companion object {

		/**
		 * Clean complete cache application space (all cache spaces included).
		 */
		fun cleanCache(context: Context) {
			//Timber.d(">>>_ cleaning whole cache")
			getAppCacheDirectory(context).deleteRecursively()
		}
	}

	/**
	 * Use to get exact file of assets by copying asset to application folder as cache.
	 * This could be useful as an input for frameworks without access to android resources(e.g. ffmpeg).
	 * This could be also useful for multiple exoPlayers to prevent lock one resource when accessing in parallel.
	 */
	suspend fun copyFileFromAssetsToCache(
		context: Context,
		assetSubDirName: String,
		fileName: String,
		reuseExistingFile: Boolean = false
	): AppCacheCopyResult {
		return withContext(Dispatchers.IO) {
			try {
				val appCacheDir = getAppCacheDirectory(context, cacheSpace)
				val newFile = File(appCacheDir, fileName)
				if (newFile.exists() && reuseExistingFile) {
					//reuse existing file
					AppCacheCopyResult.Success(newFile)
				} else {
					//copy file from asset
					AppCacheCopyResult.Success(newFile
							.also { file ->
								file.outputStream().use { cache ->
									context.assets.open("$assetSubDirName/$fileName").use {
										it.copyTo(cache)
									}
								}
							})
				}
			} catch (e: Exception) {
				AppCacheCopyResult.Error(java.lang.RuntimeException(e))
			}
		}
	}

	/**
	 * Use to copy bitmap to application cache folder.
	 * This could be useful as an input for frameworks without access to android resources(e.g. ffmpeg).
	 */
	fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): AppCacheCopyResult {
		return try {
			val newFile = File(getAppCacheDirectory(context), fileName)
			AppCacheCopyResult.Success(newFile.apply { writeBitmap(bitmap, Bitmap.CompressFormat.PNG, 100) })
		} catch (e: Exception) {
			AppCacheCopyResult.Error(java.lang.RuntimeException(e))
		}
	}

	suspend fun copyFileFromLocalUriToCache(
		context: Context,
		sourceFileUri: Uri,
		destFileName: String
	): AppCacheCopyResult {
		return withContext(Dispatchers.IO) {
			try {

				//val file = File(context.cacheDir, context.contentResolver.getFileName(sourceFileUri))

				val appCacheDir = getAppCacheDirectory(context, cacheSpace)
				val newFile = File(appCacheDir, destFileName)
				newFile.delete()
				newFile.createNewFile()

				val parcelFileDescriptor = context.contentResolver.openFileDescriptor(sourceFileUri, "r", null)

				if (parcelFileDescriptor == null) {
					AppCacheCopyResult.Error(IllegalStateException("Unable to get parcel file descriptor for uri: $sourceFileUri"))
				} else {
					val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
					val outputStream = FileOutputStream(newFile)

					//copy file from uri
					AppCacheCopyResult.Success(newFile
							.also { file ->
								outputStream.use { cache ->
									inputStream.copyTo(cache)
								}
							})
				}

			} catch (e: Exception) {
				AppCacheCopyResult.Error(java.lang.RuntimeException(e))
			}
		}
	}

	/**
	 * Clean just particular cache space connected to current app cache manager instance
	 */
	fun cleanCacheSpace(context: Context, doNotCleanCacheBelowThresholdSize: Boolean = false) {
		val cacheFolder = getAppCacheDirectory(context, cacheSpace)
		val size = folderSize(cacheFolder)

		Timber.d(">>>_ size[$size]<$cacheThreshold")

		if (doNotCleanCacheBelowThresholdSize && size < cacheThreshold) {
			Timber.d(">>>_ Keep application cache space $cacheSpace")
		} else {

			getAppCacheDirectory(context, cacheSpace).deleteRecursively()
		}

	}

	fun createTempFile(context: Context, fileName: String) = File(getAppCacheDirectory(context), fileName)

}

private fun getAppCacheDirectory(context: Context, cacheSpace: String = GLOBAL_CACHE_SPACE): File {

	val globalCacheDir = File("${requireNotNull(context.externalCacheDir).path}/$APP_CACHE_DIR").apply {
		if (!exists()) {
			mkdir()
		}
	}
	return if (cacheSpace == GLOBAL_CACHE_SPACE) globalCacheDir else {
		File("${globalCacheDir.absolutePath}/$cacheSpace").apply {
			if (!exists()) {
				mkdir()
			}
		}
	}
}