package cz.kotox.core.entity

import android.os.Parcel
import android.os.Parcelable
import android.text.SpannableString
import android.text.TextUtils
import androidx.annotation.Keep

/**
 *  Wrapper entity workaround for passing Spanned via varargs.
 *
 *  Original inspiration here, but it's working across fragments only : https://stackoverflow.com/questions/10566486/android-how-to-persistently-store-a-spanned
 *  Trick finally came from here, which is working also across activities: https://stackoverflow.com/questions/30274400/how-to-write-spannablestring-to-parcel
 *
 *  @Parcelize doesn't work here :`-(
 */
@Keep
class ParcelableSpanned(
	val spannableString: SpannableString?
) : Parcelable {
	constructor(parcel: Parcel) : this(parcel.readSpannableString())

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeSpannableString(spannableString, flags)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ParcelableSpanned> {
		override fun createFromParcel(parcel: Parcel): ParcelableSpanned {
			return ParcelableSpanned(parcel)
		}

		override fun newArray(size: Int): Array<ParcelableSpanned?> {
			return arrayOfNulls(size)
		}
	}
}

fun Parcel.writeSpannableString(spannableString: SpannableString?, flags: Int) {
	TextUtils.writeToParcel(spannableString, this, flags)
}

fun Parcel.readSpannableString(): SpannableString? {
	return TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(this) as SpannableString?
}