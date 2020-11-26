package cz.kotox.core.arch.di.viewModel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Reusable
import javax.inject.Inject
import javax.inject.Provider

/**
 * Use this class for instantiating your ViewModels. @Inject it into your Fragment/Activity and create ViewModel with it as a factory.
 *
 * For example in Fragment:
 * ```
 * @Inject
 * lateinit var factory: InjectingSavedStateViewModelFactory
 * lateinit var viewModel : SomeViewModel
 *
 * override fun onCreate(savedInstanceState: Bundle?) {
 *   super.onCreate(savedInstanceState)
 *   viewModel = ViewModelProvider(this, factory)[SomeViewModel::class.java]
 * }
 * ```
 * @param assistedFactories used for ViewModels annotated with `@AssistedInject` and having `AssistedInject.Factory`
 * @param viewModelProviders used for ViewModels annotated with `@Inject` (pure Dagger instantiation)
 *
 * @see AssistedSavedStateViewModelFactory for how to get this class
 */
@Reusable
class InjectingSavedStateViewModelFactory @Inject constructor(
		private val assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
		private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {

	/**
	 * Creates instance of ViewModel either annotated with @AssistedInject or @Inject and passes dependencies it needs.
	 */
	fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null, viewModelArgs: ViewModelArgs) =
		object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
			override fun <T : ViewModel?> create(
				key: String,
				modelClass: Class<T>,
				handle: SavedStateHandle
			): T {
				val viewModel =
					createAssistedInjectViewModel(modelClass, handle, viewModelArgs)
						?: createInjectViewModel(modelClass)
						?: throw IllegalArgumentException("Unknown model class $modelClass")

				try {
					@Suppress("UNCHECKED_CAST")
					return viewModel as T
				} catch (e: Exception) {
					throw RuntimeException(e)
				}
			}
		}

	/**
	 * Creates ViewModel based on @AssistedInject constructor and its factory
	 */
	private fun <T : ViewModel?> createAssistedInjectViewModel(
		modelClass: Class<T>,
		handle: SavedStateHandle,
		viewModelArgs: ViewModelArgs
	): ViewModel? {
		val creator = assistedFactories[modelClass]
			?: assistedFactories.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
			?: return null

		return creator.create(handle, viewModelArgs)
	}

	/**
	 * Creates ViewModel based on regular Dagger @Inject constructor
	 */
	private fun <T : ViewModel?> createInjectViewModel(modelClass: Class<T>): ViewModel? {
		val creator = viewModelProviders[modelClass]
			?: viewModelProviders.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
			?: return null

		return creator.get()
	}
}
