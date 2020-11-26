package cz.kotox.core.arch

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import cz.kotox.core.arch.databinding.ResultDialogFragmentBinding

private const val ARG_ICON = "icon"
private const val ARG_TITLE = "title"
private const val ARG_DESCRIPTION = "description"

class ResultDialogFragment : BaseDialogFragmentWithBinding<ResultDialogFragmentBinding>(R.layout.result_dialog_fragment) {

	companion object {
		fun newInstance(@DrawableRes icon: Int, @StringRes title: Int, @StringRes description: Int? = null) =
			ResultDialogFragment().apply {
				arguments = Bundle().apply {
					putInt(ARG_ICON, icon)
					putInt(ARG_TITLE, title)
					if (description != null) putInt(ARG_DESCRIPTION, description)
				}
			}
	}

	override val cancelable = true

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		return super.onCreateDialog(savedInstanceState).apply {
			window?.setBackgroundDrawableResource(android.R.color.transparent)

			binding.icon.setImageDrawable(ContextCompat.getDrawable(context, requireNotNull(arguments).getInt(ARG_ICON)))
			binding.title.text = getString(requireNotNull(arguments).getInt(ARG_TITLE))
			if(arguments?.containsKey(ARG_DESCRIPTION) == true) {
				binding.description.apply {
					text = getString(requireNotNull(arguments).getInt(ARG_DESCRIPTION))
					visibility = View.VISIBLE
				}
			}
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