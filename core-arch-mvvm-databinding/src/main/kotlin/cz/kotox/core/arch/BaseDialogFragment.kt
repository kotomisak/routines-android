package cz.kotox.core.arch

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.arch.extension.materialDialog
import cz.kotox.core.arch.extension.view

abstract class BaseDialogFragment : AppCompatDialogFragment(), BaseUIScreen {
	override val baseActivity
		get() = activity as? BaseActivity
			?: throw IllegalStateException("No activity in this fragment, can't finish")

	override val navController: NavController
		get() = baseActivity.navController

	override var lastSnackbar: Snackbar? = null

	open val cancelable = true

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		isCancelable = cancelable
		retainInstance = !cancelable
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		// cancelable on touch outside
		if (dialog != null) requireNotNull(dialog).setCanceledOnTouchOutside(cancelable)
	}

	override fun onDestroyView() {
		// http://code.google.com/p/android/issues/detail?id=17423
		if (dialog != null && retainInstance) requireNotNull(dialog).setDismissMessage(null)

		dismissLastSnackbar()
		super.onDestroyView()
	}
}

abstract class BaseDialogFragmentWithBinding<B : ViewDataBinding>(@LayoutRes val layoutResId: Int) : BaseDialogFragment() {

	val binding: B by lazy {
		DataBindingUtil.inflate<B>(LayoutInflater.from(activity), layoutResId, null, false)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = binding.root

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
		requireContext().materialDialog {
			this.view = binding.root
		}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.lifecycleOwner = viewLifecycleOwner
		binding.setVariable(BR.view, this@BaseDialogFragmentWithBinding)
	}

}
