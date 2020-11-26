package cz.kotox.core.arch

import android.content.Context
import androidx.fragment.app.Fragment
import cz.kotox.core.arch.databinding.OfflineDialogFragmentBinding

interface OfflineDialogView {
	fun onRetry()
	fun onDismiss()
}

interface OfflineClickListener {
	fun onRetry()
}

class OfflineDialog
	: BaseDialogFragmentWithBinding<OfflineDialogFragmentBinding>(R.layout.offline_dialog_fragment),
		OfflineDialogView {

	companion object {
		fun newInstance(fragment: Fragment) = OfflineDialog().apply {
			setTargetFragment(fragment, 0)
		}
	}

	lateinit var listener: OfflineClickListener

	override val cancelable = true

	override fun onAttach(context: Context) {
		super.onAttach(context)
		val fragment = targetFragment

		//This way of settings listener solves situation when system creates dialogFragment automatically
		if (fragment is OfflineClickListener) {
			listener = fragment
		} else {
			throw RuntimeException(" $fragment must implement ${OfflineClickListener::class.java.name} !")
		}
	}

	override fun onRetry() {
		dismiss()
		listener.onRetry()
	}

	override fun onDismiss() {
		dismiss()
	}
}
