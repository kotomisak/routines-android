package cz.kotox.core.arch.ktools

import android.util.Patterns

object Validator {

	fun validateEmail(email: String?) = email != null && email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
	fun validateUrl(url: String?) = url != null && url.isNotBlank() && Patterns.WEB_URL.matcher(url).matches()
	fun validatePhoneNumber(number: String?) = number != null && number.isNotBlank() && Patterns.PHONE.matcher(number).matches()
	fun validatePassword(password: String?, minLength: Int) = password != null && password.isNotEmpty() && password.length >= minLength
	fun validatePinPassword(password: String?, pinLength: Int, minPasswordLength: Int) = password != null && password.isNotEmpty() && (password.length >= minPasswordLength || password.length == pinLength)
	fun validateLength(input: String?, minLength: Int, maxLength: Int) = input != null && input.isNotEmpty() && input.length >= minLength && input.length <= maxLength
	fun validateByteLength(input: String?, maxLength: Int) = input != null && input.isNotEmpty() && input.toByteArray().size <= maxLength
	fun validateRegExMatches(input: String?, regEx: Regex) = input != null && input.isNotEmpty() && input.matches(regEx)
	fun validateRegExContains(input: String?, regEx: Regex) = input != null && input.isNotEmpty() && input.contains(regEx)
	fun validateChanged(input: String?, default: String?) = input != default
}