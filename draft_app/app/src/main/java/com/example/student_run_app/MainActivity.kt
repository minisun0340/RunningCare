package com.example.student_run_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.content.res.Configuration
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
//import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

class MainActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {

    //lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private var layout_Distance_value: TextView? = null
    private var layout_Step_value: TextView? = null
    private var layout_kcal_value: TextView? = null
    private var layout_Speed_value: TextView? = null

    private val layout_Speed_chart: LineChart? = null

    private val Bluetooth_item: MenuItem? = null
    private lateinit var Setting_item: MenuItem
    private lateinit var Sound_item: MenuItem

    private var layout_Image_view: ImageView? = null

    /*------------------------------------------------------------------------ Bluetooth Setting Start ------------------------------------------------------------------*/

    companion object {
        @SuppressLint("StaticFieldLeak")
        var mContext: Context? = null
        private val REQUEST_SELECT_DEVICE = 1
        private val REQUEST_ENABLE_BT = 2
        private val UART_PROFILE_READY = 10
        val TAG = "nRFUART"
        private val UART_PROFILE_CONNECTED = 20
        private val UART_PROFILE_DISCONNECTED = 21
        private val STATE_OFF = 10

        private val REQUEST_SETTING_DEVICE = 11
        var datavals = ArrayList<Entry>()
    }

    var mRemoteRssiVal: TextView? = null
    var mRg: RadioGroup? = null
    private var mState = UART_PROFILE_DISCONNECTED
    private var mService: UartService? = null
    private var mDevice: BluetoothDevice? = null
    private var mBtAdapter: BluetoothAdapter? = null

    //private ListView messageListView;
    //private ArrayAdapter<String> listAdapter;
    private val btnConnectDisconnect: Button? = null  //private ListView messageListView;

    //private ArrayAdapter<String> listAdapter;
    private val btnSend: Button? = null
    //private EditText edtMessage;
    /*------------------------------------------------------------------------ Bluetooth Setting End ---------------------------------------------------------------------*/

    /*------------------------------------------------------------------------ Variable init -----------------------------------------------------------------------------*/
    var IMU_Data: String? = null
    lateinit var separate: Array<String>
    lateinit var graph_separate: Array<String>

    var Step_data = 0f
    var Steps_data: String? = null

    var Walk_state = 0
    var Speed_data = 0f

    var Speed_count = 0

    /*----------------------------------------------------------------------- Graph variable -----------------------------------------------------------------------------*/
    var x_max = 50
    var x_min = 0

    var Max_run_value = 0f
    var Min_run_value = 0f

    var local_run_count = 0

    var lineChart: LineChart? = null
    var lineData: LineData = LineData()
    var Speed_graph_value: LineDataSet? = null

    /*---------------------------------------------------------------------- Sound Setting -----------------------------------------------------------*/
    var sound: String? = null
    var checkedId = 0
    var setting_sc: String? = null

    var sharedPreferences: SharedPreferences? = null

