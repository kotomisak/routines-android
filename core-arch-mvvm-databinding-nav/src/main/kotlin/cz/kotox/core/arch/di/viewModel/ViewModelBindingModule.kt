package cz.kotox.core.arch.di.viewModel

import androidx.lifecycle.ViewModel
import cz.kotox.core.arch.di.viewModel.AssistedSavedStateViewModelFactory
import dagger.Module
import dagger.multibindings.Multibinds

@Module
abstract class ViewModelBindingModule {
	@Multibinds
	abstract fun viewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

	@Multibinds
	abstract fun assistedViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>

}
