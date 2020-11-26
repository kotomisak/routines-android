package cz.kotox.core.arch.di.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * General factory for ViewModels with SavedStateHandle.
 * This factory allows having all ViewModels in one InjectingSavedStateViewModelFactory
 *
 * Use With @AssistedInject.Factory:
 *
 * ```
 * @AssistedInject.Factory
 * interface Factory: AssistedSavedStateViewModelFactory<SomeViewModel>
 * ```
 * In your Dagger module (annotated with `@AssistedModule`) add binding of your AssistedInject.Factory to this abstraction:
 * ```
 * @Binds
 * @IntoMap
 * @ViewModelKey(SomeViewModel::class)
 * abstract fun bindSomeViewModelFactory(factory: SomeViewModel.Factory) : AssistedSavedStateViewModelFactory<out ViewModel>
 * ```
 *
 * @see com.squareup.inject.assisted.dagger2.AssistedModule
 * @see com.squareup.inject.assisted.AssistedInject.Factory
 */
interface AssistedSavedStateViewModelFactory<T : ViewModel> {
	fun create(savedStateHandle: SavedStateHandle, args: ViewModelArgs): T
}

@Suppress("UNCHECKED_CAST")
class ViewModelArgs {
	private val args: MutableMap<String, Any> = mutableMapOf()

	operator fun <T: Any> get(key: String): T {
		return try {
			args[key] as T
		} catch (e: Exception) {
			throw ClassCastException()
		}
	}

	operator fun <T: Any> set(key: String, value: T) {
		args[key] = value
	}
}
