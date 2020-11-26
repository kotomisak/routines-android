package cz.kotox.core.rest.di

import cz.kotox.core.rest.converter.MoshiRetrofitEnumConverterFactory
import cz.kotox.core.rest.interceptror.ConnectivityInterceptor
import cz.kotox.core.rest.interceptror.HeaderInterceptor
import cz.kotox.core.rest.interceptror.LoggingInterceptor
import com.squareup.moshi.Moshi
import cz.kotox.core.CoreConfig
import cz.kotox.core.rest.ErrorParserCallAdapterFactory
import cz.kotox.core.rest.RestAuthenticator
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
object CoreRestModule {

	@Provides
	@JvmStatic
	@Singleton
	@Named("UrlDirect")
	fun provideUrlDirectRetrofit(
		@Named("UrlDirect") okHttpClient: OkHttpClient
	): Retrofit = Retrofit.Builder()
		.baseUrl("http://www.dummy.com")
		.client(okHttpClient)
		.build()

	@Provides
	@JvmStatic
	@Singleton
	@Named("UrlDirect")
	fun provideUrlDirectOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
		.connectTimeout(60, TimeUnit.SECONDS)
		.readTimeout(60, TimeUnit.SECONDS)
		.writeTimeout(60, TimeUnit.SECONDS)
		.build()


	@Provides
	@JvmStatic
	@Singleton
	fun provideRetrofit(
		okHttpClient: OkHttpClient,
		moshi: Moshi,
		errorParserAdapter: ErrorParserCallAdapterFactory
	): Retrofit = Retrofit.Builder()
		.baseUrl(CoreConfig.RestConfig.BASE_URL)
		.client(okHttpClient)
		.addConverterFactory(MoshiConverterFactory.create(moshi))
		.addConverterFactory(MoshiRetrofitEnumConverterFactory())
		.addCallAdapterFactory(errorParserAdapter)
		.build()

	@Provides
	@JvmStatic
	@Singleton
	fun provideOkHttpClient(
			restAuthenticator: RestAuthenticator,
			httpLoggingInterceptor: HttpLoggingInterceptor,
			headerInterceptor: HeaderInterceptor,
			connectivityInterceptor: ConnectivityInterceptor
	): OkHttpClient = OkHttpClient.Builder()
		.connectTimeout(30, TimeUnit.SECONDS)
		.readTimeout(30, TimeUnit.SECONDS)
		.writeTimeout(30, TimeUnit.SECONDS)
		.authenticator(restAuthenticator)
		.addInterceptor(connectivityInterceptor)
		.addInterceptor(headerInterceptor)
		.addInterceptor(httpLoggingInterceptor)
		.build()

	@Provides
	@Singleton
	fun provideLoggingInterceptor(): HttpLoggingInterceptor = LoggingInterceptor.loggingInterceptor()
}