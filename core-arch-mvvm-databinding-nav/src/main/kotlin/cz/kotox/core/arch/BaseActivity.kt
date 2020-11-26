package cz.kotox.core.arch

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.NavigationRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

abstract class BaseActivity(
	@LayoutRes private val layoutResId: Int,
	@IdRes val navHostFragmentId: Int,
	@NavigationRes private val navGraphResourceId: Int
) : AppCompatActivity(), BaseUIScreen {

	override val baseActivity: BaseActivity get() = this
	override var lastSnackbar: Snackbar? = null
	override val navController: NavController get() = Navigation.findNavController(this, navHostFragmentId)

	private var toolbarHashCode = 0

	override fun setTheme(resId: Int) {
		super.setTheme(resId)
		setupWindow(resId)
	}

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setupView()
	}

	internal open fun setupView() {
		setContentView(layoutResId)
		setupActionBar()
	}

	public override fun onNewIntent(intent: Intent) {
		Timber.v(this.javaClass.simpleName)
		super.onNewIntent(intent)
	}

	public override fun onDestroy() {
		dismissLastSnackbar()
		super.onDestroy()
	}

	override fun finish() {
		super<AppCompatActivity>.finish()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		item.onNavDestinationSelected(navController) ||
			when (item.itemId) {
				android.R.id.home -> {
					onBackPressed()
					true
				}
				else -> super.onOptionsItemSelected(item)
			}

	internal fun setupActionBar() {
		navController.apply {
			setGraph(navGraphResourceId, intent.extras)
			val appBarConfiguration = AppBarConfiguration.Builder(this.graph).build()

			//Fixme add toolbars
//			findViewById<Toolbar>(R.id.toolbar)?.let {
//				it.setupWithNavController(this, appBarConfiguration)
//				setSupportActionBar(it)
//			}
		}
	}

	fun setToolbarTitleColor(@ColorRes color: Int) {

		//Fixme add toolbars
//		findViewById<Toolbar>(R.id.toolbar)?.apply {
//			setTitleTextColor(ContextCompat.getColor(this@BaseActivity, color))
//		}
	}

	fun setToolbarTitle(@StringRes titleResId: Int, vararg formatArgs: Any?) {

		//Fixme add toolbars
//		findViewById<Toolbar>(R.id.toolbar)?.apply {
//			title = getString(titleResId, *formatArgs)
//		}
	}

	fun setNavigationType(navigationType: NavigationType, @ColorRes color: Int? = null) {

		//Fixme add toolbars
//		findViewById<Toolbar>(R.id.toolbar)?.apply {
//			navigationIcon = when (navigationType) {
//				NavigationType.CLOSE -> {
//					AppCompatResources.getDrawable(baseActivity, R.drawable.abc_ic_clear_material)?.apply {
//						DrawableCompat.setTint(this, ContextCompat.getColor(baseActivity, color ?: R.color.color_on_background))
//					}
//				}
//				NavigationType.UP -> {
//					AppCompatResources.getDrawable(baseActivity, R.drawable.abc_ic_ab_back_material)?.apply {
//						DrawableCompat.setTint(this, ContextCompat.getColor(baseActivity, color ?: R.color.color_on_background))
//					}
//				}
//				NavigationType.NONE -> {
//					null
//				}
//			}
//		}
	}

	fun showToolbar(show: Boolean) {
		//Fixme add toolbars
//		findViewById<Toolbar>(R.id.toolbar)?.apply {
//			visibility = if(show) View.VISIBLE else View.GONE
//		}
	}

	fun setDefaultSystemBars(darkStatusBar: Boolean) {
		val fullscreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

		window.apply {
			// Older Android versions have only white icons in the navigation and status bar, so color_primary is used for both bars.
			when {
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
					// Change navigation bar to solid color
					clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
					navigationBarColor = ContextCompat.getColor(context, R.color.color_navigation_bar)
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
						navigationBarDividerColor = ContextCompat.getColor(context, R.color.color_navigation_bar)
					}

					// Change system bars icons to dark color
					decorView.systemUiVisibility = fullscreenFlags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
					if (!darkStatusBar) {
						// Change status bar icons to dark color
						decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
					}
				}
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
					// Change status bar icons to dark color
					decorView.systemUiVisibility = fullscreenFlags
					if (!darkStatusBar) {
						// Change status bar icons to dark color
						decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
					}
				}
				else -> {
					// Change status bar to translucent
					addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
				}
			}
		}
	}

	fun setTranslucentNavigationBar(darkStatusBar: Boolean) {
		val fullscreenFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

		window.apply {
			// Older Android versions have only white icons in the navigation and status bar, so color_primary is used for both bars.
			when {
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 -> {
					// Change navigation bar to translucent
					navigationBarColor = Color.TRANSPARENT
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
						navigationBarDividerColor = Color.TRANSPARENT
					}
					addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

					// Change navigation bar icons to dark color
					decorView.systemUiVisibility = fullscreenFlags
					if (!darkStatusBar) {
						// Change status bar icons to dark color
						decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
					}
				}
				Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
					if (darkStatusBar) {
						// Change status bar icons to light color
						decorView.systemUiVisibility = fullscreenFlags
					} else {
						// Change status bar icons to dark color
						decorView.systemUiVisibility = fullscreenFlags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
					}
				}
				else -> {
					if (darkStatusBar) {
						// Change status bar to transparent
						clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
					} else {
						// Change status bar to translucent
						addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
					}
				}
			}
		}
	}

	private fun setupWindow(@StyleRes themeId: Int) {
		// Do nothing for Launcher theme. It would break it.
		if (themeId != R.style.Soulvibe_Launcher) {
			window.apply {
				decorView.systemUiVisibility = decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
			}
		}
	}
}