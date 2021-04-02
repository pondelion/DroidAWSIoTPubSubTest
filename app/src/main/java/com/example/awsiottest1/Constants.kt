package com.example.awsiottest1

import com.amazonaws.regions.Regions

class Constants {
    companion object {
        const val KEY_STORE_NAME = "android_test1.jks"
        const val KEY_STORE_PASSWORD = "android_test_pass"
        const val CERT_ID = "android_test_certid"
        const val THING_NAME = "android-test1"
        const val AWS_IOT_ENDPOINT = "********-ats.iot.ap-northeast-1.amazonaws.com"
        const val CERT_FILE = "certificate_downloaded_from_aws_iot_console.pem.crt"
        const val PRIVATE_KEY_FILE = "private_key_downloaded_from_aws_iot_console.pem.key"
        const val ROOT_CA_FILE = "AmazonRootCA1.pem"
        val REGION = Regions.AP_NORTHEAST_1
    }
}