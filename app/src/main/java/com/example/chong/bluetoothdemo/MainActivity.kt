package com.example.chong.bluetoothdemo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val TAG = "MainActivity"
    val deviceNames = mutableListOf<String>()
    var lvAdapter: ArrayAdapter<String>? = null
    private val broadReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1 == null) {
                return
            }
            if(lvAdapter == null){
                return
            }
            when(p1.action){
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    txtStatus.text = "finished"
                    btnSearch.isEnabled = true
                }
                BluetoothDevice.ACTION_FOUND ->{
                    val device = p1.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.d(TAG, "${device.name}, ${device.address}, ${p1.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)}")
                    device.name?.let {
                        deviceNames.add(device.name)
                        lvAdapter!!.notifyDataSetInvalidated()
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    deviceNames.clear()
                    lvAdapter!!.notifyDataSetInvalidated()
                }
            }
        }
    }

    private val bluetoothAdapter: BluetoothAdapter
        get() {
            val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            return bluetoothAdapter
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lvAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, deviceNames)
        lvResult.adapter = lvAdapter


        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND)

        registerReceiver(broadReceiver, intentFilter)

        val bluetoothAdapter: BluetoothAdapter = bluetoothAdapter

        btnSearch.setOnClickListener {
            txtStatus.text = "Searching..."
            it.isEnabled = false
            bluetoothAdapter.startDiscovery()
        }
    }

}
