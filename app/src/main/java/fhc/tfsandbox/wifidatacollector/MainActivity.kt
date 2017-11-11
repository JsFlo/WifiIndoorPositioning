package fhc.tfsandbox.wifidatacollector

import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import fhc.tfsandbox.wifidatacollector.data.WifiScanResult
import fhc.tfsandbox.wifidatacollector.output.FileCounterStreamProvider
import fhc.tfsandbox.wifidatacollector.output.ListOutputListener
import fhc.tfsandbox.wifidatacollector.output.OutputList
import fhc.tfsandbox.wifidatacollector.output.WifiFileOutputWriter
import fhc.tfsandbox.wifidatacollector.receiver.WifiScanResultsBroadcastReceiver
import fhc.tfsandbox.wifidatacollector.ui.RoomAdapter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
        ListOutputListener<WifiScanResult>,
        WifiScanResultsBroadcastReceiver.WifiScanResultsListener {

    companion object {
        private const val BATCH_SIZE = 10
    }

    private var writeCounter = 0
    private lateinit var outputList: OutputList<WifiScanResult>
    private lateinit var wifiScanReceiver: WifiScanResultsBroadcastReceiver
    private lateinit var roomAdapter: RoomAdapter

    @SuppressLint("WifiManagerLeak")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()

        val fileCounterStreamProvider = FileCounterStreamProvider(this, "debug_testing")
        val fileWriter = WifiFileOutputWriter(fileCounterStreamProvider)
        outputList = OutputList(listOf(fileWriter, this), BATCH_SIZE)

        val wifiManager = getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiScanReceiver = WifiScanResultsBroadcastReceiver(wifiManager, this)
        registerReceiver(wifiScanReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

        start_button.setOnClickListener {
            wifiScanReceiver.startScanning()
        }
        stop_button.setOnClickListener {
            outputList.clear()
            wifiScanReceiver.stopScanning()
        }
    }

    private fun setupViews() {
        batch_size_tv.text = BATCH_SIZE.toString()

        val rooms = resources.getStringArray(R.array.room_labels)
        roomAdapter = RoomAdapter.getRoomAdapter(this, rooms)
        label_spinner.adapter = roomAdapter
    }

    // one training row (one scan)
    override fun onWifiScanResult(wifiScanResult: WifiScanResult) {
        outputList.add(wifiScanResult)

        batch_size_tv.text = "${outputList.size()}/$BATCH_SIZE"
    }

    // file has been written
    override fun outputData(data: List<WifiScanResult>) {
        writeCounter++
        roomAdapter.incrementCounter(label_spinner.selectedItemPosition)
        counter_tv.text = writeCounter.toString()
    }

    override fun getCurrentLabel(): Int {
        return label_spinner.selectedItemPosition + 1
    }

}
