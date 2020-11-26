package cz.kotox.core.logging.timber

import timber.log.Timber

class LineNumberDebugTree : Timber.DebugTree() {
	override fun createStackElementTag(element: StackTraceElement): String? {
		return "Soulvibe:(${element.fileName}:${element.lineNumber})#${element.methodName}"
	}
}