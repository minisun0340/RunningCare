package com.example.student_run_app

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationSettingsRequest.Builder
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Setting_Activity : AppCompatActivity(), OnMapReadyCallback, OnRequestPermissionsResultCallback {
    private var mLoadButton: Button? = null

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null

    private val TAG = "googlemap_example"
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val UPDATE_INTERVER_MS = 1000
    private val POLYLINE_UPDATE_MS = 5
    private val FATEST_UPDATE_INTERVAL_MS = 500

    private val PATTERN_GAP_LENGTH_PX = 20
    private val DOT: PatternItem = Dot()
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX.toFloat())
    private val PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT)

    var Marker_Check = 0

    private var arrayPoints: ArrayList<LatLng?>? = null

    private val markerPosition: Vector<LatLng>? = null
    private val activeMarkers: Vector<Marker>? = null

    private var mDatabase: DatabaseReference? = null

    var Local_Latitude = 0.0
    var Local_Longitude = 0.0

    var Latitude_final = 0.0
    var Longitude_final = 0.0

    var onResume_Latitude = 0.0
    var onResume_Longitude = 0.0

    var i = 0

    private val PERMISSIONS_REQUEST_CODE = 100
    var needRequest = false

    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var mCurrentLocation: Location? = null
    var currnetPosition: LatLng? = null

    var Last_Position: LatLng? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null

    private var mLayout: View? = null

    // 시간 관련 함수
    var sdfNow = SimpleDateFormat("yyyy/MM/dd/HH:mm:ss")
    var String_Date: String? = null
    var Get_firebase_val: String? = null
    lateinit var speared: Array<String>
    var Latitude_parsing = 0f
    var longitude_parsing = 0f

    // 마커 관련 변수 선언
    var listOfpoints: ArrayList<LatLng?> = ArrayList<LatLng?>()
    var All_location: String? = null

    var mOptions = MarkerOptions()
    var circle1m = CircleOptions()

    var polylineOptions: PolylineOptions? = null


    // 중심 좌표부터의 반경거리, 3m
    var local_radius = 3

    var between_a_to_b = 0.0
    var count_i = 0
    var hue = 0f

    var Walk_State_Flag = 0
    var poly_color = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )
        polylineOptions = PolylineOptions()
        mLayout = findViewById(R.id.Setting_layout)
        mLoadButton = findViewById(R.id.Load_location)
        locationRequest = LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVER_MS.toLong())
            .setFastestInterval(FATEST_UPDATE_INTERVAL_MS.toLong())
        val builder: LocationSettingsRequest.Builder = Builder()
        builder.addLocationRequest(locationRequest)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /* 지도 위치 버튼 선택 시 본인 위치로 이동 */
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        try {
            val input = openFileInput("Latlngpoints.txt")
            val din = DataInputStream(input)
            val sz = din.readInt()
            for (i in 0 until sz) {
                val str = din.readUTF()
                Log.v("read", str)
                val stringArray = str.split(",").toTypedArray()
                val latitude = stringArray[0].toDouble()
                val longtitude = stringArray[1].toDouble()
                onResume_Latitude = latitude
                onResume_Longitude = longtitude
                listOfpoints.add(LatLng(onResume_Latitude, onResume_Longitude))
                //Log.d(TAG, "onResume: "+ listOfpoints);
            }
            din.close()
            // 마커를 읽어오는 함수 사용
            //loadMarkers(listOfpoints);
        } catch (exc: IOException) {
            exc.printStackTrace()
        } catch (x: NullPointerException) {
            x.printStackTrace()
        }
        mDatabase = FirebaseDatabase.getInstance().getReference()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        try {
            currentMarker = null
            val output = openFileOutput(
                "Latlngpoints.txt",
                MODE_PRIVATE
            )
            val dout = DataOutputStream(output)
            dout.writeInt(listOfpoints.size)
            for (point in listOfpoints) {
                if (between_a_to_b > circle1m.radius) {
                    dout.writeUTF(point!!.latitude.toString() + "," + point.longitude)
                    Log.v("write", point.latitude.toString() + "," + point.longitude)
                }
            }
            dout.flush()
            dout.close()
        } catch (exc: IOException) {
            exc.printStackTrace()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("places", listOfpoints)
    }

    private fun restore(outState: Bundle?) {
        if (outState != null) {
            listOfpoints = (outState.getSerializable("places") as ArrayList<LatLng?>?)!!
        }
    }

    override fun onRestoreInstanceState(outState: Bundle?, persistentState: PersistableBundle?) {
        super.onRestoreInstanceState(outState!!)
        restore(outState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady :")
        mMap = googleMap

        //loadMarkers(listOfpoints);

        //setDefaultLocation();
        val hasFindeLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFindeLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                Snackbar.make(
                    mLayout!!, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인") {
                    ActivityCompat.requestPermissions(
                        this@Setting_Activity, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE
                    )
                }.show()
            } else {
                ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
        mMap!!.uiSettings.isMyLocationButtonEnabled = true
        val LOAD_LOCATION = LatLng(onResume_Latitude, onResume_Longitude)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LOAD_LOCATION, 18f)
        mMap!!.moveCamera(cameraUpdate)
        mMap!!.setOnMapClickListener { Log.d(TAG, "onMapClick : ") }
    }

    var locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList: List<Location> = locationResult.getLocations()
            if (locationList.size > 0) {
                location = locationList[locationList.size - 1]
                currnetPosition = LatLng(location!!.latitude, location!!.longitude)
                Last_Position = LatLng(Latitude_final, Longitude_final)
                if (Latitude_final == 0.0 && Longitude_final == 0.0) {
                    Latitude_final = onResume_Latitude
                    Log.d(TAG, "onLocationResult_Lat: $Latitude_final")
                    Longitude_final = onResume_Longitude
                    Log.d(TAG, "onLocationResult_Long: $Longitude_final")
                }

                Local_Latitude = location!!.latitude
                Local_Longitude = location!!.longitude
                setCurrentLocation(location)
                mCurrentLocation = location
                between_a_to_b =
                    getDistance(Local_Latitude, Local_Longitude, Latitude_final, Longitude_final)
                write_Location("minseok", Local_Latitude, Local_Longitude)

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if ((MainActivity.mContext as MainActivity).Walk_state === 1) {
                    if (Walk_State_Flag == 0) {
                        val bitmapdraw = resources.getDrawable(R.drawable.running) as BitmapDrawable
                        val b = bitmapdraw.bitmap
                        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
                        mOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        //polylineOptions.color(Color.RED);
                        mMap!!.addMarker(mOptions)
                        listOfpoints!!.add(LatLng(Latitude_final, Longitude_final))
                    }
                }
                //걷는 아이콘 뛰으는 소소코드
                /*
                else if(((MainActivity)MainActivity.mContext).Walk_state == 0){
                    if(Walk_State_Flag == 1) {
                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.sneaker);
                        Bitmap b = bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScale dBitmap(b, 100, 100, false);
                        mOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                        //polylineOptions.color(Color.BLUE);
                        mMap.addMarker(mOptions);
                        listOfpoints.add(new LatLng(Latitude_final, Longitude_final));
                    }
                }
                 */mOptions.position(currnetPosition)
                make_a_way()

                /*
                polylineOptions.width(8);
                polylineOptions.pattern(PATTERN_POLYLINE_DOTTED);
                arrayPoints.add(currnetPosition);
                polylineOptions.addAll(arrayPoints);
                 */

                //.fillColor(Color.parseColor("#880000ff"));

                /*
                if(Marker_Check == 0)
                {
                    Read_Location("minseok");
                    Log.d("get_string = ", Get_firebase_val);
                    speared = Get_firebase_val.split("=");
                    Latitude_parsing = Float.parseFloat(speared[1]);
                    longitude_parsing = Float.parseFloat(speared[3]);

                    MarkerOptions mOptions = new MarkerOptions();
                    mOptions.position(new LatLng(Latitude_parsing, longitude_parsing));
                    mMap.addMarker(mOptions);

                    Marker_Check = 1;
                }
                */if (i >= POLYLINE_UPDATE_MS) {
                    val now = System.currentTimeMillis()
                    val date = Date(now)
                    String_Date = sdfNow.format(date)
                    i = 0
                }
                i++
                Latitude_final = Local_Latitude
                Longitude_final = Local_Longitude
                Walk_State_Flag = (MainActivity.mContext as MainActivity).Walk_state
            }
        }
    }


    private fun write_Location(location_Id: String, latitude_1: Double, longitude_2: Double) {
        val mLocation = LOCATION(latitude_1, longitude_2)
        mDatabase?.child("Location")?.child(location_Id)?.setValue(mLocation)
            ?.addOnSuccessListener(OnSuccessListener<Void?> {
                Toast.makeText(
                    this@Setting_Activity,
                    "저장을 완료했습니다.",
                    Toast.LENGTH_SHORT
                )
            })
            ?.addOnFailureListener(OnFailureListener {
                Toast.makeText(
                    this@Setting_Activity,
                    "저장을 실패했습니다.",
                    Toast.LENGTH_SHORT
                )
            })
    }

    /*
    private void write_Firebase(String time, String User_name, double latitude_realtime, double longitude_realtime) {
        Check_ID mCheck_ID = new Check_ID(time, latitude_realtime, longitude_realtime);

        mDatabase.child("Location").child(User_name).setValue(mCheck_ID)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Setting_Activity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Setting_Activity.this, "저장을 취소했습니다.", Toast.LENGTH_SHORT);

                    }
                });
    }
     */

    private fun Read_Location(location_Id: String) {
        mDatabase!!.child("Location").child(location_Id).get().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("firebase", "Error getting data", task.exception)
            } else {
                Log.d("firebase", task.result.value.toString())
                Get_firebase_val = task.result.value.toString()
            }
        }
    }

    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting")
            showDialogForLocationServiceSetting()
        } else {
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음")
                return
            }
            Log.d(TAG, "startLocationUpdates: call mFusedLocationClient.requestLocationUpdates")
            mFusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
            if (checkPermission()) mMap!!.isMyLocationEnabled = true
        }
    }

    fun getCurrentAddress(latLng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latLng.latitude,
                latLng.longitude,
                1
            )
        } catch (ioException: IOException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        return if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            "주소 미발견"
        } else {
            val address = addresses[0]
            address.getAddressLine(0).toString()
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    fun setCurrentLocation(location: Location?) {
        val currentLatLng = LatLng(location!!.latitude, location.longitude)
        arrayPoints = ArrayList()
        val cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)
    }

    fun setDefaultLocation() {
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인"
        if (currentMarker != null) currentMarker!!.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        currentMarker = mMap!!.addMarker(markerOptions)
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15f)
        mMap!!.moveCamera(cameraUpdate)
    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        return if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            true
        } else false

    }

    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permission: Array<String?>,
        grandResults: IntArray
    ) {
        super.onRequestPermissionsResult(permsRequestCode, permission, grandResults)
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {
            var check_result = true
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {
                startLocationUpdates()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    Snackbar.make(
                        mLayout!!, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요, ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "확인"
                    ) { finish() }.show()
                } else {
                    Snackbar.make(
                        mLayout!!, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "확인"
                    ) { finish() }.show()
                }
            }
        }
    }

    private fun showDialogForLocationServiceSetting() {
        val builder = AlertDialog.Builder(this@Setting_Activity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
            앱을 사용하기 위해서는 위치 서비스가 필요합니다. 
            위치 설정을 수정하시겠습니까?
            """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정") { dialog, which ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        }
        builder.setNegativeButton(
            "취소"
        ) { dialog, which -> dialog.cancel() }
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            GPS_ENABLE_REQUEST_CODE -> if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d(TAG, "onActivityResult: GPS 활성화 되있음")
                    needRequest = true
                    return
                }
            }
        }
    }

    private fun loadMarkers(listOfpoint: List<LatLng>) {
        var i = listOfpoint.size
        while (i > 0) {
            i--
            val Lat = listOfpoint[i].latitude
            Log.d(TAG, "loadMarkers: $Lat")
            val Lon = listOfpoint[i].longitude
            Log.d(TAG, "loadMarkers: $Lon")
            //MarkerOptions mp = new MarkerOptions();
            mOptions.position(LatLng(Lat, Lon))
            mOptions.title("Load Marker")
            mMap!!.addMarker(mOptions)
            Log.d(TAG, "loadmarker is working")
            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Lat, Lon), 18f))
        }
    }

    fun getDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val distance: Double
        val locationA = Location("point A")
        locationA.latitude = lat1
        locationA.longitude = lng1
        val locationB = Location("point B")
        locationB.latitude = lat2
        locationB.longitude = lng2
        distance = locationA.distanceTo(locationB).toDouble()
        Log.d(TAG, "getDistance: $distance")
        //String double_to_string = Double.toString(Math.round(distance)/100.0);
        mLoadButton!!.text = String.format("%.2f", distance) + " m"
        return distance
    }

    fun make_a_way() {
        polylineOptions!!.width(8f)

        /*
        if(((MainActivity)MainActivity.mContext).Walk_state==1)
            polylineOptions.color(Color.BLUE);

        else
            polylineOptions.color(Color.RED);

         */polylineOptions!!.pattern(PATTERN_POLYLINE_DOTTED)
        arrayPoints!!.add(currnetPosition)
        polylineOptions!!.addAll(arrayPoints)
        mMap!!.addPolyline(polylineOptions)
    }
}