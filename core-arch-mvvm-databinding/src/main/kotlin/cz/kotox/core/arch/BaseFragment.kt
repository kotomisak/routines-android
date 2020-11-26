package cz.kotox.core.arch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
//import com.google.firebase.analytics.FirebaseAnalytics

enum class NavigationType { NONE, UP, CLOSE }

abstract class BaseFragment(
	private val navigationType: NavigationType = NavigationType.NONE
) : Fragment(), BaseUIScreen, FragmentDialogActions {

	override val baseActivity: BaseActivity
		get() = activity as? BaseActivity
			?: throw IllegalStateException("No activity in this fragment, can't finish")

	override var lastSnackbar: Snackbar? = null

	override val navController: NavController
		get() = baseActivity.navController

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onResume() {
		super.onResume()

		//Fixme keep just invocation and move implementation to analytics fragment
		//FirebaseAnalytics.getInstance(baseActivity).setCurrentScreen(baseActivity, this.javaClass.simpleName, this.javaClass.simpleName)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		baseActivity.setNavigationType(navigationType)
	}

	override fun onDestroyView() {
		dismissLastSnackbar()
		super.onDestroyView()
	}

	/**
	 * Serves only as shortcut to activity.finish()
	 * WARNING: has to be final, otherwise children fragments could override and think that the code will be called on finish
	 */
	final override fun finish() = super.finish()
}
