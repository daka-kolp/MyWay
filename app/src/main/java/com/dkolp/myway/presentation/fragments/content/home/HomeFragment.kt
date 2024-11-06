package com.dkolp.myway.presentation.fragments.content.home

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.entities.Geolocation
import com.dkolp.myway.core.domain.entities.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {
    private val locationVM by viewModels<CurrentLocationViewModel>()
    private val placesVM by viewModels<PlacesViewModel>()
    private var addressTextField: AutoCompleteTextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askGeolocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val supportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        placesVM.uiPlacesState.observe(viewLifecycleOwner) { onPlacesViewUpdate(it) }

        addressTextField = view.findViewById(R.id.address_input)
        addressTextField?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) { placesVM.findPlaceByText(s.toString()) }
        })
    }

    override fun onMapReady(map: GoogleMap) {
        map.setMapStyle(context?.let { MapStyleOptions.loadRawResourceStyle(it, R.raw.style_map) })
        locationVM.uiCurrentLocationState.observe(viewLifecycleOwner) { onCurrentLocationViewUpdate(it, map) }
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
        val latLng = currentLocation.latLngFromLocation()
        val options = MarkerOptions().icon(getUserIcon()).position(latLng)
        map.addMarker(options)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5F))

        placesVM.findPlaceByGeolocation(currentLocation.geolocationFromLocation())
    }

    private fun Location.latLngFromLocation(): LatLng {
        return LatLng(latitude, longitude)
    }

    private fun getUserIcon(): BitmapDescriptor {
        val size = 124
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.location_pin)
        val marker = Bitmap.createScaledBitmap(bitmap, size, size, false)
        return BitmapDescriptorFactory.fromBitmap(marker)
    }

    private fun Location.geolocationFromLocation(): Geolocation {
        return Geolocation(latitude, longitude)
    }

    private fun onPlacesViewUpdate(uiState: PlacesViewModel.UIPlacesState) {
        when (uiState) {
            is PlacesViewModel.UIPlacesState.ResultOnSearch -> onPlacesFetched(uiState.places)
            is PlacesViewModel.UIPlacesState.ResultOnCurrentLocation -> onPlaceFetched(uiState.place)
            is PlacesViewModel.UIPlacesState.Error -> onError(uiState.error)
            else -> Unit
        }
    }

    private fun onPlacesFetched(places: List<Place>) {
        val suggestAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, places) }
        if (suggestAdapter != null) {
            suggestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addressTextField?.setAdapter(suggestAdapter)
            suggestAdapter.notifyDataSetChanged()
        }
    }

    private fun onPlaceFetched(place: Place) {
        val text = addressTextField?.editableText
        if (text != null) {
            text.clear()
            text.insert(0, place.toString())
        }
    }

    private fun onError(error: String) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
    }
}
