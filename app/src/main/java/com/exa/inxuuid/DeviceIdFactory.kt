package com.exa.inxuuid

import android.content.Context
import android.media.MediaDrm
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log

import com.fingerprintjs.android.fingerprint.tools.executeSafe
import java.security.MessageDigest
import java.util.UUID

private const val URI_GSF_CONTENT_PROVIDER = "content://com.google.android.gsf.gservices"
private const val ID_KEY = "android_id"
private const val WIDEWINE_UUID_MOST_SIG_BITS = -0x121074568629b532L
private const val WIDEWINE_UUID_LEAST_SIG_BITS = -0x5c37d8232ae2de13L

class DeviceIdFactory(private val context: Context) {


    public fun getDeviceId(): String {
        var gsfif = getGsfAndroidId()
        if (gsfif != null) return gsfif
        var mediaDrmId = getMediaDrmId()
        if (mediaDrmId != null) return mediaDrmId
        return getAndroidId()
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }


    fun getMediaDrmId(): String? = executeSafe({
        mediaDrmId()
    }, null)

    private fun mediaDrmId(): String {
        val widevineUUID = UUID(WIDEWINE_UUID_MOST_SIG_BITS, WIDEWINE_UUID_LEAST_SIG_BITS)
        val wvDrm: MediaDrm?

        wvDrm = MediaDrm(widevineUUID)
        val mivevineId = wvDrm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
        releaseMediaDRM(wvDrm)
        val md: MessageDigest = MessageDigest.getInstance("SHA-256")
        md.update(mivevineId)

        return md.digest().toHexString()
    }

    private fun releaseMediaDRM(drmObject: MediaDrm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            drmObject.close()
        } else {
            drmObject.release()
        }
    }

    fun getAndroidId(): String {
        return executeSafe({
            Settings.Secure.getString(
                context.contentResolver, Settings.Secure.ANDROID_ID
            )
        }, "")
    }

    fun getGsfAndroidId(): String? {
        val URI = Uri.parse(URI_GSF_CONTENT_PROVIDER)
        val params = arrayOf(ID_KEY)
        return try {
            val cursor = context.contentResolver.query(URI, null, null, params, null)

            if (cursor == null) {
                return null
            }

            if (!cursor.moveToFirst() || cursor.columnCount < 2) {
                cursor.close()
                return null
            }
            try {
                val result = java.lang.Long.toHexString(cursor.getString(1).toLong())
                cursor.close()
                result
            } catch (e: NumberFormatException) {
                cursor.close()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

}