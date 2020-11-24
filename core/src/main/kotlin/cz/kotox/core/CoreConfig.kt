package cz.kotox.core

object CoreConfig {
	const val LOGS = BuildConfig.LOGS
	const val ENVIRONMENT_NAME = BuildConfig.FLAVOR

	const val IS_PRODUCTION_FLAVOR_TYPE = ENVIRONMENT_NAME == "production"
	const val IS_MOCK_FLAVOR_TYPE = ENVIRONMENT_NAME == "mock"

	const val IS_DEBUG_BUILD_TYPE = BuildConfig.BUILD_TYPE == "debug"
	const val IS_RELEASE_BUILD_TYPE = BuildConfig.BUILD_TYPE == "release"
}