package com.example.mobilecomputinghw

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Log.d
import android.webkit.PermissionRequest
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import java.util.jar.Manifest
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION)
    lateinit var locationManager: LocationManager
    private var hasGps = false
    private var hasNetwork = false
    private var locationGps : Location? = null
    private var locationNetwork : Location? = null
    private var updating = false    // for creating dynamic updates
    private var counter = 0         // not used now


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            var allSuccess = true
            for (i in permissions.indices){
                if(checkCallingOrSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED)
                    allSuccess = false
            }

            if(!allSuccess)
                requestPermissions(permissions, PERMISSION_REQUEST)
                d( "CodeAndroidLocation", "permission granted")
        }

        startButton.setOnClickListener {
            d( "CodeAndroidLocation", "start button was pressed!")
//            if (counter !=0){
//                updating = true
//                while (updating == true){
//                    d( "CodeAndroidLocation", "updating location")
//                    cordinatesTV2.setText("")
//                    getLocation()
//                }
//            }else {
//                getLocation()
//                counter += 1
//            }
            updating == true
            cordinatesTV2.setText("")
            getLocation()
        }

        stopButton.setOnClickListener {
            d( "manula", "stop button was pressed!")
            updating = false
            cordinatesTV2.setText("")
            Log.d("CodeAndroidLocation", "GPS location was erased")

        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(){
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(hasGps || hasNetwork){
                if(hasGps){
                    Log.d("CodeAndroidLocation", "hasGps")
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0F,object : LocationListener{
                        override fun onLocationChanged(p0: Location?) {
                            Log.d("CodeAndroidLocation", "Location has been changed .... ")
                            if(p0!=null){
                                if (updating == true){
                                locationGps = p0
                                var localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                cordinatesTV2.setText("")
                                cordinatesTV2.append("\nGPS")
                                cordinatesTV2.append("\nLatitude : " + localGpsLocation!!.latitude)
                                cordinatesTV2.append("\nLongitude : " + localGpsLocation!!.longitude)
                                Log.d("CodeAndroidLocation", "Gps Latitude : " + localGpsLocation!!.latitude)
                                Log.d("CodeAndroidLocation", "Gps Longitude : " + localGpsLocation!!.longitude)
                            }}
                        }
                        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                        override fun onProviderEnabled(p0: String?) {}
                        override fun onProviderDisabled(p0: String?) {}
                    })

                    var localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(localGpsLocation!=null)
                        locationGps = localGpsLocation
                }

                if(hasNetwork){
                    Log.d("CodeAndroidLocation", "hasNetwork")
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,0F,object : LocationListener{
                        override fun onLocationChanged(p0: Location?) {
                            if(p0!=null){
                                locationNetwork = p0
                            }
                        }
                        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {}
                        override fun onProviderEnabled(p0: String?) {}
                        override fun onProviderDisabled(p0: String?) {}
                    })

                    var localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    if(localNetworkLocation!=null)
                        locationNetwork = localNetworkLocation
                }
                if (locationGps!=null && locationNetwork!=null){
                    if (locationGps!!.accuracy > locationNetwork!!.accuracy){
                        cordinatesTV2.append("\nNetwork")
                        cordinatesTV2.append("\nLatitude : " + locationNetwork!!.latitude)
                        cordinatesTV2.append("\nLongitude : " + locationNetwork!!.longitude)
                        Log.d("CodeAndroidLocation", "Network Latitude : " + locationNetwork!!.latitude)
                        Log.d("CodeAndroidLocation", "Network Longitude : " + locationNetwork!!.longitude)

                    } else{
                        cordinatesTV2.append("\nGPS")
                        cordinatesTV2.append("\nLatitude : " + locationGps!!.latitude)
                        cordinatesTV2.append("\nLongitude : " + locationGps!!.longitude)
                        Log.d("CodeAndroidLocation", "Gps Latitude : " + locationGps!!.latitude)
                        Log.d("CodeAndroidLocation", "Gps Longitude : " + locationGps!!.longitude)

                    }
                }else{
                    Log.d("CodeAndroidLocation", "Values equal to null")
                }
            }else{
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }

    }}


