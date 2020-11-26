package cz.kotox.core.rest

import cz.kotox.core.database.AppPreferences
import cz.kotox.core.ktools.fold
import cz.kotox.core.rest.interceptror.AUTHORIZATION_HEADER
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class RestAuthenticator @Inject constructor(
	private val accessTokenProvider: Provider<AccessTokenProvider>,
	private val restContract: RestContract,
	private val prefs: AppPreferences
) : Authenticator {

	override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
		Timber.i("Access token expired")

		val originalRequest = response.request

		if (!originalRequest.url.toString().contains("sessions/refresh")) {
			// Remove access token so it's not used for getting new one (API returns Unauthorized if the token is provided).

			val loggedUser = prefs.userLoggedIn
			prefs.accessTokenExpired()

			accessTokenProvider.get().refreshAccessToken().fold(
				{
					Timber.e(IllegalStateException("Refresh token API failed: $it"))

					if (loggedUser) {
						Timber.d(">>>_ TOKEN: EXPIRING SESSION FOR LOGGED IN USER")
						prefs.refreshTokenExpired()
						restContract.sessionExpired()
					} else {
						Timber.d(">>>_ TOKEN: no need to retire session, since there was no user logged in!")
						Timber.w(">>>_ TOKEN: There is probably some API anonymous endpoint called with by anonymous user!")
					}
					null
				},
				{
					originalRequest.newBuilder()
						.header(AUTHORIZATION_HEADER, "Bearer ${prefs.accessToken}")
						.build()
				}
			)
		} else {
			null
		}
	}
}