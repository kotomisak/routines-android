package cz.kotox.core.arch

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.arch.SNACK_BAR_MAX_LINES_DEFAULT
import cz.kotox.core.liveevent.Event

class ShowError(
	val message: String? = null,
	@StringRes val messageResId: Int? = null,
	val action: (Pair<Int, () -> Unit>)? = null,
	val length: Int = Snackbar.LENGTH_LONG,
	val maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT
) : Event()

object Finish : Event()

data class ShowProgress(@StringRes val title: Int) : Event()
object HideProgress : Event()

data class ShowResult(@DrawableRes val icon: Int, @StringRes val title: Int, @StringRes val description: Int? = null) : Event()
object HideResult : Event()

object HideKeyboard : Event()

object ShowOfflineDialog : Event()
