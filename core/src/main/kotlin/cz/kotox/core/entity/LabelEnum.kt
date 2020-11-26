package cz.kotox.core.entity

import android.content.Context

interface LabelEnum {

	val resource: Int
	fun getLabel(context: Context): String = context.getString(this.resource)
}