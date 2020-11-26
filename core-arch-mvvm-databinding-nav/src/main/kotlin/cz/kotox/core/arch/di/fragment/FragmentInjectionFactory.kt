package cz.kotox.core.arch.di.fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import javax.inject.Provider

class FragmentInjectionFactory @Inject constructor(
	private val providers: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>>
) : FragmentFactory() {

	override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
		val fragmentClass = loadFragmentClass(classLoader, className)
		return providers[fragmentClass]?.get() ?: super.instantiate(classLoader, className)
	}

}