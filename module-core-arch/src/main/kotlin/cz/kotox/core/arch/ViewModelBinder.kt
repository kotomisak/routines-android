package cz.kotox.core.arch

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import cz.kotox.core.arch.liveevent.Event
import cz.kotox.core.di.Injectable

interface ViewModelBinder<V : BaseViewModel, B : ViewDataBinding> : LifecycleOwner, BaseUIScreen, Injectable {
	var binding: B
	var viewModel: V
	val currentFragmentManager: FragmentManager

	fun inflateBindingLayout(inflater: LayoutInflater): B
	fun setupViewModel(): V
	fun getViewLifecycleOwner(): LifecycleOwner

	fun setupBinding(inflater: LayoutInflater): B = inflateBindingLayout(inflater).apply {
		lifecycleOwner = getViewLifecycleOwner()

		//TODO
//		setVariable(BR.view, this@ViewModelBinder)
		setVariable(BR.viewModel, viewModel)
	}

	@CallSuper
	fun observeBaseEvents() {
		observeEvent<ShowToastEvent> { showToast(it.message) }
		observeEvent<FinishEvent> { finish() }
		observeSnackbarEvent(binding.root)
		observeEvent<HideSnackbarEvent> { dismissLastSnackbar() }
		observeEvent<ShowNoConnectionDialog> { TODO("showNoConnectionDialog not implemented yet!") }
	}

	/**
	 * Specified this way, so that children may override which view will be used to show the snackbar
	 * @param view to show snackbar
	 */
	fun observeSnackbarEvent(view: View) {
		observeEvent<ShowSnackbarEvent> { snackbarEvent ->
			showSnackbar(view, snackbarEvent.message, snackbarEvent.length, snackbarEvent.maxLines) {
				snackbarEvent.action?.let { (actionName, action) ->
					this@showSnackbar.setAction(actionName) { action(); lastSnackbar = null }
				}
			}
		}
	}
}

/**
 * This way because interface can't have inline function :(
 * @param lifecycleOwner to specify whether using whole Lifecycle owner or ViewLifecycleOwner
 */
inline fun <reified T : Event> ViewModelBinder<*, *>.observeEvent(lifecycleOwner: LifecycleOwner = getViewLifecycleOwner(), crossinline action: (T) -> Unit) {
	viewModel.observeEvent(lifecycleOwner, T::class.java, Observer { action.invoke(it) })
}
