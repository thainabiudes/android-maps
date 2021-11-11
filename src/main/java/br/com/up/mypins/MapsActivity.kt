package br.com.up.mypins

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.StringSearch
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.*
import br.com.up.mypins.connection.PlaceAPI

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.com.up.mypins.databinding.ActivityMapsBinding
import br.com.up.mypins.model.Places
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputEditText

// : é extends do java e  o , é implements OnMapReadyCallback
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            if(locationResult.locations.size > 0){
                val location = locationResult.locations[0]

                val myPosition = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(myPosition).title("Minha posição").snippet("Estou na minha casa"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16f))
            }
        }

    }

    //exemplo de função, coloca a tipagem conforme abaixo
    //fun returnInteger() : Int{
    //      return 9
    // }

    //todas as funções tem prefixo fun
    //? antes da variável quer dizer não nula
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputSearch = findViewById<TextInputEditText>(R.id.input_text_edit)
        inputSearch.setOnEditorActionListener { view, id, keyEvent ->

            if(id == EditorInfo.IME_ACTION_SEARCH){
                //search action
                val stringSearch = view.text.toString()
                Log.v("App mapa", "stringSearch")
                PlaceAPI().searchPlaces(stringSearch, object: PlaceAPI.PlaceAPIListener{
                    override fun onPlacesResult(places: ArrayList<Places>) {
                        Log.v("App mapa", "onPlacesResult")
                        for (place in places) {
                            var myPosition = LatLng(place.lat, place.lng)
                            mMap.addMarker(MarkerOptions().position(myPosition).title(place.name).snippet(place.address))
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 16f))
                        }

                    }

                })

                //termo digitado -> stringSearch

                return@setOnEditorActionListener false
            }
            true
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //val = const e var = let
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment //tipagem é depois do as

        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        checkLocationPermission()

    }

    @Suppress("MissingPermission")
    private fun configLocationProvider(){
        val locationRequest = LocationRequest()
        locationRequest.interval = 5000

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

/*        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val myPosition = LatLng(location.latitude, location.longitude)
            mMap.addMarker(MarkerOptions().position(myPosition).title("Minha posição"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myPosition))

        }*/
    }

    fun checkLocationPermission(){
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            configLocationProvider()

        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1000
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            checkLocationPermission();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        // Add a marker in Sydney and move the camera
    }
}

//https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurantes%20em%Curitiba&key=AIzaSyDyFUltXibk_rGCb7Nak3wB5HC_KamkKRI&language=pt-BR