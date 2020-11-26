package cz.kotox.core.arch.ktools

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import cz.kotox.core.ktools.combineLiveData
import cz.kotox.core.ktools.map
import cz.kotox.core.ktools.mutableLiveDataOf

class Form {
	val valid = MediatorLiveData<Boolean>().apply { value = true }
	private val sources = mutableSetOf<LiveData<Boolean>>()
	fun <T> addField(field: FormField<T>) {
		sources += field.valid
		valid.addSource(field.valid) { valid.value = sources.all { it.value ?: false } }
	}
}

class FormField<T>(defaultValue: T?, parentForm: Form? = null) {
	private val validators: MutableList<(T?) -> Int?> = mutableListOf()
	val value: MutableLiveData<T?> = mutableLiveDataOf(defaultValue)
	private val showError: MutableLiveData<Boolean> = mutableLiveDataOf(false)

	private val errorMessageResIntern: LiveData<Int?> = value.map { v ->
		if (showError.value == false && v != null) showError.value = true

		validators.map { it.invoke(v) }.filterNotNull().firstOrNull()
	}

	val errorMessageRes: LiveData<Int?> = combineLiveData(showError, errorMessageResIntern) {
		if (showError.value == true) {
			errorMessageResIntern.value
		} else {
			null
		}
	}

	val valid: LiveData<Boolean> = errorMessageResIntern.map { it == null }

	init {
		parentForm?.addField(this)
	}

	fun validator(validator: (value: T?) -> Int?) = this.apply {
		validators += validator
		value.value = value.value // refresh validation
	}
}