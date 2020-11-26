package cz.kotox.core.arch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.arch.di.viewModel.InjectingSavedStateViewModelFactory
import cz.kotox.core.arch.di.viewModel.ViewModelArgs
import cz.kotox.core.database.AppPreferences
import cz.kotox.core.utility.FragmentPermissionManager
import cz.kotox.core.utility.lazyUnsafe
import timber.log.Timber
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment<B : ViewDataBinding>(
	private val navigationType: NavigationType = NavigationType.NONE
) : BottomSheetDialogFragment(), BaseUIScreen {

	override val baseActivity: BaseActivity get() = requireActivity() as BaseActivity

	override var lastSnackbar: Snackbar? = null

	override val navController: NavController
		get() = baseActivity.navController

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		baseActivity.setNavigationType(navigationType)
	}

	override fun onDestroyView() {
		dismissLastSnackbar()
		super.onDestroyView()
	}
}

abstract class BaseBottomSheetDialogFragmentWithViewModel<V : BaseViewModel, B : ViewDataBinding>(
		@LayoutRes private val layoutResId: Int,
		private val navigationType: NavigationType = NavigationType.NONE,
		private val supportButtonAboveKeyboard: Boolean = false
) :
	BaseBottomSheetDialogFragment<B>(navigationType),
		ViewModelBinder<V, B>,
		FragmentDialogActions {

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

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		super.onCreateView(inflater, container, savedInstanceState)
		binding = setupBinding(inflater, layoutResId)
		Timber.d(">>>_ BOTTOM")
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

		if (supportButtonAboveKeyboard) {
			//This is complementary code to adjustResize in style.
			//https://stackoverflow.com/questions/50223392/android-show-bottomsheetdialogfragment-above-keyboard
			dialog?.setOnShowListener {
				val dialog = it as BottomSheetDialog
				val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
				bottomSheet?.let { sheet ->
					dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
					sheet.parent.parent.requestLayout()
				}
			}
		}
	}

	open fun getViewModelArgs(): ViewModelArgs = ViewModelArgs()

	override fun observe() {}
}

abstract class BasePermissionBottomSheetDialogFragmentWithViewModel<V : BaseViewModel, B : ViewDataBinding>(
		@LayoutRes private val layoutResId: Int,
		private val navigationType: NavigationType = NavigationType.NONE,
		private val supportButtonAboveKeyboard: Boolean = false
) : BaseBottomSheetDialogFragmentWithViewModel<V, B>(layoutResId, navigationType, supportButtonAboveKeyboard) {

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		permissionManager.onPermissionResult(requestCode, permissions, grantResults)
	}

	//Use member injection to not complicated constructor-parameters/inheritance
	@Inject
	lateinit var permissionAppPreferences: AppPreferences

	private val permissionManager: FragmentPermissionManager by lazyUnsafe { FragmentPermissionManager(this, permissionAppPreferences) }

	fun checkStoragePermission(grantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!permissionManager.checkStoragePermission()) {
			permissionManager.requestStoragePermissions(
				{ grantedCallback.invoke() },
				{ deniedCallback.invoke() },
				{ showAppSettingsCallback.invoke() }
			)
		} else {
			grantedCallback.invoke()
		}
	}

	fun checkCameraPermission(grantedCallback: () -> Unit = {}, deniedCallback: () -> Unit = {}, showAppSettingsCallback: () -> Unit = {}) {
		if (!permissionManager.checkCameraPermission()) {
			permissionManager.requestCameraPermissions(
				{ grantedCallback.invoke() },
				{ deniedCallback.invoke() },
				{ showAppSettingsCallback.invoke() }
			)
		} else {
			grantedCallback.invoke()
		}
	}
}