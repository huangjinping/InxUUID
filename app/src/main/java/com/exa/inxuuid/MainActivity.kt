package com.exa.inxuuid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.fingerprintjs.android.fingerprint.Fingerprinter
import com.fingerprintjs.android.fingerprint.FingerprinterFactory

class MainActivity : AppCompatActivity() {
    lateinit var txt_add: TextView
    lateinit var edt_emass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        txt_add = findViewById(R.id.txt_add)
        edt_emass = findViewById(R.id.edt_emass)
        var deviceIdFactory = DeviceIdFactory(this@MainActivity)
        edt_emass.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        Log.d("getDeviceId", "=======1=")
        txt_add.setOnClickListener({
            var deviceID = deviceIdFactory.getDeviceId()
            Log.d("getDeviceId", "=======2=$deviceID")
        })
    }

    fun getUUID() {
        // Initialization
        val fingerprinter = FingerprinterFactory.create(this@MainActivity)

// Usage
        fingerprinter.getFingerprint(version = Fingerprinter.Version.V_5) { fingerprint ->
            // Use fingerprint
            runOnUiThread {
                txt_add.text = fingerprint
            }
            print("finge3rprint-----$fingerprint")
            Log.d("finge3rprint", "==$fingerprint===");
        }

        fingerprinter.getDeviceId(version = Fingerprinter.Version.V_5) { result ->
            val deviceId = result.deviceId
            // Use deviceId
            print("devi2ceId-------$deviceId")
            Log.d("devi2ceId", "==$deviceId===");

        }
    }
}