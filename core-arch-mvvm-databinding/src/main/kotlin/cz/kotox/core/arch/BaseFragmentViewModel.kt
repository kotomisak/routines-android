package cz.kotox.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import cz.kotox.core.arch.di.viewModel.InjectingSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import javax.inject.Inject

abstract class BaseFragmentViewModel<V : BaseViewModel, B : ViewDataBinding>(
	@LayoutRes private val layoutResId: Int,
	navigationType: NavigationType = NavigationType.NONE
) : BaseFragment(navigationType), ViewModelBinder<V, B> {

	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = requireFragmentManager()

	@Inject
	lateinit var defaultViewModelFactory: InjectingSavedStateViewModelFactory

	/**
	 * This method androidx uses for `by viewModels` method.
	 * We can set out injecting factory here and therefore don't touch it again
	 */
	override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
		defaultViewModelFactory.create(this, arguments, getViewModelArgs())

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		binding = setupBinding(inflater, layoutResId)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observe()
		observeBaseEvents()

		observeEvent<ShowProgress> { showProgress(parentFragmentManager, it.title) }
		observeEvent<HideProgress> { hideProgress(parentFragmentManager) }

		observeEvent<ShowResult> { showResult(parentFragmentManager, it.icon, it.title, it.description) }
		observeEvent<HideResult> { hideResult(parentFragmentManager) }
		observeEvent<ShowOfflineDialog> { showOfflineDialog(this, parentFragmentManager) }
	}

	override fun observe() {}

	open fun getViewModelArgs(): ViewModelArgs = ViewModelArgs()
}


