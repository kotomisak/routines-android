package cz.kotox.core.arch

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

fun Fragment.showExistingDialogOrCreateNew(fragmentTag: String, createDialogInstance: () -> DialogFragment) {
	val fragmentTransaction = requireFragmentManager().beginTransaction()
	val originalFragment = requireFragmentManager().findFragmentByTag(fragmentTag)?.let { fragmentTransaction.remove(it) }
	if (originalFragment == null) {
		createDialogInstance().show(fragmentTransaction, fragmentTag)
	} else {
		if (originalFragment is DialogFragment) {
			originalFragment.show(fragmentTransaction, fragmentTag)
		}
	}
}

fun Fragment.removeDialogByTag(fragmentTag: String) {
	val fragmentTransaction = requireFragmentManager().beginTransaction()
	requireFragmentManager().findFragmentByTag(fragmentTag)?.let { fragmentTransaction.remove(it) }
	fragmentTransaction.commitAllowingStateLoss()
}

fun Fragment.dismissDialog(fragmentTag: String) {
	requireFragmentManager().findFragmentByTag(fragmentTag)?.let {
		if (it is DialogFragment) {
			it.dismiss()
		}
	}
}

fun Fragment.showDialogInTransaction(fragmentTag: String, dialogFragment: DialogFragment) =
	showDialogInTransaction(requireFragmentManager(), fragmentTag, dialogFragment)

fun FragmentActivity.showDialogInTransaction(fragmentTag: String, dialogFragment: DialogFragment) =
	showDialogInTransaction(supportFragmentManager, fragmentTag, dialogFragment)

fun showDialogInTransaction(fragmentManager: FragmentManager, fragmentTag: String, dialogFragment: DialogFragment) {
	val fragmentTransaction = fragmentManager.beginTransaction()
	fragmentManager.findFragmentByTag(fragmentTag)?.let { fragmentTransaction.remove(it) }
	dialogFragment.show(fragmentTransaction, fragmentTag)
}
