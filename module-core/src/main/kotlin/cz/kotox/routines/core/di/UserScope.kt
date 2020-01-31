package cz.kotox.routines.core.di

import javax.inject.Scope

/**
 * Represents scope when one user is logged in.
 * If user logs out, all objects bound to that user should be released
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class UserScope