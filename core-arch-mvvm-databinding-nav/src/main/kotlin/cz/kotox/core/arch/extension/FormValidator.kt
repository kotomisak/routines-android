package cz.kotox.core.arch.extension

import cz.kotox.core.arch.R
import cz.kotox.core.arch.ktools.FormField
import cz.kotox.core.arch.ktools.Validator

fun <T> FormField<T>.notNullValidator() = validator { if (it == null) R.string.form_error_empty else null }
fun FormField<String>.notEmptyValidator() = validator { if (it.isNullOrBlank()) R.string.form_error_empty else null }
fun FormField<String>.emailValidator() = validator { if (!Validator.validateEmail(it)) R.string.form_error_invalid_email else null }
fun FormField<String>.emailOrEmptyValidator() = validator { if (!Validator.validateEmail(it) && !it.isNullOrBlank()) R.string.form_error_invalid_email else null }
fun FormField<String>.phoneValidator() = validator { if (!Validator.validatePhoneNumber(it)) R.string.form_error_invalid_phone else null }
fun FormField<String>.urlValidator() = validator { if (!Validator.validateUrl(it)) R.string.form_error_invalid_url else null }
fun FormField<String>.passwordValidator(minLength: Int) = validator { if (!Validator.validatePassword(it, minLength)) R.string.form_error_invalid_password else null }
fun FormField<String>.pinPasswordValidator(pinLength: Int, minPasswordLength: Int) = validator { if (!Validator.validatePinPassword(it, pinLength, minPasswordLength)) R.string.form_error_invalid_password else null }
fun FormField<String>.lengthValidator(minLength: Int, maxLength: Int) = validator { if (!Validator.validateLength(it, minLength, maxLength)) R.string.form_error_invalid_length else null }
fun FormField<String>.byteLengthValidator(maxLength: Int) = validator { if (!Validator.validateByteLength(it, maxLength)) R.string.form_error_invalid_length else null }
fun FormField<String>.regExContainsValidator(regex: Regex) = validator { if (!Validator.validateRegExContains(it, regex)) R.string.form_error_invalid_characters else null }
fun FormField<String>.regExMatchesValidator(regex: Regex) = validator { if (!Validator.validateRegExMatches(it, regex)) R.string.form_error_invalid_characters else null }
fun FormField<String>.changedValidator(default: String?) = validator { if (!Validator.validateChanged(it, default)) R.string.form_error_no_change else null }