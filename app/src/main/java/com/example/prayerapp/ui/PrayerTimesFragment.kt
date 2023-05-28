package com.example.prayerapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayerapp.databinding.FragmentPrayerTimesBinding
import com.example.prayerapp.domain.models.Data
import com.example.prayerapp.utils.NetworkHelper
import com.example.prayerapp.utils.Resource
import com.example.prayerapp.viewmodels.PrayerTimesViewModel
import com.google.android.gms.location.LocationCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "PrayerTimesFragment"

@AndroidEntryPoint
class PrayerTimesFragment : Fragment() {


    private lateinit var binding: FragmentPrayerTimesBinding

    private val viewModel by viewModels<PrayerTimesViewModel>()
    private lateinit var adapter: PrayersTimeAdapter
    private var dayOfMonth:Int =0

    private var latitude = 31.2772984
    private var longitude = 30.0087258

    private var locationCallback: LocationCallback? = null
    private lateinit var locationManager: LocationManager

    private lateinit var calendar:Calendar


    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
             latitude = location.latitude
             longitude = location.longitude
            // Use the latitude and longitude values
            Log.d(TAG, "Latitude: $latitude, Longitude: $longitude")

            fetchLocationDetails(location)

            if(NetworkHelper.hasInternetConnection(requireContext())) {

                viewModel.getPrayersTimingsFromRemote(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    latitude, longitude
                )
            }else {
                viewModel.getPrayersTimingsFromDB()
            }

            // Stop requesting location updates after getting the current location
            locationManager.removeUpdates(this)
        }
    }

    private fun fetchLocationDetails(location: Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address> = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            ) as List<Address>

            if (addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val city = address.locality
                val street = address.getAddressLine(0)

                val locationString = "$city, $street"
//                locationTextView.text = locationString

//                val splitAddress = "${street.split("،")[0]} , $city"

                binding.addressTV.text = street

                Log.d(TAG, "fetchLocationDetails: $locationString")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            FragmentPrayerTimesBinding.inflate(inflater, container, false)

        checkLocationPermission()

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        calendar = Calendar.getInstance()
        adapter = PrayersTimeAdapter()



        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        startLocationUpdates()

        val currentYear = calendar.get(Calendar.YEAR)
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val monthFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val monthName = monthFormat.format(calendar.time)
        binding.tvMonth.text = monthName

        binding.tvYear.text = currentYear.toString()
        binding.tvDay.text = calendar.get(Calendar.DAY_OF_MONTH).toString()

        binding.dpCalender.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { view, year, monthOfYear, dayOfMonth ->

            if(NetworkHelper.hasInternetConnection(requireContext())) {

                viewModel.getPrayersTimingsFromRemote(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    latitude, longitude
                )
            }else {
                viewModel.getPrayersTimingsFromDB()
            }

            this.dayOfMonth = dayOfMonth
        }

        if(NetworkHelper.hasInternetConnection(requireContext())) {

            viewModel.getPrayersTimingsFromRemote(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                latitude, longitude
            )
        }else {
            viewModel.getPrayersTimingsFromDB()
        }



        binding.timingsRV.apply {
            adapter = this@PrayerTimesFragment.adapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.prayerResult.collect { result ->

                  when(result){
                      is Resource.Loading ->{
                          binding.progressBar.visibility = View.VISIBLE
                      }
                      is Resource.Success ->{

                          result.data?.let {


                              viewModel.insertPrayersTimings(it.data[dayOfMonth-1])

                              val timings = it.data[dayOfMonth-1].timings

                              val convertedTimings = viewModel.convertFromTimings(timings)

                              Log.d(TAG, "timings fajr: ${timings.fajr}")

                              adapter.updateListPrayers(convertedTimings)
                          }
                          binding.progressBar.visibility = View.GONE
                      }

                      is Resource.Error ->{
                          binding.progressBar.visibility = View.GONE
                          Toast.makeText(requireContext(), result.message.toString(), Toast.LENGTH_SHORT).show()
                      }

                      else -> {}
                  }

                }

            }
        }

        lifecycleScope.launch {
            viewModel.getPrayersTimingsDB.collect{ result->
                when(result){
                    is Resource.Loading ->{
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success ->{
                        binding.progressBar.visibility = View.GONE
                        result.data?.let {


                            val timings = it[0].timings

                            val convertedTimings = viewModel.convertFromTimings(timings)

                            Log.d(TAG, "timings fajr: ${timings.fajr}")

                            adapter.updateListPrayers(convertedTimings)


                        }
                    }

                    is Resource.Error ->{
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        binding.button.setOnClickListener {
            findNavController().navigate(PrayerTimesFragmentDirections.actionPrayerTimesFragmentToQiblaDirectionFragment())
        }

    }


    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

//            fusedLocationProvider?.requestLocationUpdates(
//                locationRequest,
//                locationCallback!!,
//                Looper.getMainLooper()
//            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000, // Update interval in milliseconds
                1f, // Minimum distance in meters
                locationListener
            )
        }
    }


    override fun onPause() {
        super.onPause()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {

//            fusedLocationProvider?.removeLocationUpdates(locationCallback!!)

            locationManager.removeUpdates(locationListener)
        }
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(requireContext())
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            checkBackgroundLocation()
        }
    }

    private fun checkBackgroundLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestBackgroundLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000, // Update interval in milliseconds
                            1f, // Minimum distance in meters
                            locationListener
                        )

                        // Now check background location
                        checkBackgroundLocation()
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()

                    // Check if we are in a state where the user has denied the permission and
                    // selected Don't ask again
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        startActivity(
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", requireActivity().packageName, null),
                            ),
                        )
                    }
                }
                return
            }

            MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000, // Update interval in milliseconds
                            1f, // Minimum distance in meters
                            locationListener
                        )

                        Toast.makeText(
                            requireContext(),
                            "Granted Background Location Permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(requireContext(), "permission denied", Toast.LENGTH_LONG).show()
                }
                return

            }
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }

}