package fhc.tfsandbox.wifidatacollector

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager

class MyApplication : Application() {
    lateinit var wifiManager: WifiManager

    override fun onCreate() {
        super.onCreate()
        wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
    }
}