package cz.kotox.template.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cz.kotox.core.arch.BaseActivity
import cz.kotox.template.R
import cz.kotox.core.database.PreferencesCommon
import javax.inject.Inject

class MainActivity : BaseActivity() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)

		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		setSupportActionBar(toolbar)

		val host: NavHostFragment = supportFragmentManager
			.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return

		// Set up Action Bar
		val navController = host.navController
		setupActionBar(navController)

		//TODO fix this
//		navController.addOnNavigatedListener { _, destination ->
//			val dest: String = try {
//				resources.getResourceName(destination.id)
//			} catch (e: Resources.NotFoundException) {
//				Integer.toString(destination.id)
//			}
//
//			Timber.d("Navigated to %s", dest)
//		}

		//Timber.d(">>>${preferencesCore.sampleToken}")
	}

	private fun setupActionBar(navController: NavController) {
		NavigationUI.setupActionBarWithNavController(this, navController)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		val retValue = super.onCreateOptionsMenu(menu)
		menuInflater.inflate(R.menu.menu_overflow, menu)
		return retValue
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Have the NavHelper look for an action or destination matching the menu
		// item id and navigate there if found.
		// Otherwise, bubble up to the parent.
		return NavigationUI.onNavDestinationSelected(item,
			Navigation.findNavController(this, R.id.my_nav_host_fragment))
			|| super.onOptionsItemSelected(item)
	}

}