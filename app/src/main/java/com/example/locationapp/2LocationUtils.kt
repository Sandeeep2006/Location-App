package com.example.locationapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationRequest
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

class LocationUtils(val context:Context){

    private val fusedLocationClient:FusedLocationProviderClient=        //6.a using FLP(fused location provider) to get the location so that we can work with it
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")                                  //?
    fun reqLocationUpdates(viewmodel: LocationViewModel){
        val locationcallback= object :LocationCallback(){
            override fun onLocationResult(locationresult: LocationResult) {
                super.onLocationResult(locationresult)
                val location=locationresult.lastLocation?.let{
                    val location=LocationData(latitude = it.latitude,longitude = it.longitude)
                    viewmodel.updateLocation(location)
                }
            }
        }
        val locationreq=com.google.android.gms.location.LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()

        fusedLocationClient.requestLocationUpdates(locationreq,locationcallback,Looper.getMainLooper())

    }                                                                   //?

    fun hasLocationOrNot(context:Context):Boolean {            //2.a checking if our current context has location or not
        if ((ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED)
            &&
            (ContextCompat.checkSelfPermission(context,android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED))
            return true;
        else
            return false
    }

    fun reverseGeoDecoder(location:LocationData):String {           // Converting the longitude and longitude into readable address
        val geocoder: Geocoder= Geocoder(context, Locale.getDefault())
        val coordinate=LatLng(location.latitude,location.longitude)
        val addresses:MutableList<Address>? =geocoder.getFromLocation(coordinate.latitude,coordinate.longitude,1)

        return if (addresses?.isNotEmpty()==true){
            addresses[0].getAddressLine(0)
        }else {
            "Address Not Found"
        }
    }
}