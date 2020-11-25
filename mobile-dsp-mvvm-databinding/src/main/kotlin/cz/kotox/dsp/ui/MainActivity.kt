package cz.kotox.dsp.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cz.kotox.core.arch.BaseActivity
import cz.kotox.dsp.R
import cz.kotox.core.database.PreferencesCommon
import javax.inject.Inject

class MainActivity : BaseActivity() {

	@Inject
	lateinit var preferencesCommon: PreferencesCommon

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
	}

}