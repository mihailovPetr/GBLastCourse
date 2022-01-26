package com.example.gblastcourse.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.example.gblastcourse.R
import com.example.gblastcourse.databinding.FragmentMapsBinding
import com.example.gblastcourse.utils.showSnackBar
import com.example.gblastcourse.model.AppState
import com.example.gblastcourse.viewmodel.MainViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.android.viewmodel.ext.android.viewModel


private const val REFRESH_PERIOD = 60000L
private const val MINIMAL_DISTANCE = 100f

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!

    private var map: GoogleMap? = null

    private val mainViewModel: MainViewModel by viewModel()

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        googleMap.setOnMapLongClickListener { latLng ->
            addMarkerToList(latLng)
        }
        googleMap.setOnMarkerClickListener() {
            onMarkerClick(it)
        }
        getLocation()
        activateMyLocation()
        initViewModel()
    }

    private fun onMarkerClick(marker: Marker): Boolean {
        activity?.let {
            val bottomSheetDialogFragment = BottomSheetDialogFragment()

            bottomSheetDialogFragment.setData(marker.position.latitude, marker.position.longitude)
            bottomSheetDialogFragment.show(
                childFragmentManager, BottomSheetDialogFragment.TAG
            )
        }

        return false
    }

    private fun addMarkerToList(location: LatLng) {
        setMarker(location = location, resourceId = R.drawable.ic_map_pin)
        mainViewModel.setMarker(
            latitude = location.latitude,
            longitude = location.longitude
        )
    }

    private fun setMarker(location: LatLng, searchText: String? = null, resourceId: Int) {
        map?.addMarker(
            MarkerOptions()
                .position(location)
                .title(searchText ?: "")
                .icon(BitmapDescriptorFactory.fromResource(resourceId))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        checkPermission()
        mapFragment?.getMapAsync(callback)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_screen_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_markers -> {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.map, MarkersFragment.newInstance())
                    .addToBackStack("")
                    .commitAllowingStateLoss()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initViewModel() {
        mainViewModel.getLiveData().observe(viewLifecycleOwner, Observer { renderData(it) })
        mainViewModel.getAllMarkers()
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {

                binding.map.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE

                val markers = appState.markers
                for (marker in markers) {
                    val location = LatLng(marker.latitude, marker.longitude)
                    setMarker(location, marker.name, R.drawable.ic_map_pin)
                }
            }
            is AppState.Loading -> {
                binding.map.visibility = View.GONE
                binding.includedLoadingLayout.loadingLayout.visibility = View.VISIBLE
            }
            is AppState.Error -> {
                binding.map.visibility = View.VISIBLE
                binding.includedLoadingLayout.loadingLayout.visibility = View.GONE
                binding.map.showSnackBar(
                    getString(R.string.error),
                    getString(R.string.reload),
                    {
                        mainViewModel.getAllMarkers()
                    })
            }
        }
    }

    private fun checkPermission() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private val onLocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            context?.let {
                val coordinates = LatLng(
                    location.latitude,
                    location.longitude
                )
                map?.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        coordinates,
                        15f
                    )
                )
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private fun activateMyLocation() {
        activity?.let { activity ->
            map?.let { map ->
                val isPermissionGranted =
                    ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) ==
                            PackageManager.PERMISSION_GRANTED
                map.isMyLocationEnabled = isPermissionGranted
                map.uiSettings.isMyLocationButtonEnabled = isPermissionGranted
            }
        }
    }

    private fun getLocation() {
        activity?.let { context ->

            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            }
        }
    }

    val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation()
            activateMyLocation()
        } else {
            showDialog(
                getString(R.string.dialog_title_no_gps),
                getString(R.string.dialog_message_no_gps)
            )
        }

    }

    private fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it).setTitle(title).setMessage(message)
                .setNegativeButton("Закрыть") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = MapsFragment()
    }
}