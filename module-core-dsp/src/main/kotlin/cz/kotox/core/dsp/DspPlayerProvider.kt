package cz.kotox.core.dsp

import be.tarsos.dsp.AudioGenerator
import be.tarsos.dsp.effects.DelayEffect
import be.tarsos.dsp.effects.FlangerEffect
import be.tarsos.dsp.filters.LowPassFS
import be.tarsos.dsp.io.android.AndroidAudioPlayer
import be.tarsos.dsp.synthesis.AmplitudeLFO
import be.tarsos.dsp.synthesis.NoiseGenerator
import be.tarsos.dsp.synthesis.SineGenerator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * https://stackoverflow.com/questions/25956415/tarsos-dsp-android-audiotrack-plays-static-or-too-fast
 */
@Singleton
class DspPlayerProvider @Inject constructor() {

	var androidAudioPlayer: AndroidAudioPlayer? = null
	@ExperimentalCoroutinesApi
	fun playFrequency() = callbackFlow<AudioGenerator> {

		Timber.d(">>> PLAY...")
		val generator = AudioGenerator(/*1024*/12100, /*0*/6050)
		generator.addAudioProcessor(NoiseGenerator(0.2))
		generator.addAudioProcessor(LowPassFS(1000f, 44100f))
		generator.addAudioProcessor(LowPassFS(1000f, 44100f))
		generator.addAudioProcessor(LowPassFS(1000f, 44100f))
		generator.addAudioProcessor(SineGenerator(0.05, 220.0))
		generator.addAudioProcessor(AmplitudeLFO(10.0, 0.9))
		generator.addAudioProcessor(SineGenerator(0.2, 440.0))
		generator.addAudioProcessor(SineGenerator(0.1, 880.0))
		generator.addAudioProcessor(DelayEffect(1.5, 0.4, 44100.0))
		generator.addAudioProcessor(AmplitudeLFO())
		generator.addAudioProcessor(NoiseGenerator(0.02))
		generator.addAudioProcessor(SineGenerator(0.05, 1760.0))
		generator.addAudioProcessor(SineGenerator(0.01, 2460.0))
		generator.addAudioProcessor(AmplitudeLFO(0.1, 0.7))
		generator.addAudioProcessor(DelayEffect(0.757, 0.4, 44100.0))
		generator.addAudioProcessor(FlangerEffect(0.1, 0.2, 44100.0, 4.0))

		//java.lang.IllegalArgumentException: The buffer size should be at least 10632 (samples) according to  AudioTrack.getMinBufferSize().

		try {
			androidAudioPlayer = AndroidAudioPlayer(generator.format)
			generator.addAudioProcessor(AndroidAudioPlayer(generator.format))

		} catch (iae: IllegalArgumentException) {
			Timber.e(">>>X TarsosDSPAudioFormat failure!!!")
			iae.printStackTrace()
		}
		sendBlocking(generator)
		generator.run()
	}

	fun finished() {
		androidAudioPlayer?.processingFinished()
	}

}