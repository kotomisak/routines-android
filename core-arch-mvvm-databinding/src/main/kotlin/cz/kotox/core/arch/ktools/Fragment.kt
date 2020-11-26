package cz.kotox.core.arch.ktools

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import cz.kotox.core.ktools.observeOnce

fun <T> Fragment.bind(liveData: LiveData<T>, observer: (T) -> Unit) =
	liveData.observe(viewLifecycleOwner, Observer(observer))

fun <T> Fragment.bindRead(liveData: LiveData<T>, observer: (T) -> Unit) =
	liveData.observeOnce(Observer(observer))