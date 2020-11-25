package cz.kotox.dsp.di

import cz.kotox.dsp.MobileDspApplication
import cz.kotox.dsp.UserInitializer
import cz.kotox.core.arch.ApplicationInterfaceContract
import cz.kotox.core.di.BaseComponent
import cz.kotox.core.di.BaseFeatureComponent
import cz.kotox.core.di.UserScope
import cz.kotox.core.entity.AppVersion
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
	modules = [
		BaseModule::class,
		UserComponentModule::class,
//		FeatureCoreModule::class,
		ViewModelModule::class,
		MobileDspDaggerModule::class
	],
	dependencies = [
		BaseComponent::class
	]
)
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

	fun inject(mobileDspApplication: MobileDspApplication)

}

@Module(subcomponents = [UserComponent::class])
object UserComponentModule

@Subcomponent(modules = [
// feature modules
	MobileModule::class
])
@UserScope
interface UserComponent : BaseFeatureComponent {
	@Subcomponent.Builder
	interface Builder {
		fun build(): UserComponent
	}

	fun userInitializer(): UserInitializer
}

