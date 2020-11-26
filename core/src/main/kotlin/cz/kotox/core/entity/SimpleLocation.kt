package cz.kotox.core.entity

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class SimpleLocation(
	val name: String,
	val longitude: Double?,
	val latitude: Double?
) : Parcelable