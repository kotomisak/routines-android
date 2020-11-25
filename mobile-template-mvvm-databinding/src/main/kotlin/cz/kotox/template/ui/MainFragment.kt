package cz.kotox.template.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.Navigation
import cz.kotox.template.R
import cz.kotox.core.arch.BaseFragmentViewModel
import cz.kotox.template.databinding.MainFragmentBinding

/**
 * Fragment used to show how to navigate to another destination
 */
class MainFragment : BaseFragmentViewModel<MainViewModel, MainFragmentBinding>() {

	override fun inflateBindingLayout(inflater: LayoutInflater) = MainFragmentBinding.inflate(inflater)

	override fun setupViewModel() = findViewModel<MainViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.navigateAnalyzeBt.setOnClickListener {
			Navigation.findNavController(view).navigate(R.id.navigate_to_analyzer)

		}

	}
}
