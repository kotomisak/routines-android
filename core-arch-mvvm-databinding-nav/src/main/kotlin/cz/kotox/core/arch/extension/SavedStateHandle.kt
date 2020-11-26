package cz.kotox.core.arch.extension

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import java.io.Serializable

inline fun <reified T : Parcelable> SavedStateHandle.setList(key: String, value: List<T>) = set(key, value.toTypedArray())
inline fun <reified T : Serializable> SavedStateHandle.setSerializableList(key: String, value: List<T>) = set(key, value.toTypedArray())

inline fun <reified T : Parcelable> SavedStateHandle.getList(key: String): List<T> = get<Array<T>>(key)?.toList() ?: emptyList()
inline fun <reified T : Serializable> SavedStateHandle.getSerializedList(key: String): List<T> = get<Array<T>>(key)?.toList() ?: emptyList()