package cz.kotox.core

private const val FLAVOR_STAGING = "mock"
private const val FLAVOR_DEVELOP = "dev"
private const val FLAVOR_PRODUCTION = "production"

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

object CoreConfig {

	const val IS_PRODUCTION_FLAVOR = BuildConfig.FLAVOR == FLAVOR_PRODUCTION
	const val IS_STAGING_FLAVOR = BuildConfig.FLAVOR == FLAVOR_STAGING
	const val IS_DEVELOP_FLAVOR = BuildConfig.FLAVOR == FLAVOR_DEVELOP

	const val IS_DEBUG_BUILD_TYPE = BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG
	const val IS_RELEASE_BUILD_TYPE = BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE

	object RestConfig {
		//Fixme configure this somehow these URLs from exact app using this module...
		private const val PRODUCTION_URL: String = "https://prod.com/"
		private const val STAGING_URL: String = "https://stg.com/"
		private const val DEVELOP_URL: String = "https://dev.com/"

		val BASE_URL = when {
			IS_PRODUCTION_FLAVOR -> PRODUCTION_URL
			IS_STAGING_FLAVOR -> STAGING_URL
			IS_DEVELOP_FLAVOR -> DEVELOP_URL
			else -> throw UnknownError("Unknown flavor")
		}
	}
}