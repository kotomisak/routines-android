package cz.kotox.core.ktools

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations

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

fun <T> liveDataOf(value: T): LiveData<T> = MutableLiveData<T>().apply { this.value = value }

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

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: (T) -> Unit) = this.observe(lifecycleOwner, Observer(observer))

fun <A, B> zipLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A, B>> {
	return MediatorLiveData<Pair<A, B>>().apply {
		var lastA: A? = null
		var lastB: B? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			if (localLastA != null && localLastB != null)
				this.value = Pair(localLastA, localLastB)
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
	}
}

fun <A, B> applyFirstLiveData(a: LiveData<A>, b: LiveData<B>): LiveData<Pair<A?, B?>> {
	return MediatorLiveData<Pair<A?, B?>>().apply {
		var lastA: A? = null
		var lastB: B? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			this.value = Pair(localLastA, localLastB)
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
	}
}

/*TODO refactor this with warargs, I just have no time to play with that*/
fun zipBitwiseResults(a: LiveData<Boolean>, b: LiveData<Boolean>): LiveData<Boolean> {
	return MediatorLiveData<Boolean>().apply {
		var lastA: Boolean? = null
		var lastB: Boolean? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			if (localLastA != null && localLastB != null)
				this.value = localLastA && localLastB
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
	}
}

/*TODO refactor this with warargs, I just have no time to play with that*/
fun zipBitwiseResults(a: LiveData<Boolean>, b: LiveData<Boolean>, c: LiveData<Boolean>): LiveData<Boolean> {
	return MediatorLiveData<Boolean>().apply {
		var lastA: Boolean? = null
		var lastB: Boolean? = null
		var lastC: Boolean? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			val localLastC = lastC
			if (localLastA != null && localLastB != null && localLastC != null)
				this.value = localLastA && localLastB && localLastC
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
		addSource(c) {
			lastC = it
			update()
		}
	}
}

/*TODO refactor this with warargs, I just have no time to play with that*/
fun zipBitwiseResults(a: LiveData<Boolean>, b: LiveData<Boolean>, c: LiveData<Boolean>, d: LiveData<Boolean>, e: LiveData<Boolean>): LiveData<Boolean> {
	return MediatorLiveData<Boolean>().apply {
		var lastA: Boolean? = null
		var lastB: Boolean? = null
		var lastC: Boolean? = null
		var lastD: Boolean? = null
		var lastE: Boolean? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			val localLastC = lastC
			val localLastD = lastD
			val localLastE = lastE
			if (localLastA != null && localLastB != null && localLastC != null && localLastD != null && localLastE != null)
				this.value = localLastA && localLastB && localLastC && localLastD && localLastE
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
		addSource(c) {
			lastC = it
			update()
		}
		addSource(d) {
			lastD = it
			update()
		}
		addSource(e) {
			lastE = it
			update()
		}
	}
}

/*TODO refactor this with warargs, I just have no time to play with that*/
fun zipBitwiseResults(a: LiveData<Boolean>, b: LiveData<Boolean>, c: LiveData<Boolean>, d: LiveData<Boolean>, e: LiveData<Boolean>, f: LiveData<Boolean>): LiveData<Boolean> {
	return MediatorLiveData<Boolean>().apply {
		var lastA: Boolean? = null
		var lastB: Boolean? = null
		var lastC: Boolean? = null
		var lastD: Boolean? = null
		var lastE: Boolean? = null
		var lastF: Boolean? = null

		fun update() {
			val localLastA = lastA
			val localLastB = lastB
			val localLastC = lastC
			val localLastD = lastD
			val localLastE = lastE
			val localLastF = lastF

			if (localLastA != null && localLastB != null && localLastC != null && localLastD != null && localLastE != null && localLastF != null)
				this.value = localLastA && localLastB && localLastC && localLastD && localLastE && localLastF
		}

		addSource(a) {
			lastA = it
			update()
		}
		addSource(b) {
			lastB = it
			update()
		}
		addSource(c) {
			lastC = it
			update()
		}
		addSource(d) {
			lastD = it
			update()
		}
		addSource(e) {
			lastE = it
			update()
		}
		addSource(f) {
			lastF = it
			update()
		}
	}
}
