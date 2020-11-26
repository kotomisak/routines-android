package cz.kotox.core.arch.di

import cz.kotox.core.arch.di.fragment.FragmentBindingModule
import cz.kotox.core.arch.di.viewModel.ViewModelBindingModule
import dagger.Module

@Module(includes = [
	FeatureCoreModule::class,
	FragmentBindingModule::class,
	ViewModelBindingModule::class
])
object BaseArchModule