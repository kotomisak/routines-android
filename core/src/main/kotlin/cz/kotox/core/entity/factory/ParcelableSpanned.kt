package cz.kotox.core.entity.factory

import android.text.SpannableString
import android.text.Spanned
import androidx.core.text.toSpanned
import cz.kotox.core.entity.ParcelableSpanned

/**
 *  Factory for passing Spanned via varargs.
 */

fun toParcelableSpanned(spanned: Spanned): ParcelableSpanned = ParcelableSpanned(SpannableString.valueOf(spanned))

fun toSpanned(parcelableSpanned: ParcelableSpanned): Spanned = requireNotNull(parcelableSpanned.spannableString).toSpanned()