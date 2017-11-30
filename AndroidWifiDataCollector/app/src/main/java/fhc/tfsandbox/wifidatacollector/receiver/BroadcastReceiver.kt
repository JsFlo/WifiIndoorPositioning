package fhc.tfsandbox.wifidatacollector.receiver

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.models.WifiScanResult
import fhc.tfsandbox.wifidatacollector.models.WifiStateData

/**
 * Acts as a [WifiManager] wrapper which will notify [WifiScanResultsListener] when a list of scan results
 * are returned.
 */
class WifiScanResultsBroadcastReceiver(lifecycleOwner: LifecycleOwner,
                                       private val context: Context,
                                       private val wifiManager: WifiManager,
                                       private val wifiScanListener: WifiScanResultsListener,
                                       private val continuousScan: Boolean = true)
    : BroadcastReceiver(), LifecycleObserver {

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    interface WifiScanResultsListener {
        fun onWifiScanResult(wifiScanResult: WifiScanResult)
    }

    private var scanEnabled = false

    fun startScanning() {
        if (!scanEnabled) {
            scanEnabled = true
            wifiManager.startScan()
        }
    }

    fun stopScanning() {
        if (scanEnabled) {
            scanEnabled = false
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (scanEnabled) {
            if (!continuousScan) {
                stopScanning()
            }
            intent?.let {
                if (it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                    wifiScanListener.onWifiScanResult(WifiScanResult(wifiManager.scanResults.map { WifiStateData(it.BSSID, it.SSID, it.level) }))
                    if (scanEnabled) {
                        wifiManager.startScan()
                    }
                }
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        context.registerReceiver(this,
                IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        try {
            context.unregisterReceiver(this)
        } catch (e: IllegalArgumentException) {
            "Wasn't registered :(".debugPrint()
        }
    }

}