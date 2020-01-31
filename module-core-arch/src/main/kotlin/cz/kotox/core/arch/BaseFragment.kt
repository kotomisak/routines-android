package cz.kotox.core.arch

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.R
import cz.kotox.core.di.Injectable
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView, BaseUIScreen, Injectable {

	override val baseActivity: BaseActivity
		get() = activity as? BaseActivity
			?: throw IllegalStateException("No activity in this fragment, can't finish")


	@Inject
	lateinit var appInterface: AppInterface //Use e.g. to redirectToLogin

	override fun getContext(): Context = super.getContext()!!

	override var lastSnackbar: Snackbar? = null

	override fun onAttach(context: Context) {
		AndroidSupportInjection.inject(this)
		super.onAttach(context)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onResume() {
		super.onResume()
		//analytics.trackView(baseActivity, analyticScreenName, javaClass.simpleName)
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


	fun trackView(viewName: String) {
		//analytics.trackView(baseActivity, viewName, javaClass.simpleName)
	}

}