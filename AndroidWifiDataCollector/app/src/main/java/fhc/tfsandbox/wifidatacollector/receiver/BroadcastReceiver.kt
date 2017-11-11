package fhc.tfsandbox.wifidatacollector.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.data.WifiStateData

/**
 * Acts as a [WifiManager] wrapper which will notify [WifiScanResultsListener] when a list of scan results
 * are returned.
 *
 * TODO: context should be a weak ref
 */
class WifiScanResultsBroadcastReceiver(private val context: Context, private val wifiManager: WifiManager, private val wifiScanListener: WifiScanResultsListener) : BroadcastReceiver() {

    interface WifiScanResultsListener {
        fun getCurrentLabel(): Int
        fun onWifiScanResult(wifiScanResult: WifiScanResult)
    }

    private var scanEnabled = false

    fun startScanning() {
        if (!scanEnabled) {
            scanEnabled = true
            context.registerReceiver(this, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            wifiManager.startScan()
        }
    }

    fun stopScanning() {
        if (scanEnabled) {
            scanEnabled = false
            try {
                context.unregisterReceiver(this)
            } catch (e: IllegalArgumentException) {
                "Wasn't registered :(".debugPrint()
            }
        }
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