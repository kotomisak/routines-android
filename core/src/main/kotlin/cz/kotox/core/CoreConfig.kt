package cz.kotox.core

private const val FLAVOR_PRODUCTION = "production"
private const val FLAVOR_STAGING = "staging"
private const val FLAVOR_DEVELOP = "dev"

private const val BUILD_TYPE_DEBUG = "debug"
private const val BUILD_TYPE_RELEASE = "release"

object CoreConfig {

	const val IS_PRODUCTION_FLAVOR = BuildConfig.FLAVOR == FLAVOR_PRODUCTION
	const val IS_STAGING_FLAVOR = BuildConfig.FLAVOR == FLAVOR_STAGING
	const val IS_DEVELOP_FLAVOR = BuildConfig.FLAVOR == FLAVOR_DEVELOP

	const val IS_DEBUG_BUILD_TYPE = BuildConfig.BUILD_TYPE == BUILD_TYPE_DEBUG
	const val IS_RELEASE_BUILD_TYPE = BuildConfig.BUILD_TYPE == BUILD_TYPE_RELEASE
}