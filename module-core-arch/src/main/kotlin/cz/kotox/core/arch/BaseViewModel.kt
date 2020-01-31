package cz.kotox.core.arch

import android.annotation.SuppressLint
import android.content.Context
import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import cz.kotox.core.OpenForMocking
import cz.kotox.core.arch.liveevent.Event
import cz.kotox.core.arch.liveevent.LiveBus
import cz.kotox.core.utility.isOnline
import cz.kotox.core.utility.logWithTag
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OpenForMocking
abstract class BaseViewModel : ViewModel(), NotifyObservable by BaseViewObservable() {
	val compositeDisposable = CompositeDisposable()

	@SuppressLint("StaticFieldLeak")
	@Inject
	open lateinit var appContext: Context

	private val liveEventsBus = LiveBus()

	init {
		logWithTag(javaClass.simpleName, "init")
	}

	override fun onCleared() {
		logWithTag(javaClass.simpleName, "onCleared")
		compositeDisposable.dispose()
	}

	/**
	 * This method runs [doBefore] action immediately and then after [delayMillis] runs [doAfter] action.
	 * All actions are stored in [compositeDisposable] and disposed when [onCleared].
	 *
	 * Return the disposable here and dispose it manually before each call if we need only one action at a time.
	 */
	fun runAfterDelay(delayMillis: Long = 1500, doBefore: (() -> Unit)? = null, doAfter: () -> Unit) {
		compositeDisposable += Single.just(true)
			.delay(delayMillis, TimeUnit.MILLISECONDS)
			.observeOn(AndroidSchedulers.mainThread())
			.doOnSubscribe { doBefore?.invoke() }
			.doOnSuccess { doAfter() }
			.subscribe()
	}

	open fun isOnline(): Boolean = isOnline(appContext)

	open fun isOnlineShowDialog(): Boolean {
		if (!isOnline()) {
			sendEvent(ShowNoConnectionDialog())
			return false
		}
		return true
	}

	open val statefulTrackingScreens: Map<Int, String> = emptyMap()



	fun sendEvent(event: Event) {
		liveEventsBus.send(event)
	}

	fun <T : Event> observeEvent(owner: LifecycleOwner, eventClass: Class<T>, observer: Observer<T>) {
		liveEventsBus.observe(owner, eventClass, observer)
	}
}

interface NotifyObservable : Observable {
	fun notifyChange()
	fun notifyPropertyChanged(fieldId: Int)
}

/**
 * Must be done this way with intermediary interface [NotifyObservable] because [Observable] interface
 * doesn't cover [notifyPropertyChanged] and [notifyChange] methods.
 */
class BaseViewObservable : BaseObservable(), NotifyObservable
