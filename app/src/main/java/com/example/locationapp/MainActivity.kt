package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewmodel: LocationViewModel = viewModel()      //5.b creating a view model which we are using from our dependencies
            LocationAppTheme {
                Surface(modifier = Modifier.fillMaxSize(),color = MaterialTheme.colorScheme.background) {
                    MyApp(viewmodel)
                }
            }
        }
    }
}

@Composable
fun MyApp(viewmodel:LocationViewModel){
    val context = LocalContext.current
    val locationU=LocationUtils(context)
    locationDisplay(locationU,viewmodel,context)
}

@Composable
fun locationDisplay(location_utils:LocationUtils,viewmodel:LocationViewModel,context: Context){

    val location=viewmodel.location.value
    val address=location?.let{                          //a way of smart casting a data that can be a null type to a non-null type
        location_utils.reverseGeoDecoder(location)
    }

    val permissionLauncher = rememberLauncherForActivityResult(contract =
        ActivityResultContracts.RequestMultiplePermissions()    // 2.b it is basically a pop-up window which will ask for the permissions from the user and store the result
        , onResult = {          //  2.c it : returns a map of <String,Boolean> ,i.e. it returns a map of permissions in the form of a string and their corresponding boolean value, if the boolean value is true then the permission is granted
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true && it[Manifest.permission.ACCESS_COARSE_LOCATION]==true){
                //permission has been request. change location

                location_utils.reqLocationUpdates(viewmodel)
            }
             else {     //2.d give the user a rationale, telling the user why you need permission
                 val rationaleRequired= ActivityCompat.shouldShowRequestPermissionRationale(
                     context as MainActivity,                       //2.e passing the context of MainActivity and asking the user to grant either the coarse location permission or fine location permission
                     Manifest.permission.ACCESS_FINE_LOCATION
                 )  || ActivityCompat.shouldShowRequestPermissionRationale(context as MainActivity,
                     Manifest.permission.ACCESS_COARSE_LOCATION
                     )

                if (rationaleRequired){     // Why are we needing to show Rationale? Because since we are in this condition we have ben denied either fine or coarse permission but hasnt clicked the dont ask again button, but since we will be needing that we will display the rationale and ask the user to change the settings.
                    Toast.makeText(context,"Permission Required", Toast.LENGTH_LONG).show()
                }else {             //If the user has denied either ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION and selected "Don't ask again," rationaleRequired will be false.
                    Toast.makeText(context,"Permission Denied. Change permission in settings.",Toast.LENGTH_LONG).show();
                }
            }
        })

    Column(modifier=Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center
    ,horizontalAlignment = Alignment.CenterHorizontally
    ){
        if (location!=null){
            Text(text = "Latitude: ${location.latitude} Longitude: ${location.longitude} \n $address")
        }else
        {
            Text(text="Location Not Available")
        }
        Spacer(modifier = Modifier.padding(50.dp))
        Button(onClick = {
            if (location_utils.hasLocationOrNot(context)){
                //permission has been request. change location
                location_utils.reqLocationUpdates(viewmodel)
            }else {
                //ask for permission
                permissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }) {
            Text(text = "My location")
        }
    }
}