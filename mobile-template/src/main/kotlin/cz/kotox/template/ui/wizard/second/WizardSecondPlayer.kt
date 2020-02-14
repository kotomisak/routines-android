package cz.kotox.template.ui.wizard.second

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LifecycleObserver
import cz.kotox.core.arch.liveevent.Event
import cz.kotox.core.arch.observeEvent
import cz.kotox.template.databinding.WizardSecondPlayerFragmentBinding
import cz.kotox.template.ui.wizard.BaseWizardFragment
import cz.kotox.template.ui.wizard.BaseWizardViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

class WizardSecondPlayerFragment : BaseWizardFragment<WizardSecondPlayerViewModel, WizardSecondPlayerFragmentBinding>() {

	override fun inflateBindingLayout(inflater: LayoutInflater) = WizardSecondPlayerFragmentBinding.inflate(inflater)

	override fun setupWizardViewModel() = findViewModel<WizardSecondPlayerViewModel>()

	@ExperimentalCoroutinesApi
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding.playBt.setOnClickListener {
			startPlayer()
		}

		binding.stopBt.isEnabled = false
		binding.stopBt.setOnClickListener {
			stopPlayer()
		}

		observeEvent<StopPlayerEvent> { stopPlayer() }
	}

	@ExperimentalCoroutinesApi
	private fun startPlayer() {
		binding.playBt.isEnabled = false
		viewModel.play()
		binding.stopBt.isEnabled = true
	}

	private fun stopPlayer() {
		binding.stopBt.isEnabled = false
		viewModel.stop()
		binding.playBt.isEnabled = true
	}

	override fun onPause() {
		stopPlayer()
		super.onPause()
	}

	companion object {
		fun newInstance() = WizardSecondPlayerFragment().apply {
			val bundle = Bundle()
			arguments = bundle
		}
	}
}

class WizardSecondPlayerViewModel @Inject constructor() : BaseWizardViewModel(), LifecycleObserver {

	@ExperimentalCoroutinesApi
	fun play() {
		Timber.d(">>> PLAYER PLAY")
	}

	fun stop() {
		Timber.d(">>> PLAYER STOP")
	}
}

object StopPlayerEvent : Event()