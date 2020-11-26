package cz.kotox.dsp.di

import android.app.Activity

/**
 * This exists so things get less coupled from the application class. Thanks to this interface,
 * our application class doesn't need to be open just so our test application class can extend it.
 */
interface AppComponentProvider {

	val component: ApplicationComponent
}

/**
 * And this exists to makes things beautiful in the Activity. Who needs `dagger-android`?
 */
val Activity.injector get() = (application as AppComponentProvider).component