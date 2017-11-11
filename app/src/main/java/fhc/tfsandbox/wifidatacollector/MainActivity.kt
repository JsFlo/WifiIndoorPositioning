package fhc.tfsandbox.wifidatacollector

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import fhc.tfsandbox.wifidatacollector.common.debugPrint
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.data.WifiStateData
import fhc.tfsandbox.wifidatacollector.output.FileCounterStreamProvider
import fhc.tfsandbox.wifidatacollector.output.ListOutputListener
import fhc.tfsandbox.wifidatacollector.output.OutputList
import fhc.tfsandbox.wifidatacollector.output.WifiFileOutputWriter
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter


class MainActivity : AppCompatActivity(), ListOutputListener<WifiScanResult>, AdapterView.OnItemSelectedListener {
    private lateinit var outputList: OutputList<WifiScanResult>
    private var counter = 0
    private var currentLabel: Int = -1
    private var stopScanFlag = false

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupSpinner()

        val fileCounterStreamProvider = FileCounterStreamProvider(this, "train_data")
        val fileWriter = WifiFileOutputWriter(fileCounterStreamProvider)
        outputList = OutputList(listOf(fileWriter, this), 10)

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager

        registerReceiver(
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        if (!stopScanFlag) {
                            intent?.let {
                                if (it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
                                    "received a list of results: ${wifiManager.scanResults.size}".debugPrint()
                                    onWifiScanResult(WifiScanResult(wifiManager.scanResults.map { WifiStateData(it.BSSID, it.SSID, it.level, currentLabel) }))
                                    if (!stopScanFlag) {
                                        wifiManager.startScan()
                                    }
                                }
                            }
                        }
                    }

                }, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        start_button.setOnClickListener {
            stopScanFlag = false
            wifiManager.startScan()
        }
        stop_button.setOnClickListener { stopScanFlag = true }
    }

    private fun onWifiScanResult(wifiScanResult: WifiScanResult) {
        outputList.add(wifiScanResult)
    }

    override fun outputData(data: List<WifiScanResult>) {
        counter++

        counter_tv.text = counter.toString()
    }

    private fun setupSpinner() {
        label_spinner.onItemSelectedListener = this
        val rooms = ArrayList<String>()
        rooms.add("Living Room")
        rooms.add("Kitchen")
        rooms.add("Restroom")
        rooms.add("Bedroom")

        val roomAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rooms)
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        label_spinner.adapter = roomAdapter
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(p0: AdapterView<*>?, parentView: View?, position: Int, id: Long) {
        currentLabel = position + 1
    }

}
