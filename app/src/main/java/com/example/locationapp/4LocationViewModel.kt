package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel:ViewModel() {           //4.a Creating a ViewModel to separate the data from the UI
    private val _location = mutableStateOf<LocationData?>(null)
    val location: State<LocationData?> =_location;              // 5.a now we need to pass this location to other parts of the code like the Main & '2' to show the location

    fun updateLocation(newLocation:LocationData){       //shielding our private data from the UI
        _location.value=newLocation
    }
}