package com.dkolp.myway.presentation.fragments.content.routes

import android.Manifest
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.entities.Address
import com.dkolp.myway.core.domain.entities.Route
import com.dkolp.myway.presentation.fragments.content.save_address.AddressesViewModel
import com.dkolp.myway.presentation.fragments.content.save_address.CurrentLocationViewModel
import com.dkolp.myway.presentation.helpers.geolocationFromLatLng
import com.dkolp.myway.presentation.helpers.geolocationFromLocation
import com.dkolp.myway.presentation.helpers.getPointerIcon
import com.dkolp.myway.presentation.helpers.latLngFromLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.PolyUtil
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutesFragment : Fragment(), OnMapReadyCallback {
    private val locationVM by viewModels<CurrentLocationViewModel>()
    private val routesVM by viewModels<RoutesViewModel>()
    private val addressesVM by viewModels<AddressesViewModel>()
    private lateinit var textDestinationAddress: TextView
    private lateinit var textDistance: TextView

    private var polyline: Polyline? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askGeolocationPermissions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_routes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportMapFragment = getChildFragmentManager().findFragmentById(R.id.routes_map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        textDestinationAddress = view.findViewById(R.id.destination_address)
        textDistance = view.findViewById(R.id.route_distance)

        addressesVM.uiAddressesState.observe(viewLifecycleOwner) { onPlacesViewUpdate(it) }
    }

    override fun onMapReady(map: GoogleMap) {
        locationVM.uiCurrentLocationState.observe(viewLifecycleOwner) { onCurrentLocationViewUpdate(it, map) }
        routesVM.uiRoutesState.observe(viewLifecycleOwner) { onRoutesViewUpdate(it, map) }
        map.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.style_map) })
        map.setOnMapLongClickListener { setMarker(it, map) }
    }

    private fun askGeolocationPermissions() {
        val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            val afl = Manifest.permission.ACCESS_FINE_LOCATION
            val acl = Manifest.permission.ACCESS_COARSE_LOCATION
            if (it.getOrDefault(afl, false) || it.getOrDefault(acl, false)) {
                locationVM.getCurrentLocation()
            }
        }
        permissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun onCurrentLocationViewUpdate(uiState: CurrentLocationViewModel.UICurrentLocationState, map: GoogleMap) {
        when (uiState) {
            is CurrentLocationViewModel.UICurrentLocationState.Result -> onCurrentLocationFetched(uiState.location, map)
            is CurrentLocationViewModel.UICurrentLocationState.Error -> onError(uiState.error)
            else -> Unit
        }
    }

    private fun onCurrentLocationFetched(currentLocation: Location, map: GoogleMap) {
        map.clear()
        routesVM.origin = currentLocation.geolocationFromLocation()
        val latLng = currentLocation.latLngFromLocation()
        val options = MarkerOptions().icon(getPointerIcon(resources)).position(latLng)
        map.addMarker(options)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5F))
    }

    private fun onRoutesViewUpdate(uiState: RoutesViewModel.UIRoutesState, map: GoogleMap) {
        when (uiState) {
            is RoutesViewModel.UIRoutesState.Result -> onRouteFetched(uiState.route, map)
            is RoutesViewModel.UIRoutesState.Error -> onError(uiState.error)
            else -> Unit
        }
    }

    private fun onRouteFetched(route: Route, map: GoogleMap) {
        textDistance.text = route.getFormattedDistance()
        polyline?.remove()
        val decodedPath = PolyUtil.decode(route.points)
        val options = PolylineOptions().color(Color.GRAY)
        polyline = map.addPolyline(options.addAll(decodedPath))
    }

    private fun onError(error: String) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
    }

    private fun setMarker(latLng: LatLng, map: GoogleMap) {
        marker?.remove()
        val location = latLng.geolocationFromLatLng()
        addressesVM.findAddressByGeolocation(location)
        routesVM.getRoutes(location)
        val options = MarkerOptions().icon(getPointerIcon(resources)).position(latLng).draggable(true)
        marker = map.addMarker(options)
    }

    private fun onPlacesViewUpdate(uiState: AddressesViewModel.UIAddressesState) {
        when (uiState) {
            is AddressesViewModel.UIAddressesState.ResultOnLocation -> onAddressFetched(uiState.address)
            else -> Unit
        }
    }

    private fun onAddressFetched(address: Address) {
        textDestinationAddress.text = address.address
    }
}
