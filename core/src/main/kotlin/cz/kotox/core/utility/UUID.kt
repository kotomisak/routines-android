package cz.kotox.core.utility

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
//import com.google.firebase.iid.FirebaseInstanceId
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

class AndroidAppUUID @Inject constructor(
	val application: Application
) {

	fun createRandomUUID(): String {
		val instanceId = try {
			////Fixme - remove dependency on Firebase
			null //FirebaseInstanceId.getInstance().id
		} catch (e: Exception) {
			e.printStackTrace()
			Timber.e(e)
			null
		}

		val uuid = UUID.randomUUID().toString()

		Timber.d("instanceId=$instanceId uuid=$uuid")

		return "${instanceId ?: ""}-$uuid"
	}

	/**
	 * Returns id unique to the application if possible in following steps.
	 * 1) It tries to use firebase ID
	 * 2) It tries to use ANDROID_ID
	 * 3) It will use random id otherwise
	 */
	//Fixme - remove dependency on Firebase
	fun createAppImmutableUniqueId(): String
//			try {
//		Timber.d(">>>_ device token GENERATE")
//		FirebaseInstanceId.getInstance().id
//	} catch (e: Exception) {
			{
				//Timber.e(e, "Unable to get firebase instance id")
				return application.deviceId() ?: run {
					//Timber.e(IllegalStateException("Unable to get ANDROID_ID"))
					UUID.randomUUID().toString()
				}
			}
//	}

	@SuppressLint("HardwareIds")
	private fun Context.deviceId(): String? = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: null

}
