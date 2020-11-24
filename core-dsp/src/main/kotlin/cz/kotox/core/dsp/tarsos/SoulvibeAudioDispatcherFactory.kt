package cz.kotox.core.dsp.tarsos

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MicrophoneInfo
import be.tarsos.dsp.AudioDispatcher
import be.tarsos.dsp.io.TarsosDSPAudioFormat
import be.tarsos.dsp.io.TarsosDSPAudioInputStream
import be.tarsos.dsp.io.android.AndroidAudioInputStream
import cz.kotox.core.dsp.DspEventTracker
import cz.kotox.core.utility.getDeviceAndOsVersionShortString
import cz.kotox.core.utility.getDeviceAndOsVersionString
import kotlinx.coroutines.delay
import timber.log.Timber

/**
 * Custom factory alternative to default AudioDispatcherFactory in TarsosDSP library.
 * This customization allows to track microphone information and eventually microphone selection.
 *
 * @author Michal Jenicek
 * @see be.tarsos.dsp.io.android.AudioDispatcherFactory
 */
object SoulvibeAudioDispatcherFactory {
	/**
	 * Create a new AudioDispatcher connected to the default microphone.
	 *
	 * @param sampleRate
	 * The requested sample rate.
	 * @param audioBufferSize
	 * The size of the audio buffer (in samples).
	 *
	 * @param bufferOverlap
	 * The size of the overlap (in samples).
	 * @return A new AudioDispatcher
	 */
	suspend fun fromMicrophone(
		sampleRate: Int,
		audioBufferSize: Int,
		bufferOverlap: Int,
		dspEventTracker: DspEventTracker
	): AudioDispatcher {
		val minAudioBufferSize = AudioRecord.getMinBufferSize(sampleRate,
			AudioFormat.CHANNEL_IN_MONO,
			AudioFormat.ENCODING_PCM_16BIT)
		val minAudioBufferSizeInSamples = minAudioBufferSize / 2
		return if (minAudioBufferSizeInSamples <= audioBufferSize) {
			val audioInputStream = AudioRecord(
				MediaRecorder.AudioSource.MIC, sampleRate,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT,
				audioBufferSize * 2)
			val format = TarsosDSPAudioFormat(sampleRate.toFloat(), 16, 1, true, false)
			val audioStream: TarsosDSPAudioInputStream = AndroidAudioInputStream(audioInputStream, format)
			//start recording ! Opens the stream.
			audioInputStream.startRecording()


			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
				delay(500) // Wait a bit for mic to be activated, otherwise there will be error when reading active microphones
				try {
					audioInputStream.activeMicrophones?.forEach { trackMicrophoneInfo(it, dspEventTracker) }
				} catch (e: java.io.IOException) {
					Timber.e(e, "Unable to track info about active microphones.")
				}
			}

			AudioDispatcher(audioStream, audioBufferSize, bufferOverlap)
		} else {
			throw IllegalArgumentException("Buffer size too small should be at least " + minAudioBufferSize * 2)
		}
	}

	private fun trackMicrophoneInfo(microphone: MicrophoneInfo, dspEventTracker: DspEventTracker) {

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
			val micRecordLong = StringBuilder()
			val micRecordShort = StringBuilder()
			val unknownString = "_"

			micRecordLong.append("device: ${getDeviceAndOsVersionString()}, ")
			micRecordShort.append("A:${getDeviceAndOsVersionShortString()}")

			micRecordLong.append("description: ${microphone.description}, ")
			micRecordShort.append("B:${microphone.description}")

			micRecordLong.append("type: ${microphone.type}, ")
			micRecordShort.append("C:${microphone.type}")

			micRecordLong.append("address: ${microphone.address}, ")
			micRecordShort.append("D:${microphone.address}")

			val sensitivityString: String = if (microphone.sensitivity == MicrophoneInfo.SENSITIVITY_UNKNOWN) unknownString else microphone.sensitivity.toString()
			micRecordLong.append("sensitivity: $sensitivityString, ")
			micRecordShort.append("E:$sensitivityString")

			val directionalityString: String = if (microphone.directionality == MicrophoneInfo.DIRECTIONALITY_UNKNOWN) unknownString else microphone.directionality.toString()
			micRecordLong.append("directionality: $directionalityString")
			micRecordShort.append("F:$directionalityString")

			val locationString: String = if (microphone.location == MicrophoneInfo.LOCATION_UNKNOWN) unknownString else microphone.location.toString()
			micRecordLong.append("micLocation: $locationString,")
			micRecordShort.append("G:$directionalityString")

			micRecordLong.append("deviceGroup: ${microphone.group} index: ${microphone.indexInTheGroup}, ")

			val position = microphone.position
			if (position == MicrophoneInfo.POSITION_UNKNOWN) {
				micRecordLong.append("position: ${unknownString}, ")
				micRecordShort.append("H:${unknownString}")
			} else {
				micRecordLong.append("position:[${position.x},${position.y},${position.z}], ")
				micRecordShort.append("H:${position.x},${position.y},${position.z}")
			}

			val orientation = microphone.orientation
			if (orientation == MicrophoneInfo.ORIENTATION_UNKNOWN) {
				micRecordLong.append("orientation: $unknownString, ")
				micRecordShort.append("I:$unknownString")
			} else {
				micRecordLong.append("orientation: [${orientation.x},${orientation.y},${orientation.z}], ")
				micRecordShort.append("I:${orientation.x},${orientation.y},${orientation.z}")
			}
			micRecordLong.append("frequencyResponse: ${microphone.frequencyResponse}, ")
			//Do not log frequency response to short record because it might be really long array.
			//frequencyResponse: [Pair{100.0 -0.78}, Pair{106.0 -0.71}, Pair{112.0 -0.64}, Pair{118.0 -0.6}, Pair{125.0 -0.55}, Pair{132.0 -0.5}, Pair{140.0 -0.47}, Pair{150.0 -0.42}, Pair{160.0 -0.39}, Pair{170.0 -0.36}, Pair{180.0 -0.34}, Pair{190.0 -0.33}, Pair{200.0 -0.32}, Pair{212.0 -0.29}, Pair{224.0 -0.28}, Pair{236.0 -0.28}, Pair{250.0 -0.27}, Pair{265.0 -0.25}, Pair{280.0 -0.25}, Pair{300.0 -0.24}, Pair{315.0 -0.23}, Pair{335.0 -0.23}, Pair{355.0 -0.22}, Pair{375.0 -0.22}, Pair{400.0 -0.19}, Pair{425.0 -0.17}, Pair{450.0 -0.15}, Pair{475.0 -0.15}, Pair{500.0 -0.14}, Pair{530.0 -0.14}, Pair{560.0 -0.12}, Pair{600.0 -0.11}, Pair{630.0 -0.1}, Pair{670.0 -0.1}, Pair{710.0 -0.08}, Pair{750.0 -0.07}, Pair{800.0 -0.07}, Pair{850.0 -0.04}, Pair{900.0 -0.03}, Pair{950.0 -0.01}, Pair{1000.0 0.0}, Pair{1060.0 0.04}, Pair{1120.0 0.06}, Pair{1180.0 0.07}, Pair{1250.0 0.08}, Pair{1320.0 0.13}, Pair{1400.0 0.09}, Pair{1500.0 0.14}, Pair{1600.0 0.19}, Pair{1700.0 0.23}, Pair{1800.0 0.28}, Pair{1900.0 0.29}, Pair{2000.0 0.31}, Pair{2120.0 0.37}, Pair{2240.0 0.88}, Pair{2360.0 0.86}, Pair{2500.0 0.77}, Pair{2650.0 0.78}, Pair{2800.0 0.84}, Pair{3000.0 0.86}, Pair{3150.0 1.05}, Pair{3350.0 1.12}, Pair{3550.0 1.18}, Pair{3750.0 1.25}, Pair{4000.0 1.43}, Pair{4250.0 1.66}, Pair{4500.0 1.83}, Pair{4750.0 2.02}, Pair{5000.0 2.23}, Pair{5300.0 2.59}, Pair{5600.0 2.84}, Pair{6000.0 3.35}, Pair{6300.0 4.01}, Pair{6700.0 6.82}, Pair{7100.0 6.62}, Pair{7500.0 6.42}, Pair{8000.0 7.3}, Pair{8500.0 8.23}, Pair{9000.0 7.54}, Pair{9500.0 12.68}, Pair{10000.0 13.76}, Pair{10600.0 18.69}, Pair{11200.0 19.68}, Pair{11800.0 20.9}, Pair{12500.0 23.7}, Pair{13200.0 25.1}, Pair{14000.0 21.65}, Pair{15000.0 16.18}, Pair{16000.0 18.84}, Pair{17000.0 25.44}, Pair{18000.0 23.48}, Pair{19000.0 23.22}, Pair{20000.0 24.89}]

			/**
			 * Info below is usually cut by the Firebase max value length limit
			 */

			micRecordLong.append("channelMapping:${microphone.channelMapping}, ")
			micRecordShort.append("J:${microphone.channelMapping}")
			//channelMapping:[Pair{0 2}]

			val maxSplString: String = if (microphone.maxSpl == MicrophoneInfo.SPL_UNKNOWN) unknownString else microphone.maxSpl.toString()
			micRecordLong.append("max spl:  $maxSplString, ")

			val minSplString: String = if (microphone.minSpl == MicrophoneInfo.SPL_UNKNOWN) unknownString else microphone.minSpl.toString()
			micRecordLong.append("min spl: $minSplString, ")

			micRecordLong.append("portId: ${microphone.id}")
			micRecordShort.append("K:${microphone.id}")

			Timber.d(micRecordLong.toString())
			Timber.d(micRecordShort.toString())
			dspEventTracker.logMicInfo(micInfo = micRecordShort.toString())
		}
	}

}