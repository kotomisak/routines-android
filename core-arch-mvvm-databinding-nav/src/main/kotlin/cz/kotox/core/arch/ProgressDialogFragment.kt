package cz.kotox.core.arch

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.StringRes
import cz.kotox.core.arch.databinding.ProgressDialogFragmentBinding

private const val ARG_TITLE = "title"

class ProgressDialogFragment : BaseDialogFragmentWithBinding<ProgressDialogFragmentBinding>(R.layout.progress_dialog_fragment) {

	companion object {
		fun newInstance(@StringRes title: Int) =
			ProgressDialogFragment().apply {
				arguments = Bundle().apply {
					putInt(ARG_TITLE, title)
				}
			}
	}

	override val cancelable = true

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return super.onCreateDialog(savedInstanceState).apply {
			window?.setBackgroundDrawableResource(android.R.color.transparent)

			binding.progressTitle.text=  getString(requireNotNull(arguments).getInt(ARG_TITLE))
		}
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		if (dialog != null) requireNotNull(dialog).setCanceledOnTouchOutside(false)
	}

	override fun onCancel(dialog: DialogInterface) {
		super.onCancel(dialog)

		activity?.onBackPressed()
	}
}