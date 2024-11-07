package com.dkolp.myway.presentation.fragments.content.save_address

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
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.map.entities.Geolocation
import com.dkolp.myway.core.domain.map.entities.Place
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SaveAddressFragment : Fragment(), OnMapReadyCallback {
    private val locationVM by viewModels<CurrentLocationViewModel>()
    private val placesVM by viewModels<PlacesViewModel>()
    private val saveAddressVM by viewModels<SaveAddressViewModel>()
    private var addressTextField: AutoCompleteTextView? = null
    private var saveAddressButton: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askGeolocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_save_address, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews(view)

        val supportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        placesVM.uiPlacesState.observe(viewLifecycleOwner) { onPlacesViewUpdate(it) }
        saveAddressVM.formValid.observe(viewLifecycleOwner) { saveAddressButton?.isEnabled = it }
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

    private fun setViews(view: View) {
        val backButton = view.findViewById<Button>(R.id.back_button)
        backButton.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed()}

        addressTextField = view.findViewById(R.id.address_input)
        addressTextField?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val place = saveAddressVM.place.value.toString()
                if (s.toString() != place) saveAddressVM.place.postValue(Place.nullable())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                placesVM.findPlaceByText(s.toString())
            }
        })

        val placeTypeChipGroup = view.findViewById<ChipGroup>(R.id.place_type_chip_group)
        placeTypeChipGroup.setOnCheckedStateChangeListener { _, ids ->
            if (ids.isNotEmpty()) {
                val type = placeTypeChipGroup.findViewById<Chip>(ids.first())?.text
                if (type != null) saveAddressVM.placeType.postValue(type.toString())
            }
        }

        saveAddressButton = view.findViewById(R.id.save_address_button)
        saveAddressButton?.setOnClickListener { onSaveAddressClick() }
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
            addressTextField?.setOnItemClickListener { _, _, position, _ ->
                saveAddressVM.place.postValue(places[position])
            }
            suggestAdapter.notifyDataSetChanged()
        }
    }

    private fun onPlaceFetched(place: Place) {
        val text = addressTextField?.editableText
        if (text != null) {
            text.clear()
            text.insert(0, place.toString())
        }
        saveAddressVM.place.postValue(place)
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

    private fun onError(error: String) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
    }

    private fun onSaveAddressClick() {
        val place = saveAddressVM.place.value
        val placeType = saveAddressVM.placeType.value
        Toast.makeText(context, "$place, $placeType", Toast.LENGTH_LONG).show()
    }
}
