package cz.kotox.routines.di

import cz.kotox.routines.AppOnScreenNavApplication
import cz.kotox.routines.UserInitializer
import cz.kotox.routines.core.arch.ApplicationInterfaceContract
import cz.kotox.routines.core.di.BaseComponent
import cz.kotox.routines.core.di.BaseFeatureComponent
import cz.kotox.routines.core.di.FeatureCoreModule
import cz.kotox.routines.core.di.UserScope
import cz.kotox.routines.core.entity.AppVersion
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
	AndroidInjectionModule::class,
	AndroidSupportInjectionModule::class,
	FeatureCoreModule::class,
	ViewModelModule::class,
	AppOnScreenNavDaggerModule::class
])
interface AppComponent {
	@Component.Builder
	interface Builder {

		fun baseComponent(baseComponent: BaseComponent): Builder

		@BindsInstance
		fun appVersion(appVersion: AppVersion): Builder

		@BindsInstance
		fun applicationInterface(appInterface: ApplicationInterfaceContract): Builder

		fun build(): AppComponent
	}

	fun userComponentBuilder(): UserComponent.Builder

	fun inject(appOnScreenNavApplication: AppOnScreenNavApplication)

}

@UserScope
interface UserComponent : BaseFeatureComponent {
	@Subcomponent.Builder
	interface Builder {
		fun build(): UserComponent
	}

	fun userInitializer(): UserInitializer
}

@Module(subcomponents = [UserComponent::class])
object UserComponentModule

