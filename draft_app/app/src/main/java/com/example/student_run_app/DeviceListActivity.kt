package com.example.student_run_app

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.LeScanCallback
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import java.util.*

class DeviceListActivity : Activity() {

    private var mBluetoothAdapter: BluetoothAdapter? = null

    private var mEmptyList: TextView? = null
    companion object {
        const val TAG = "DeviceListActivity"
    }
    var deviceList: MutableList<BluetoothDevice>? = null
    private var deviceAdapter: DeviceAdapter? = null
    private val onService: ServiceConnection? = null
    lateinit var devRssiValues: MutableMap<String, Int>
    private val SCAN_PERIOD: Long = 10000 //scanning for 10 seconds

    private var mHandler: Handler? = null
    private var mScanning = false

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar)
        setContentView(R.layout.activity_device_list)
        val layoutParams = this.window.attributes
        layoutParams.gravity = Gravity.TOP
        layoutParams.y = 200
        mHandler = Handler()
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        populateList()
        mEmptyList = findViewById<View>(R.id.empty_button) as TextView
        val cancelButton = findViewById<View>(R.id.btn_cancel) as Button
        cancelButton.setOnClickListener { if (mScanning == false) scanLeDevice(true) else finish() }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun populateList() {
        /* Initialize device list container */
        Log.d(TAG, "populateList")
        deviceList = ArrayList()
        deviceAdapter = DeviceAdapter(this, deviceList as ArrayList<BluetoothDevice>)
        devRssiValues = HashMap()
        val newDevicesListView = findViewById<View>(R.id.new_devices) as ListView
        newDevicesListView.adapter = deviceAdapter
        newDevicesListView.onItemClickListener = mDeviceClickListener
        scanLeDevice(true)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun scanLeDevice(enable: Boolean) {
        val cancelButton = findViewById<View>(R.id.btn_cancel) as Button
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler!!.postDelayed({
                mScanning = false
                mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
                cancelButton.setText(R.string.scan)
            }, SCAN_PERIOD)
            mScanning = true
            mBluetoothAdapter!!.startLeScan(mLeScanCallback)
            cancelButton.setText(R.string.cancel)
        } else {
            mScanning = false
            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
            cancelButton.setText(R.string.scan)
        }
    }

    private val mLeScanCallback =
        LeScanCallback { device, rssi, scanRecord -> runOnUiThread { addDevice(device, rssi) } }

    private fun addDevice(device: BluetoothDevice, rssi: Int) {
        var deviceFound = false
        for (listDev in deviceList!!) {
            if (listDev.address == device.address) {
                deviceFound = true
                break
            }
        }
        devRssiValues[device.address] = rssi
        if (!deviceFound) {
            deviceList!!.add(device)
            mEmptyList!!.visibility = View.GONE
            deviceAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onStop() {
        super.onStop()
        mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onDestroy() {
        super.onDestroy()
        mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private val mDeviceClickListener =
        OnItemClickListener { parent, view, position, id ->
            val device = deviceList!![position]
            mBluetoothAdapter!!.stopLeScan(mLeScanCallback)
            val b = Bundle()
            b.putString(BluetoothDevice.EXTRA_DEVICE, deviceList!![position].address)
            val result = Intent()
            result.putExtras(b)
            setResult(RESULT_OK, result)
            finish()
        }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onPause() {
        super.onPause()
        scanLeDevice(false)
    }

    inner class DeviceAdapter(var context: Context, devices: List<BluetoothDevice>) : BaseAdapter() {
        var devices: List<BluetoothDevice>
        var inflater: LayoutInflater

        override fun getCount(): Int {
            return devices.size
        }

        override fun getItem(position: Int): Any {
            return devices[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View { //convertView: View에서 물음표 하나 붙임
            val vg: ViewGroup = if (convertView != null) {
                convertView as ViewGroup
            } else {
                inflater.inflate(R.layout.device_element, null) as ViewGroup
            }

            val device : BluetoothDevice = devices[position]
            val tvadd = vg.findViewById<View>(R.id.address) as TextView
            val tvname = vg.findViewById<View>(R.id.name) as TextView
            val tvpaired = vg.findViewById<View>(R.id.paired) as TextView
            val tvrssi = vg.findViewById<View>(R.id.rssi) as TextView

            tvrssi.visibility = View.VISIBLE
            val rssival = devRssiValues[device.address]!!.toInt().toByte()
            if (rssival.toInt() != 0) {
                tvrssi.text = "Rssi = $rssival"
            }
            tvname.text = device.name
            tvadd.text = device.address
            if (device.bondState == BluetoothDevice.BOND_BONDED) {
                Log.i(TAG, "device::" + device.name)
                tvname.setTextColor(Color.WHITE)
                tvadd.setTextColor(Color.WHITE)
                tvpaired.setTextColor(Color.GRAY)
                tvpaired.visibility = View.VISIBLE
                tvpaired.text = "paired"
                tvrssi.visibility = View.VISIBLE
                tvrssi.setTextColor(Color.WHITE)
            } else {
                tvname.setTextColor(Color.WHITE)
                tvadd.setTextColor(Color.WHITE)
                tvpaired.visibility = View.GONE
                tvrssi.visibility = View.VISIBLE
                tvrssi.setTextColor(Color.WHITE)
            }
            return vg
        }

        init {
            inflater = LayoutInflater.from(context)
            this.devices = devices
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}