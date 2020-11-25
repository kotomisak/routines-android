package cz.kotox.template.ui

import android.os.Bundle
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
	}

}