package cz.kotox.core.arch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.utility.logWithTag
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), BaseView, BaseUIScreen, HasAndroidInjector {

	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

	override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

	override val baseActivity: BaseActivity get() = this
	override var lastSnackbar: Snackbar? = null

	public override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
	}

	public override fun onNewIntent(intent: Intent) {
		logWithTag(javaClass.simpleName, "onNewIntent")
		super.onNewIntent(intent)
	}

	public override fun onResume() {
		super.onResume()
		//analytics.trackView(baseActivity, analyticScreenName, javaClass.simpleName)
	}

	public override fun onDestroy() {
		dismissLastSnackbar()
		super.onDestroy()
	}

	override fun finish() {
		super<AppCompatActivity>.finish()
	}
}