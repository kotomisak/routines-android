package cz.kotox.core.arch

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.jakewharton.rxrelay2.BehaviorRelay
import cz.kotox.core.OpenForMocking
import cz.kotox.core.di.BaseComponent
import cz.kotox.core.di.BaseFeatureComponent
import cz.kotox.core.di.DaggerBaseComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

interface ApplicationInterfaceContract {
	fun onUserLoggedIn()
	fun redirectToLogin(authMessage: String? = null)
}

@Singleton
@OpenForMocking
class AppInterface @Inject constructor(
	private val applicationInterface: ApplicationInterfaceContract
) : LifecycleObserver, ApplicationInterfaceContract by applicationInterface {

	val isAppInForeground = BehaviorRelay.createDefault(false)

	init {
		ProcessLifecycleOwner.get().lifecycle.addObserver(this)
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	internal fun onAppStart() {
		isAppInForeground.accept(true)
		Timber.d("APP is in foreground")
	}

	@OnLifecycleEvent(Lifecycle.Event.ON_STOP)
	internal fun onAppStop() {
		isAppInForeground.accept(false)
		Timber.d("APP is in background")
	}
}

@OpenForMocking
abstract class BaseApplication : Application(), ApplicationInterfaceContract, HasAndroidInjector {
	@Inject
	lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

	abstract val userComponent: BaseFeatureComponent?

	override fun androidInjector(): AndroidInjector<Any> = AndroidInjector {
		userComponent?.activityInjector()?.maybeInject(it) ?: false || dispatchingAndroidInjector.maybeInject(it)
	}

	lateinit var baseComponent: BaseComponent

	override fun onCreate() {
		super.onCreate()
		baseComponent = DaggerBaseComponent.builder()
			.application(this)
			.build()
	}
}
