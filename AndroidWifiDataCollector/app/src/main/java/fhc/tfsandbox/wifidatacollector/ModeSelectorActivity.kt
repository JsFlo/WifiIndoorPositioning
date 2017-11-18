package fhc.tfsandbox.wifidatacollector

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_mode_selector.*

class ModeSelectorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mode_selector)

        gather_data_button.setOnClickListener { startActivity(Intent(this@ModeSelectorActivity, TrainActivity::class.java)) }
    }
}
