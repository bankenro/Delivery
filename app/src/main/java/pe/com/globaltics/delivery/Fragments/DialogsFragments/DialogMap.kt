package pe.com.globaltics.delivery.Fragments.DialogsFragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import pe.com.globaltics.delivery.R
import pe.com.globaltics.delivery.Clases.LatitudLongitud
import pe.com.globaltics.delivery.Clases.LocationPermisos


class DialogMap : DialogFragment(), View.OnClickListener, OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var confirmar: Button? = null
    private var latitud = ""
    private var longitud = ""
   // private var locationManager: LocationManager? = null
    private var mLocationRequest: LocationRequest? = null
    private var UPDATE_INTERVAL: Long = 10 * 1000 //10 segundos
    private var FASTEST_INTERVAL: Long = 2000 //2 segundos
    var locationCallback: LocationCallback? = null
    var mapFragment: SupportMapFragment? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
        return inflater.inflate(R.layout.dialog_fragment_map, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        InstanciarLocalizacion()

        //mapFragment = childFragmentManager.findFragmentById(R.id.g_map) as SupportMapFragment
        mapFragment = activity!!.supportFragmentManager.findFragmentById(R.id.g_map) as SupportMapFragment
        confirmar = view.findViewById(R.id.confirmar)

        mapFragment!!.getMapAsync(this)
        confirmar!!.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mapFragment != null) {
            activity!!.supportFragmentManager.beginTransaction().remove(mapFragment!!).commit()
        }
    }

    private fun showPointerOnMap(latitude: Double, longitude: Double) {
        mapFragment!!.getMapAsync { googleMap ->
            val latLng = LatLng(latitude, longitude)
            googleMap.addMarker(
                MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_flag))
                    .anchor(0.0f, 1.0f)
                    .position(latLng)
            )
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isZoomControlsEnabled = true

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap.moveCamera(cameraUpdate)
        }
    }

    private fun InstanciarLocalizacion() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.run {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            setFastestInterval(FASTEST_INTERVAL)
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(activity!!)
        settingsClient!!.checkLocationSettings(locationSettingsRequest)

        TraerLocalizacion()
    }

    private fun TraerLocalizacion() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                onLocationChanged(locationResult!!.lastLocation)
            }
        }
        if (Build.VERSION.SDK_INT >= 23 && checkPermission()) {
            LocationServices.getFusedLocationProviderClient(activity!!).requestLocationUpdates(
                mLocationRequest,
                locationCallback, Looper.myLooper()
            )
        }
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //confirmar!!.isClickable = true
            return true
        } else {
            try {
                (context as LocationPermisos).permisos()
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
            return false
        }
    }

    private fun onLocationChanged(lastLocation: Location?) {
        //val msg = "Localizacion " + lastLocation!!.latitude + " , " + lastLocation.longitude
        showPointerOnMap(lastLocation!!.latitude, lastLocation.longitude)
        //Toast.makeText(activity!!, msg, Toast.LENGTH_SHORT).show()
        latitud = lastLocation.latitude.toString()
        longitud = lastLocation.longitude.toString()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.confirmar -> Confirmar()
        }
    }

    override fun onMapReady(googlemap: GoogleMap?) {
        googleMap = googlemap
        googleMap!!.setMinZoomPreference(10f)
        googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
    }

    override fun onStop() {
        super.onStop()
        LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(locationCallback)
    }

    override fun onPause() {
        super.onPause()
        LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(locationCallback)
    }

    private fun Confirmar() {
        try {
            (targetFragment as LatitudLongitud).ObtenerLatitudLongitud(latitud, longitud)
        } catch (e: ClassCastException) {
            e.printStackTrace()
        }
    }
}