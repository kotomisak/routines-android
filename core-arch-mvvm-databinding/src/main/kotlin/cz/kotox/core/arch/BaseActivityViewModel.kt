package cz.kotox.core.arch

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import javax.inject.Inject

abstract class BaseActivityViewModel<V : BaseViewModel, B : ViewDataBinding> : BaseActivity(), ViewModelBinder<V, B> {
	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	override lateinit var viewModel: V
	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = supportFragmentManager
	override fun getViewLifecycleOwner(): LifecycleOwner = this

	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: FragmentActivity = this, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = setupViewModel()
		binding = setupBinding(layoutInflater)
		setContentView(binding.root)
		observeBaseEvents()
	}
}