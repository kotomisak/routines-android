package cz.kotox.core.arch

import com.google.android.material.snackbar.Snackbar
import cz.kotox.core.arch.liveevent.Event

class ShowToastEvent(val message: String) : Event()
class ShowSnackbarEvent(val message: String, val action: (Pair<Int, () -> Unit>)? = null, val length: Int = Snackbar.LENGTH_INDEFINITE, val maxLines: Int = SNACK_BAR_MAX_LINES_DEFAULT) : Event()
class HideSnackbarEvent : Event()
class FinishEvent : Event()
class ShowNoConnectionDialog : Event()
object TimezoneChangeEvent : Event()
