package cz.kotox.core.utility

/**
 * Shortcut for lazy with no thread safety
 */
fun <T> lazyUnsafe(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
