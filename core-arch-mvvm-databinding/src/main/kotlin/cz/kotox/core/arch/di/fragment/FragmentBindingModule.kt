package cz.kotox.core.arch.di.fragment

import androidx.fragment.app.FragmentFactory
import dagger.Binds
import dagger.Module

@Module()
abstract class FragmentBindingModule {
	@Binds
	abstract fun bindFragmentFactory(factory: FragmentInjectionFactory): FragmentFactory
}