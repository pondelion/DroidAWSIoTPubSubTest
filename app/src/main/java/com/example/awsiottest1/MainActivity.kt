package com.example.awsiottest1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos
import java.security.KeyStore


class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "AWSIOTTEST1"
    }

    val mqttManager = AWSIotMqttManager(Constants.THING_NAME, Constants.AWS_IOT_ENDPOINT)
    var pubBtn: Button? = null
    var pubText: EditText? = null
    var subText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pubBtn = findViewById<Button>(R.id.button2)
        pubText = findViewById<EditText>(R.id.editTextPubMsg)
        subText = findViewById<TextView>(R.id.textViewSub)
        pubBtn!!.setOnClickListener {
            publish(msg = pubText!!.text.toString())
        }

        val keyStorePath = this.filesDir.absolutePath
        val isPresent = AWSIotKeystoreHelper.isKeystorePresent(keyStorePath, Constants.KEY_STORE_NAME)
        if (!isPresent) {
            saveCertificateAndPrivateKey(keyStorePath)
        }
        val keyStore = AWSIotKeystoreHelper.getIotKeystore(
            Constants.CERT_ID,
            keyStorePath,
            Constants.KEY_STORE_NAME,
            Constants.KEY_STORE_PASSWORD
        )
        mqttManager!!.connect(keyStore, object : AWSIotMqttClientStatusCallback {
            override fun onStatusChanged(status: AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus?, throwable: Throwable?) {
                Log.d(TAG, "onStatusChanged : " + status.toString())
                if (status == AWSIotMqttClientStatusCallback.AWSIotMqttClientStatus.Connected) {
                    subscribe()
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mqttManager != null) {
            mqttManager.disconnect()
        }
    }

    fun saveCertificateAndPrivateKey(keyStorePath: String) {
        val assetManager = resources.assets
        val certIS = assetManager.open(Constants.CERT_FILE)
        val certStr = certIS.bufferedReader().use { it.readText() }
        val privKeyIS = assetManager.open(Constants.PRIVATE_KEY_FILE)
        val privKeyStr = privKeyIS.bufferedReader().use { it.readText() }
        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(
            Constants.CERT_ID,
            certStr,
            privKeyStr,
            keyStorePath,
            Constants.KEY_STORE_NAME,
            Constants.KEY_STORE_PASSWORD
        )
    }

    fun publish(msg: String, topic: String = "android_test_topic") {
        if (mqttManager != null) {
            mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0)
        }
    }

    fun subscribe(topic: String = "android_test_topic") {
        if (mqttManager != null) {
            mqttManager.subscribeToTopic(
                topic, AWSIotMqttQos.QOS0, object : AWSIotMqttNewMessageCallback {
                    override fun onMessageArrived(topic: String, data: ByteArray) {
                        subText!!.text = subText!!.text.toString() + topic + " : " + data.toString(Charsets.UTF_8) + "\n"
                    }
                }
            )
        }
    }
}
