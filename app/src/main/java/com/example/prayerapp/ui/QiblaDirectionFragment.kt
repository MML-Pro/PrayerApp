package com.example.prayerapp.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.prayerapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class QiblaDirectionFragment : Fragment(), OnMapReadyCallback, SensorEventListener, LocationListener {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123

    }


    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var compassArrow: ImageView
    private lateinit var sensorManager: SensorManager
    private lateinit var locationManager: LocationManager
    private var currentLocation: Location? = null
    private var qiblaLocation: Location? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_qibla_direction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        compassArrow = requireActivity().findViewById(R.id.compassArrow)

        // Initialize sensor and location managers
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager



    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add marker for Kaaba (Qibla) location
        val kaabaLatLng = LatLng(21.4225, 39.8262)
        map.addMarker(MarkerOptions().position(kaabaLatLng).title("Kaaba (Qibla)"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(kaabaLatLng, 15f))



        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Request location updates from both GPS and remote providers
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    1f,
                    this@QiblaDirectionFragment
                )
            }
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1000,
                    1f,
                    this@QiblaDirectionFragment
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onResume() {
        super.onResume()
        mapFragment.onResume()


        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
        sensorManager.unregisterListener(this)

    }



    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            rotateCompass(azimuth)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, this)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, this)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    override fun onLocationChanged(location: Location) {
        currentLocation = location
        updateCompass()
    }

    private fun updateCompass() {
        currentLocation?.let { current ->
            qiblaLocation?.let { qibla ->
                val azimuth = current.bearingTo(qibla)
                rotateCompass(azimuth)
            }
        }
    }

    private fun rotateCompass(degree: Float) {
        val rotation = degree + 360f

        val cameraPosition = CameraPosition.Builder()
            .target(map.cameraPosition.target)
            .bearing(rotation)
            .tilt(map.cameraPosition.tilt)
            .zoom(map.cameraPosition.zoom)
            .build()

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

}