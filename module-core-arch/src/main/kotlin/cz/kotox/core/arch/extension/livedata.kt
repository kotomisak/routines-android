package cz.kotox.core.arch.extension

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

/**
 * Live Data variation used for event-based communication from ViewModel to Activity/Fragment
 *
 * Simply create an instance in ViewModel, observe the instance in Activity/Fragment the same way as any other LiveData and when you need to trigger the event,
 * call @see LiveAction.publish(T).
 */
class EventLiveData<T> : MutableLiveData<T>() {
	private var pending = false

	override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
		if (hasActiveObservers()) {
			Log.w("LiveAction", "Multiple observers registered but only one will be notified of changes.")
		}

		// Observe the internal MutableLiveData
		super.observe(owner, Observer {
			if (pending) {
				pending = false
				observer.onChanged(it)
			}
		})
	}

	override fun setValue(t: T?) {
		pending = true
		super.setValue(t)
	}

	fun publish(value: T) {
		postValue(value)
	}
}

/**
 * Shorthand for LiveAction where you don't need to pass any value
 */
fun EventLiveData<Unit>.publish() {
	publish(Unit)
}

/**
 * Shorthand for adding source to MediatorLiveData and assigning its value - great for validators, chaining live data etc.
 */
fun <S, T> MediatorLiveData<T>.addValueSource(source: LiveData<S>, resultFunction: (sourceValue: S?) -> T) = this.apply { addSource(source, { value = resultFunction(it) }) }

/**
 * Shorthand for mapping LiveData instead of using static methods from Transformations
 */
fun <S, T> LiveData<T>.map(mapFunction: (T) -> S) = Transformations.map(this, mapFunction)

fun <S, T> LiveData<List<T>>.mapList(mapFunction: (T) -> S) = map { it.map(mapFunction) }

/**
 * Shorthand for switch mapping LiveData instead of using static methods from Transformations
 */
fun <S, T> LiveData<T>.switchMap(switchMapFunction: (T) -> LiveData<S>) = Transformations.switchMap(this, switchMapFunction)

fun <S, T> LiveData<T?>.switchMapNotNull(switchMapFunction: (T) -> LiveData<S>) = Transformations.switchMap(this) { if (it != null) switchMapFunction.invoke(it) else MutableLiveData<S>() }

/**
 * Shorthand for creating MutableLiveData
 */
fun <T> mutableLiveDataOf(value: T) = MutableLiveData<T>().apply { this.value = value }

fun <T> liveDataOf(value: T) = MutableLiveData<T>().apply { this.value = value }

fun <T> LiveData<T>.observeOnce(observer: Observer<T>, onlyIf: (T?) -> Boolean = { true }) {
	observeForever(object : Observer<T> {
		override fun onChanged(value: T?) {
			if (onlyIf(value)) {
				removeObserver(this)
				observer.onChanged(value)
			}
		}

	})
}

fun <T> combineLiveData(vararg input: LiveData<out Any?>, combineFunction: () -> T): LiveData<T> = MediatorLiveData<T>().apply {
	input.forEach { addSource(it) { value = combineFunction() } }
}

fun <T> combineMediatorLiveData(vararg input: LiveData<out Any?>, combineFunction: () -> T): MediatorLiveData<T> = MediatorLiveData<T>().apply {
	input.forEach { addSource(it) { value = combineFunction() } }
}

fun <T> MutableLiveData<T>.refresh() {
	value = value
}

inline fun <T> MutableLiveData<T>.updateValue(update: (T?) -> T?) = apply {
	postValue(update(value))
}

fun <T> LiveData<T>.doOnNext(block: (T) -> Unit) = MediatorLiveData<T>().apply {
	addSource(this@doOnNext) { newValue ->
		block(newValue)
		postValue(newValue)
	}
}