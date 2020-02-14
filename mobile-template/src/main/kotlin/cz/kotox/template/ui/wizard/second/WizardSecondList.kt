package cz.kotox.template.ui.wizard.second

import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleObserver
import androidx.recyclerview.widget.DiffUtil
import cz.kotox.core.arch.ktools.DataBoundAdapter
import cz.kotox.template.BR
import cz.kotox.template.R
import cz.kotox.template.databinding.WizardSecondListFragmentBinding
import cz.kotox.template.model.ValueSample
import cz.kotox.template.ui.wizard.BaseWizardFragment
import cz.kotox.template.ui.wizard.BaseWizardViewModel
import javax.inject.Inject

interface WizardSecondListView {
	val resultListAdapter: DataBoundAdapter<ValueSample>
}

class AnalyzerResultListFragment : BaseWizardFragment<WizardSecondListViewModel, WizardSecondListFragmentBinding>(), WizardSecondListView {

	override val resultListAdapter: DataBoundAdapter<ValueSample> = DataBoundAdapter(this, R.layout.item_value_sample, BR.item, object : DiffUtil.ItemCallback<ValueSample>() {
		override fun areItemsTheSame(oldItem: ValueSample, newItem: ValueSample): Boolean = oldItem.isItemTheSame(newItem)
		override fun areContentsTheSame(oldItem: ValueSample, newItem: ValueSample): Boolean = oldItem == newItem
	})

	override fun inflateBindingLayout(inflater: LayoutInflater) = WizardSecondListFragmentBinding.inflate(inflater)
	//
	override fun setupWizardViewModel() = findViewModel<WizardSecondListViewModel>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(viewModel)
	}

	companion object {
		fun newInstance() = AnalyzerResultListFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}
}

class WizardSecondListViewModel @Inject constructor() : BaseWizardViewModel(), LifecycleObserver {

}
