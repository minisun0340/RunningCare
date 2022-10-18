package com.example.runningcareapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_ble.*


class MainActivity : AppCompatActivity() {

    val BT_REQUEST_ENABLE = 1;
    lateinit var bluetoothManager:BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var receiver:BluetoothReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ble)
        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        enableDisableBT()
        receiver = BluetoothReceiver()
        discoverableBtn.setOnClickListener {

        }
    }

    private fun enableDisableBT(){
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)==PackageManager.PERMISSION_GRANTED->{

            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.BLUETOOTH_CONNECT) ->{
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),101)
            }
        }
        turnOnOffBtn.setOnClickListener {
            if (!bluetoothAdapter.isEnabled){
                bluetoothAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(intent)

                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                registerReceiver(receiver, intentFilter)
            }
            if(bluetoothAdapter.isEnabled){
                bluetoothAdapter.disable()

                val intentFilter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
                registerReceiver(receiver, intentFilter)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}