    /*---------------------------------------------------------------------- function -----------------------------------------------------------*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBtAdapter == null) {
            Toast.makeText(this, "블루투스가 OFF 되있습니다.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        layout_Distance_value = findViewById<View>(R.id.Distance_text) as TextView
        layout_Step_value = findViewById<View>(R.id.Step_text) as TextView
        layout_kcal_value = findViewById<View>(R.id.kcal_text) as TextView
        layout_Speed_value = findViewById<View>(R.id.speed_text) as TextView

//        Setting_item = findViewById<View>(R.id.Setting_menu) as MenuItem
//        Sound_item = findViewById<View>(R.id.Sound_menu) as MenuItem

        layout_Image_view = findViewById<View>(R.id.State_Image) as ImageView

        service_init() //mService.initialize()

        MainActivity.mContext = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Bluetooth_menu -> {
                if (!mBtAdapter!!.isEnabled) {
                    val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT)
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                        if(it.resultCode == REQUEST_ENABLE_BT) { }
                    }.launch(enableIntent)
                } else {
                    if (mState == UART_PROFILE_DISCONNECTED) {
                        val newIntent = Intent(this@MainActivity, DeviceListActivity::class.java)
                        startActivityForResult(newIntent, MainActivity.REQUEST_SELECT_DEVICE)
//                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
////                            if(it.resultCode == REQUEST_SELECT_DEVICE) { }
//                        }.launch(newIntent)
                    } else {
                        if (mDevice != null) {
                            mService?.disconnect()
                        }
                    }
                }
                true
            }
            R.id.Setting_menu -> {
                startActivity(Intent(this, Setting_Activity::class.java))
                true
            }
            R.id.Sound_menu -> {
                startActivity(Intent(applicationContext, SoundActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    //UART service connected/disconnected
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun onServiceConnected(className: ComponentName, rawBinder: IBinder) {
            mService = (rawBinder as UartService.LocalBinder).service //mService = UartService
            Log.d(TAG, "onServiceConnected mService= $mService")
            if (mService?.initialize() != true) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
        }

        override fun onServiceDisconnected(classname: ComponentName) {
            ////     mService.disconnect(mDevice);
            mService = null
        }
    }

    private val mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler(Looper.getMainLooper()) {
        //Handler events that received from UART service
        override fun handleMessage(msg: Message) {
            //add_frequency_value(int_frequency);
        }
    }

    private val UARTStatusChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            val mIntent = intent
            if (action == UartService.ACTION_GATT_CONNECTED) {
                val txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA)
                runOnUiThread { //String currentDataTimeString = DateFormat.getTimeInstance().format(new Date());
                    Log.d(TAG, "UART CONNECT_MSG")
                    mState = UART_PROFILE_CONNECTED
                }
            }
            if (action == UartService.ACTION_GATT_DISCONNECTED) {
                runOnUiThread {
                    Log.d(TAG, "UART_DISCONNECT_MSG")
                    //((TextView) findViewById(R.id.deviceName)).setText("Not Connect");
                    mState = UART_PROFILE_DISCONNECTED
                    mService?.close()
                }
            }
            if (action == UartService.ACTION_GATT_SERVICES_DISCOVERED) {
                mService?.enableTXNotification()
            }
            if (action == UartService.ACTION_DATA_AVAILABLE) {
                val txValue = intent.getByteArrayExtra(UartService.EXTRA_DATA)
                runOnUiThread {
                    try {
                        val text = String(txValue!!) //val text = String(txValue!!, "UTF-8")
                        IMU_Data = text
                        Log.d(TAG, "TEXT: $text")
                        show_IMU_data(IMU_Data!!)
                        graph_separate = text.split(" ").toTypedArray()
                        val RUN_DATA = graph_separate[4]
                        Log.d(TAG, "show_IMU_data: $RUN_DATA")
                        Run_Speed(RUN_DATA)
                    } catch (e: NullPointerException) {
                    } catch (e: ArrayIndexOutOfBoundsException) {
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                }
            }
            if (action == UartService.DEVICE_DOES_NOT_SUPPORT_UART) {
                showMessage("Device doesn't support UART. Disconnecting")
                mService?.disconnect()
            }
        }
    }

    private fun service_init() {
        val bindIntent = Intent(this@MainActivity, UartService::class.java)
        bindService(bindIntent, mServiceConnection, BIND_AUTO_CREATE)
        LocalBroadcastManager.getInstance(this@MainActivity).registerReceiver(
            UARTStatusChangeReceiver,
            makeGattUpdateIntentFilter()
        )
    }

    private fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED)
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED)
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED)
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE)
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART)
        return intentFilter
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver)
        } catch (ignore: Exception) {
            Log.e(TAG, ignore.toString())
        }
        unbindService(mServiceConnection)
        mService?.stopSelf()
        mService = null
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if (!mBtAdapter!!.isEnabled) {
            Log.i(TAG, "onResume - BT not enabled yet")
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                if(it.resultCode == REQUEST_ENABLE_BT) { }
            }.launch(enableIntent)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MainActivity.REQUEST_SELECT_DEVICE ->                 //When the DeviceListActivity return, with the selected device address
                if (resultCode == RESULT_OK && data != null) {
                    val deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE)
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)
                    Log.d(
                        TAG,
                        "... onActivityResultdevice.address==" + mDevice + "mserviceValue" + mService
                    )
                    //((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName() + " - connecting");
                    mService?.connect(deviceAddress)
                }
            REQUEST_ENABLE_BT ->                 // When the request to enable Bluetooth returns
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth has turned on ", Toast.LENGTH_SHORT).show()
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled")
                    Toast.makeText(this, "Problem in BT Turning ON ", Toast.LENGTH_SHORT).show()
                    finish()
                }
            else -> Log.e(TAG, "wrong request code")
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {}

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        if (mState == UART_PROFILE_CONNECTED) {
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(startMain)
            showMessage("BerhmKorea Run App running in background.\n             Disconnect to exit")
        } else {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.Disconnected_message)
                .setMessage(R.string.popup_message)
                .setPositiveButton(R.string.popup_yes,
                    DialogInterface.OnClickListener { dialog, which -> finish() })
                .setNegativeButton(R.string.popup_no, null)
                .show()
        }
    }

    override fun onPostResume() {
        super.onPostResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.Bluetooth_menu)

        // 범 코리아 사진
        //layout_Image_view.setImageResource(R.drawable.mancharic);

        /* 화면 처음 Gif 실행 하기 위한 코드 */layout_Image_view!!.setImageResource(R.drawable.runtoyou)
        //GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(layout_Image_view);
        Glide.with(this).load(R.drawable.runtoyou).into(layout_Image_view!!)
        if (mState == UART_PROFILE_DISCONNECTED) {
            item.title = "블루투스 연결"
        } else {
            item.title = "블루투스 연결해제"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    @SuppressLint("CommitPrefEdits")
    fun show_IMU_data(`val`: String): Int {
        separate = `val`.split(" ").toTypedArray()
        layout_Distance_value!!.text = separate[3]
        Step_data = separate[2].toFloat()
        Step_data *= 2f
        Steps_data = Step_data.toString()
        layout_Step_value!!.text = Steps_data
        Walk_state = separate[5].toInt()
        layout_Speed_value!!.text = separate[4]
        if (Walk_state == 1) {
            /* 블루투스 연결 시 */
            //layout_Image_view.setImageResource(R.drawable.run_2);

            // Gif 실행 하기 위한 코드
            layout_Image_view!!.setImageResource(R.drawable.runtoyou2)
            //GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(layout_Image_view);
            Glide.with(this).load(R.drawable.runtoyou2).into(layout_Image_view!!)

            // 사진 들어있는 것
            //layout_Image_view.setImageResource(R.drawable.manrunning);

            /*
            Intent intent = getIntent();
            setting_sc = intent.getStringExtra("SoundValue");
            showMessage(setting_sc);
            Log.d(TAG, "show_IMU_data: "+setting_sc);
            */

            sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            val shardSting = sharedPreferences?.getString("Sound_Value", "")
            Log.d(TAG, "Sound_Value: $shardSting")
            if (shardSting != null) {

                // Gif 실행 하기 위한 코드
                layout_Image_view!!.setImageResource(R.drawable.runtoyou2)
                //GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(layout_Image_view);
                Glide.with(this).load(R.drawable.runtoyou2).into(layout_Image_view!!)

                when (shardSting) {
                    "기본음" -> {
                        val player1: MediaPlayer = MediaPlayer.create(this, R.raw.original_alarm)
                        player1.start()
                        setting_sc = "기본음"
                    }
                    "일반 1" -> {
                        val player2: MediaPlayer = MediaPlayer.create(this, R.raw.emergency)
                        player2.start()
                        setting_sc = "일반 1"
                    }
                    "일반 2" -> {
                        val player3: MediaPlayer = MediaPlayer.create(this, R.raw.fire)
                        player3.start()
                        setting_sc = "일반 2"
                    }
                    "일반 3" -> {
                        val player4: MediaPlayer = MediaPlayer.create(this, R.raw.bell)
                        player4.start()
                        setting_sc = "일반 3"
                    }
                    "일반 4" -> {
                        val player5: MediaPlayer = MediaPlayer.create(this, R.raw.siren)
                        player5.start()
                        setting_sc = "일반 4"
                    }
                    "일반 5" -> {
                        val player6: MediaPlayer = MediaPlayer.create(this, R.raw.virus)
                        player6.start()
                        setting_sc = "일반 5"
                    }
                    "남자 목소리" -> {
                        val player8: MediaPlayer = MediaPlayer.create(this, R.raw.man)
                        player8.start()
                        setting_sc = "남자 목소리"
                    }
                    "여자 목소리" -> {
                        val player9: MediaPlayer = MediaPlayer.create(this, R.raw.woman)
                        player9.start()
                        setting_sc = "여자 목소리"
                    }
                }
            } else {
                val player: MediaPlayer = MediaPlayer.create(this, R.raw.original_alarm)
                player.start()
            }
        } else {
            layout_Image_view!!.setImageResource(R.drawable.run)
            val img = findViewById<View>(R.id.State_Image) as ImageView

            //layout_Image_view.setImageResource(R.drawable.mancharic);
            layout_Image_view!!.setImageResource(R.drawable.runtoyou)
            Glide.with(this).load(R.drawable.runtoyou).into(layout_Image_view!!)
        }
        return Walk_state
    }

    private fun Run_Speed(run_speed_data: String) {
        val int_Speed_data = run_speed_data.toFloat() //Float인데 왜 이름이 int_Speed_data일까?
        Speed_graph_value = LineDataSet(dataValues(int_Speed_data), "speed_value")
        lineChart = findViewById(R.id.speed_chart)
        if (Speed_count >= 50) {
            Speed_graph_value!!.removeEntry(0)
            lineChart?.getXAxis()?.axisMinimum = x_min++.toFloat()
            lineChart?.getXAxis()?.axisMinimum = x_max++.toFloat()
            lineChart?.getData()?.clearValues()
        }
        val float_speed_data = run_speed_data.toFloat()
        Max_run_value = Max_run_value(float_speed_data)
        Min_run_value = Min_run_value(float_speed_data)
        lineChart?.axisLeft?.axisMaximum = Max_run_value + 10
        lineChart?.axisLeft?.axisMinimum = Min_run_value - 10
        lineChart?.axisRight?.axisMaximum = Max_run_value + 10
        lineChart?.axisRight?.axisMinimum = Min_run_value - 10
        lineData.addDataSet(Speed_graph_value)
        lineChart?.data = lineData
        lineChart?.xAxis?.axisMaximum = x_max.toFloat()
        lineChart?.xAxis?.axisMinimum = x_min.toFloat()
        lineChart?.notifyDataSetChanged()
        lineChart?.invalidate()
    }

    private fun dataValues(int_speed_data_2: Float): ArrayList<Entry> {
        MainActivity.datavals.add(Entry(Speed_count.toFloat(), int_speed_data_2))
        Speed_count++
        println(Speed_count)
        return MainActivity.datavals
    }

    private fun Min_run_value(min_speed_data: Float): Float {
        if (Min_run_value == 0f) Min_run_value = min_speed_data
        if (Min_run_value > min_speed_data) Min_run_value = min_speed_data
        return Min_run_value
    }

    private fun Max_run_value(max_speed_data: Float): Float {
        if (Max_run_value < max_speed_data) {
            Max_run_value = max_speed_data
        } else if (Max_run_value > max_speed_data) {
            local_run_count++
            if (local_run_count == 80) {
                Max_run_value = max_speed_data
                local_run_count = 0
            }
        }
        return Max_run_value
    }

}