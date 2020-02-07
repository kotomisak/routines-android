package cz.kotox.core.arch.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> Fragment.bind(liveData: LiveData<T>, observer: (T) -> Unit) =
	liveData.observe(viewLifecycleOwner, Observer(observer))

fun <T> Fragment.bindRead(liveData: LiveData<T>, observer: (T) -> Unit) =
	liveData.observeOnce(Observer(observer))