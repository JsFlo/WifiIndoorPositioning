package fhc.tfsandbox.wifidatacollector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.data.WifiStateData

class WifiScanResultsBroadcastReceiver(private val wifiManager: WifiManager, private val wifiScanListener: WifiScanResultsListener) : BroadcastReceiver() {

    interface WifiScanResultsListener {
        fun getCurrentLabel(): Int
        fun onWifiScanResult(wifiScanResult: WifiScanResult)
    }

    private var scanEnabled = true

    fun startScanning() {
        scanEnabled = true
        wifiManager.startScan()
    }

    fun stopScanning() {
        scanEnabled = false
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (scanEnabled) {
            intent?.let {
                if (it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                    wifiScanListener.onWifiScanResult(WifiScanResult(wifiManager.scanResults.map { WifiStateData(it.BSSID, it.SSID, it.level, wifiScanListener.getCurrentLabel()) }))
                    if (scanEnabled) {
                        wifiManager.startScan()
                    }
                }
            }
        }
    }

}