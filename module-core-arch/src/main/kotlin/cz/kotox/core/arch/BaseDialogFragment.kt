package cz.kotox.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.di.Injectable
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseDialogFragment : AppCompatDialogFragment(), BaseUIScreen {
	override val baseActivity
		get() = activity as? BaseActivity
			?: throw IllegalStateException("No activity in this fragment, can't finish")

	override var lastSnackbar: Snackbar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Kotox_Light_Dialog_Alert)
	}

	override fun onDestroyView() {
		dismissLastSnackbar()
		super.onDestroyView()
	}
}

/**
 * When you need injecting, but not viewmodel
 */
abstract class BaseDialogFragmentWithInject : BaseDialogFragment(), Injectable {
	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidSupportInjection.inject(this)
		super.onCreate(savedInstanceState)
	}
}

abstract class BaseDialogFragmentWithViewModel<V : BaseViewModel, B : ViewDataBinding> : BaseDialogFragmentWithInject(), ViewModelBinder<V, B> {
	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory
	override lateinit var viewModel: V
	override lateinit var binding: B
	override val currentFragmentManager: FragmentManager get() = requireFragmentManager()

	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: AppCompatDialogFragment = this, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)
	inline fun <reified VM : V> findViewModel(ofLifecycleOwner: FragmentActivity, factory: ViewModelProvider.Factory = viewModelFactory) = ViewModelProviders.of(ofLifecycleOwner, factory).get(VM::class.java)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel = setupViewModel()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		binding = setupBinding(inflater)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		observeBaseEvents()
	}
}
