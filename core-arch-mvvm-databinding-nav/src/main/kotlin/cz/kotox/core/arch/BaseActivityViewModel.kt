package cz.kotox.core.arch

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NavigationRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import cz.kotox.core.arch.di.viewModel.InjectingSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import javax.inject.Inject

abstract class BaseActivityViewModel<V : BaseViewModel, B : ViewDataBinding>(
	@LayoutRes private val layoutResId: Int,
	@IdRes navHostFragmentId: Int,
	@NavigationRes navGraphResourceId: Int
) : BaseActivity(layoutResId, navHostFragmentId, navGraphResourceId), ViewModelBinder<V, B> {

	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = supportFragmentManager
	override fun getViewLifecycleOwner(): LifecycleOwner = this

	@Inject
	lateinit var defaultViewModelFactory: InjectingSavedStateViewModelFactory

	/**
	 * This method androidx uses for `by viewModels` method.
	 * We can set out injecting factory here and therefore don't touch it again
	 */
	override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
		defaultViewModelFactory.create(this, getExtras(), getViewModelArgs())

	override fun setupView() {
		binding = setupBinding(layoutInflater, layoutResId)
		setContentView(binding.root)
		observeBaseEvents()
		observe()
		setupActionBar()//Invoke toolbar settings after observe implementation setup all required data
	}

	override fun observe() {}

	open fun getViewModelArgs(): ViewModelArgs = ViewModelArgs()
}