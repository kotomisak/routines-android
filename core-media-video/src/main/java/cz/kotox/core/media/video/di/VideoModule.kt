package cz.kotox.core.media.video.di

import android.app.Application
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import cz.kotox.core.entity.AppId
import dagger.Module
import dagger.Provides

@Module
object VideoModule {

	@Provides
	@JvmStatic
	fun provideExoPlayer(application: Application): SimpleExoPlayer = SimpleExoPlayer.Builder(application).build()

	@Provides
	@JvmStatic
	fun provideDefaultDataSourceFactory(
		application: Application,
		appId: AppId
	): DefaultDataSourceFactory = DefaultDataSourceFactory(application, Util.getUserAgent(application, appId.value)) //TODO verify if this is ok to use appId here

	@Provides
	@JvmStatic
	fun provideAudioFocusAttributes(): AudioAttributes =
		AudioAttributes.Builder()
			.setUsage(C.USAGE_MEDIA)
			.setContentType(C.CONTENT_TYPE_MUSIC)
			.build()

}