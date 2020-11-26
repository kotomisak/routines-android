package cz.kotox.core.ktools


sealed class Either<out E, out V> {
	data class Error<out E>(val error: E) : Either<E, Nothing>()
	data class Value<out V>(val value: V) : Either<Nothing, V>()
}

// creators
fun <V> value(value: V): Either<Nothing, V> = Either.Value(value)

fun <E> error(value: E): Either<E, Nothing> = Either.Error(value)

suspend fun <V> either(action: suspend () -> V): Either<Throwable, V> =
	try {
		value(action())
	} catch (t: Exception) {
		error(t)
	}

inline infix fun <E, V, V2> Either<E, V>.map(func: (V) -> V2): Either<E, V2> = when (this) {
	is Either.Error -> this
	is Either.Value -> Either.Value(func(value))
}

inline infix fun <E, V, E2> Either<E, V>.mapError(func: (E) -> E2): Either<E2, V> = when (this) {
	is Either.Error -> Either.Error(func(error))
	is Either.Value -> this
}

inline infix fun <E, V, V2> Either<E, List<V>>.mapIterable(func: (V) -> V2): Either<E, List<V2>> = when (this) {
	is Either.Error -> this
	is Either.Value -> Either.Value(value.map(func))
}

inline infix fun <E, V, V2> Either<E, V>.flatMap(func: (V) -> Either<E, V2>): Either<E, V2> =
	when (this) {
		is Either.Error -> this
		is Either.Value -> func(value)
	}

inline infix fun <E, V, E2> Either<E, V>.flatMapError(func: (E) -> Either<E2, V>): Either<E2, V> =
	when (this) {
		is Either.Error -> func(error)
		is Either.Value -> this
	}

inline fun <E, V, V2, R> Either<E, V>.zip(second: Either<E, V2>, zipFce: (V, V2) -> Either<E, R>): Either<E, R> =
	when (val first = this) {
		is Either.Error -> first
		is Either.Value -> {
			when (second) {
				is Either.Error -> second
				is Either.Value -> zipFce(first.value, second.value)
			}
		}
	}

inline fun <E, V, A> Either<E, V>.fold(e: (E) -> A, v: (V) -> A): A = when (this) {
	is Either.Error -> e(this.error)
	is Either.Value -> v(this.value)
}
