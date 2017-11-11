package fhc.tfsandbox.wifidatacollector

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.data.WifiStateData
import fhc.tfsandbox.wifidatacollector.output.FileCounterStreamProvider
import fhc.tfsandbox.wifidatacollector.output.OutputList
import fhc.tfsandbox.wifidatacollector.output.WifiFileOutputWriter

class MainActivity : AppCompatActivity() {

    lateinit var outputList: OutputList<WifiScanResult>

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fileCounterStreamProvider = FileCounterStreamProvider(this, "train_data")
        val fileWriter = WifiFileOutputWriter(fileCounterStreamProvider)
        outputList = OutputList(fileWriter, 1000)

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {

                    if (it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                        "received a list of results: ${wifiManager.scanResults.size}".debugPrint()
                        onWifiScanResult(WifiScanResult(wifiManager.scanResults.map { WifiStateData(it.BSSID, it.SSID, it.level) }))
                        wifiManager.startScan()
                    }
                }
            }

        }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
    }

    private fun onWifiScanResult(wifiScanResult: WifiScanResult) {
        outputList.add(wifiScanResult)
    }
}
