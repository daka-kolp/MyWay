package com.dkolp.myway.presentation.fragments.content.save_address

import android.Manifest
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
import androidx.lifecycle.ViewModelProvider
import com.dkolp.myway.R
import com.dkolp.myway.core.domain.entities.Address
import com.dkolp.myway.presentation.fragments.content.addresses.PlacesViewModel
import com.dkolp.myway.presentation.helpers.geolocationFromLocation
import com.dkolp.myway.presentation.helpers.getPointerIcon
import com.dkolp.myway.presentation.helpers.latLngFromLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavePlaceFragment : Fragment(), OnMapReadyCallback {
    private val locationVM by viewModels<CurrentLocationViewModel>()
    private val addressesVM by viewModels<AddressesViewModel>()
    private val savePlaceVM by viewModels<SavePlaceViewModel>()
    private lateinit var placesVM: PlacesViewModel
    private lateinit var addressTextField: AutoCompleteTextView
    private lateinit var savePlaceButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askGeolocationPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_save_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placesVM = ViewModelProvider(requireActivity())[PlacesViewModel::class.java]

        setViews(view)

        val supportMapFragment = getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        addressesVM.uiAddressesState.observe(viewLifecycleOwner) { onPlacesViewUpdate(it) }
        savePlaceVM.formValid.observe(viewLifecycleOwner) { savePlaceButton.isEnabled = it }
        savePlaceVM.uiSaveAddressState.observe(viewLifecycleOwner) { onAddressSaved(it) }
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
        backButton.setOnClickListener { activity?.onBackPressedDispatcher?.onBackPressed() }

        addressTextField = view.findViewById(R.id.address_input)
        addressTextField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val place = savePlaceVM.address.value.toString()
                if (s.toString() != place) savePlaceVM.address.postValue(Address.nullable())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                addressesVM.findAddressesByText(s.toString())
            }
        })

        val placeTypeChipGroup = view.findViewById<ChipGroup>(R.id.place_type_chip_group)
        placeTypeChipGroup.setOnCheckedStateChangeListener { _, ids ->
            if (ids.isNotEmpty()) {
                val type = placeTypeChipGroup.findViewById<Chip>(ids.first())?.text
                if (type != null) savePlaceVM.placeType.postValue(type.toString())
            }
        }

        savePlaceButton = view.findViewById(R.id.save_place_button)
        savePlaceButton.setOnClickListener { onSaveAddressClick() }
    }

    private fun onPlacesViewUpdate(uiState: AddressesViewModel.UIAddressesState) {
        when (uiState) {
            is AddressesViewModel.UIAddressesState.ResultOnSearch -> onAddressesFetched(uiState.addresses)
            is AddressesViewModel.UIAddressesState.ResultOnLocation -> onAddressFetched(uiState.address)
            is AddressesViewModel.UIAddressesState.Error -> onError(uiState.error)
            else -> Unit
        }
    }

    private fun onAddressesFetched(addresses: List<Address>) {
        val suggestAdapter = context?.let { ArrayAdapter(it, android.R.layout.simple_list_item_1, addresses) }
        if (suggestAdapter != null) {
            suggestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            addressTextField.setAdapter(suggestAdapter)
            addressTextField.setOnItemClickListener { _, _, position, _ ->
                savePlaceVM.address.postValue(addresses[position])
            }
            suggestAdapter.notifyDataSetChanged()
        }
    }

    private fun onAddressFetched(address: Address) {
        val text = addressTextField.editableText
        if (text != null) {
            text.clear()
            text.insert(0, address.toString())
        }
        savePlaceVM.address.postValue(address)
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
        val options = MarkerOptions().icon(getPointerIcon(resources)).position(latLng)
        map.addMarker(options)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.5F))

        addressesVM.findAddressByGeolocation(currentLocation.geolocationFromLocation())
    }

    private fun onAddressSaved(uiState: SavePlaceViewModel.UISavePlaceState) {
        when (uiState) {
            is SavePlaceViewModel.UISavePlaceState.Success -> onAddressSavedSuccessfully()
            is SavePlaceViewModel.UISavePlaceState.Error -> onError(uiState.error)
            else -> Unit
        }
    }

    private fun onAddressSavedSuccessfully() {
        Toast.makeText(context, "The place has been saved successfully", Toast.LENGTH_LONG).show()
    }

    private fun onError(error: String) {
        Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
    }

    private fun onSaveAddressClick() {
        savePlaceVM.saveAddress { newPlace ->
            placesVM.addPlace(newPlace)
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }
}
